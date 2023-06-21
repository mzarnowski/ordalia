package dev.mzarnowski.cron;

import dev.mzarnowski.cron.Schedule.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.mzarnowski.cron.Schedule.Component.*;

class TableScheduleFormatter {
    private static final String LINE_TEMPLATE = "%-14s%s";
    private static final List<Component> COMPONENT_ORDER = List.of(
            MINUTE_OF_HOUR, HOUR_OF_DAY, DAY_OF_MONTH, MONTH_OF_YEAR, DAY_OF_WEEK
    );

    static String format(Schedule schedule) {
        var components = COMPONENT_ORDER.stream().map(it -> formatLine(it, schedule));

        var command = line("command", schedule.command());

        return Stream.concat(components, Stream.of(command))
                .collect(Collectors.joining("\n"));
    }

    private static String formatLine(Component component, Schedule schedule) {
        var valueString = Arrays.stream(schedule.components().get(component))
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(" "));
        return line(format(component), valueString);
    }

    private static String format(Component component) {
        return switch (component) {
            case MINUTE_OF_HOUR -> "minute";
            case HOUR_OF_DAY -> "hour";
            case MONTH_OF_YEAR -> "month";
            case DAY_OF_WEEK -> "day of week";
            case DAY_OF_MONTH -> "day of month";
        };
    }

    private static String line(String name, String value) {
        return String.format(LINE_TEMPLATE, name, value);
    }
}
