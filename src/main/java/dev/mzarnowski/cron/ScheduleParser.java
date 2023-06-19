package dev.mzarnowski.cron;

import java.util.stream.IntStream;

public class ScheduleParser {
    public static Schedule parse(String string) {
        var segments = string.split("\\h+");

        var minutes = parseMinutes(segments[0]);
        return new Schedule(segments[5], minutes);
    }

    private static int[] parseMinutes(String segment) {
        var stepOffset = segment.lastIndexOf('/');
        var step = stepOffset < 0 ? 1 : parse(segment, stepOffset + 1, segment.length());

        if (segment.startsWith("*")) {
            if ((stepOffset < 0 && segment.length() == 1) || stepOffset == 1)
                return IntStream.iterate(0, (it) -> it < 60, (it) -> it + step).toArray();
            else throw new ParseException("Invalid step pattern: " + segment);
        }

        var rangeOffset = segment.indexOf('-', 1);

        var start = parse(segment, 0, rangeOffset < 0 ? stepOffset : rangeOffset);
        int end = parseRangeOffset(segment, stepOffset, rangeOffset, start);
        return IntStream.iterate(start, (it) -> it < end, (it) -> it + step).toArray();
    }

    private static int parseRangeOffset(String segment, int stepOffset, int range, int start) {
        if (0 <= range) {
            return parse(segment, range + 1, stepOffset);
        } else if (0 <= stepOffset) {
            return 59;
        } else {
            return start;
        }
    }

    private static int parse(String segment, int offset, int limit) {
        try {
            var value = Integer.parseInt(segment, offset, limit < 0 ? segment.length() : limit, 10);
            if (0 <= value && value < 60) {
                return value;
            } else {
                var error = String.format("value %s out of minute bounds: [%s, %s)", value, 0, 60);
                throw new ParseException(error);
            }
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }
}
