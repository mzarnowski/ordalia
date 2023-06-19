package dev.mzarnowski.cron;

import java.util.stream.IntStream;

public class ScheduleParser {
    public static Schedule parse(String string) {
        var segments = string.split("\\h+");

        var minutes = parseMinutes(segments[0]);
        return new Schedule(segments[5], minutes);
    }

    private static int[] parseMinutes(String segment) {
        if (segment.equals("*")) {
            return IntStream.range(0, 60).toArray();
        }

        var range = segment.indexOf('-', 1);

        var start = parse(segment, 0, range);
        var end = parse(segment, range + 1, segment.length());
        return IntStream.range(start, end + 1).toArray();
    }

    private static int parse(String segment, int offset, int limit) {
        var value = Integer.parseInt(segment, offset, limit < 0 ? segment.length() : limit, 10);
        if (0 <= value && value < 60) {
            return value;
        } else {
            var error = String.format("value %s out of minute bounds: [%s, %s)", value, 0, 60);
            throw new ParseException(error);
        }
    }

}
