package dev.mzarnowski.cron;

public class ScheduleParser {
    public static Schedule parse(String string) {
        var segments = string.split("\\h+");

        return new Schedule(segments[5]);
    }


}
