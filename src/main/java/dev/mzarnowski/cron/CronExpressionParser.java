package dev.mzarnowski.cron;

import dev.mzarnowski.cron.Schedule.Component;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.mzarnowski.cron.Schedule.Component.*;

final class CronExpressionParser {
    private final FieldParser parser;

    static CronExpressionParser create() {
        return new CronExpressionParser(new FieldParser());
    }

    CronExpressionParser(FieldParser parser) {
        this.parser = parser;
    }

    Schedule parse(String expression) {
        var segments = expression.split("\\h+");

        return new Schedule(parseCommand(segments), parseComponents(segments));
    }

    private String parseCommand(String[] segments) {
        return Arrays.stream(segments).skip(5).collect(Collectors.joining(" "));
    }

    private Map<Component, int[]> parseComponents(String[] segments) {
        return Map.ofEntries(
                parseComponent(MINUTE_OF_HOUR, segments[0]),
                parseComponent(HOUR_OF_DAY, segments[1]),
                parseComponent(DAY_OF_MONTH, segments[2]),
                parseComponent(MONTH_OF_YEAR, segments[3]),
                parseComponent(DAY_OF_WEEK, segments[4])
        );
    }

    private Map.Entry<Component, int[]> parseComponent(Component component, String segment) {
        var values = parser.parseField(component.format, segment);
        return new AbstractMap.SimpleImmutableEntry<>(component, values);
    }
}
