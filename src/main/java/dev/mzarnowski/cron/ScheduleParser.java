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

        var value = Integer.parseInt(segment, 10);
        if (0 <= value && value < 60) {
            return new int[]{value};
        } else {
            var error = String.format("value %s out of minute bounds: [%s, %s)", value, 0, 60);
            throw new ParseException(error);
        }
    }

}
