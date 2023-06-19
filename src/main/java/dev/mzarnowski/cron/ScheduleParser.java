package dev.mzarnowski.cron;

import java.text.StringCharacterIterator;
import java.util.Map;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoField.*;

public class ScheduleParser {
    public static Schedule parse(String string) {
        var segments = string.split("\\h+");

        return new Schedule(segments[5], Map.of(
                MINUTE_OF_HOUR, parseField(FieldFormat.MINUTE, segments[0]),
                HOUR_OF_DAY, parseField(FieldFormat.HOUR, segments[1]),
                DAY_OF_MONTH, parseField(FieldFormat.DAY_OF_MONTH, segments[2]),
                MONTH_OF_YEAR, parseField(FieldFormat.MONTH, segments[3]),
                DAY_OF_WEEK, parseField(FieldFormat.DAY_OF_WEEK, segments[4])
        ));
    }

    static int[] parseField(FieldFormat format, String segment) {
        var iterator = new StringCharacterIterator(segment);
        var tokenizer = new Tokenizer(iterator);

        if (tokenizer.skip('*')) {
            return parseWildcard(format, tokenizer);
        }

        var first = tokenizer.number();

        var last = -1;
        if (tokenizer.skip('-')) {
            last = tokenizer.number();
        }

        var interval = -1;
        if (tokenizer.skip('/')) {
            interval = tokenizer.number();
            if (last == -1) last = format.max();
        }

        if (last == -1) last = first;
        if (interval == -1) interval = 1;

        if (!tokenizer.eol()) {
            throw new ParseException("Could not parse fully: " + segment);
        }

        if (first < format.min() || first > format.max()) {
            throw new ParseException("Invalid value: " + interval);
        }

        if (last < format.min() || last > format.max()) {
            throw new ParseException("Invalid value: " + interval);
        }

        if (last < first) {
            throw new ParseException(String.format("Invalid range: %d < %d", first, last));
        }

        return enumerate(first, last, interval);
    }

    private static int[] parseWildcard(FieldFormat format, Tokenizer tokenizer) {
        if (tokenizer.eol()) return enumerate(format.min(), format.max(), 1);
        else if (tokenizer.skip('/')) {
            var interval = tokenizer.number();
            return enumerate(format.min(), format.max(), interval);
        } else {
            throw new ParseException("Unsupported wildcard: [%s] != */\\d+");
        }
    }

    private static int[] enumerate(int start, int end, int step) {
        if (step == 1) return IntStream.range(start, end + 1).toArray();
        return IntStream.iterate(start, (it) -> it < end + 1, (it) -> it + step).toArray();
    }
}
