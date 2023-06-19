package dev.mzarnowski.cron;

import java.text.StringCharacterIterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class ScheduleParser {
    public static Schedule parse(String string) {
        var segments = string.split("\\h+");

        var minutes = parseMinutes(segments[0]);
        return new Schedule(segments[5], minutes);
    }

    private static int[] parseMinutes(String segment) {
        var iterator = new StringCharacterIterator(segment);
        var tokenizer = new Tokenizer(iterator);

        if (tokenizer.skip('*')) {
            return parseWildcard(tokenizer);
        }

        var start = tokenizer.number();

        var end = -1;
        if (tokenizer.skip('-')) {
            end = tokenizer.number();
        }

        var step = -1;
        if (tokenizer.skip('/')) {
            step = tokenizer.number();
            if (end == -1) end = 59;
        }

        if (end == -1) end = start;
        if (step == -1) step = 1;

        if (!tokenizer.eol()) {
            throw new ParseException("Could not parse fully: " + segment);
        }

        if (start < 0 || start > 59) {
            throw new ParseException("Invalid value: " + step);
        }

        if (end < 0 || end > 59) {
            throw new ParseException("Invalid value: " + step);
        }

        if (end < start) {
            throw new ParseException(String.format("Invalid range: %d < %d", start, end));
        }

        return enumerate(start, end + 1, step);
    }

    private static int[] parseWildcard(Tokenizer tokenizer) {
        if (tokenizer.eol()) return enumerate(0, 60, 1);
        else if (tokenizer.skip('/')) {
            var token = tokenizer.takeWhile(Character::isDigit);
            if (token == null) throw new ParseException("Missing step specification");
            return enumerate(0, 60, Integer.parseInt(token));
        } else {
            throw new ParseException("Unsupported wildcard: [%s] != */\\d+");
        }
    }

    private static int[] enumerate(int start, int end, int step) {
        if (step == 1) return IntStream.range(start, end).toArray();
        return IntStream.iterate(start, (it) -> it < end, (it) -> it + step).toArray();
    }
}
