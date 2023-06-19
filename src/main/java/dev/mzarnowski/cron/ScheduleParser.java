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
        return new int[0];
    }
}
