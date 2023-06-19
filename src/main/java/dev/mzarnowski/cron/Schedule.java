package dev.mzarnowski.cron;


import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;

public record Schedule(String command, Map<ChronoField, int[]> components) {
    public IntStream minutes(){
        return stream(ChronoField.MINUTE_OF_HOUR);
    }
    public IntStream hours(){
        return stream(ChronoField.HOUR_OF_DAY);
    }

    public IntStream daysOfMonth() {
        return stream(ChronoField.DAY_OF_MONTH);
    }

    public IntStream daysOfWeek() {
        return stream(ChronoField.DAY_OF_WEEK);
    }

    public IntStream months(){
        return stream(ChronoField.MONTH_OF_YEAR);
    }

    private IntStream stream(ChronoField field) {
        return Arrays.stream(components.getOrDefault(field, new int[0]));
    }

}
