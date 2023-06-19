package dev.mzarnowski.cron;

import java.text.StringCharacterIterator;
import java.util.Map;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoField.*;

public class ScheduleParser {
    public static Schedule parse(String string) {
        var segments = string.split("\\h+");

        return new Schedule(segments[5], Map.of(
                MINUTE_OF_HOUR, parse(0, 59, segments[0]),
                HOUR_OF_DAY, parse(0, 23, segments[1]),
                DAY_OF_MONTH, parse(1, 31, segments[2]),
                MONTH_OF_YEAR, parse(1, 12, segments[3]),
                DAY_OF_WEEK, parse(0, 23, segments[4])
        ));
    }

    private static int[] parse(int min, int max, String segment) {
        var iterator = new StringCharacterIterator(segment);
        var tokenizer = new Tokenizer(iterator);

        if (tokenizer.skip('*')) {
            return parseWildcard(min, max, tokenizer);
        }

        var start = tokenizer.number();

        var end = -1;
        if (tokenizer.skip('-')) {
            end = tokenizer.number();
        }

        var step = -1;
        if (tokenizer.skip('/')) {
            step = tokenizer.number();
            if (end == -1) end = max;
        }

        if (end == -1) end = start;
        if (step == -1) step = 1;

        if (!tokenizer.eol()) {
            throw new ParseException("Could not parse fully: " + segment);
        }

        if (start < min || start > max) {
            throw new ParseException("Invalid value: " + step);
        }

        if (end < min || end > max) {
            throw new ParseException("Invalid value: " + step);
        }

        if (end < start) {
            throw new ParseException(String.format("Invalid range: %d < %d", start, end));
        }

        return enumerate(start, end, step);
    }

    private static int[] parseWildcard(int min, int max, Tokenizer tokenizer) {
        if (tokenizer.eol()) return enumerate(min, max, 1);
        else if (tokenizer.skip('/')) {
            var step = tokenizer.number();
            return enumerate(min, max, step);
        } else {
            throw new ParseException("Unsupported wildcard: [%s] != */\\d+");
        }
    }

    private static int[] enumerate(int start, int end, int step) {
        if (step == 1) return IntStream.range(start, end + 1).toArray();
        return IntStream.iterate(start, (it) -> it < end + 1, (it) -> it + step).toArray();
    }
}
