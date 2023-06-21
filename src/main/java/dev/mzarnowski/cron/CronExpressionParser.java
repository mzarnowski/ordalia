package dev.mzarnowski.cron;

import java.util.Map;

import static java.time.temporal.ChronoField.*;

public class CronExpressionParser {
    private final FieldParser parser;

    public static CronExpressionParser create(){
        return new CronExpressionParser(new FieldParser());
    }

    public CronExpressionParser(FieldParser parser) {
        this.parser = parser;
    }

    public Schedule parse(String expression) {
        var segments = expression.split("\\h+");

        return new Schedule(segments[5], Map.of(
                MINUTE_OF_HOUR, parser.parseField(CronFieldFormat.MINUTE, segments[0]),
                HOUR_OF_DAY, parser.parseField(CronFieldFormat.HOUR, segments[1]),
                DAY_OF_MONTH, parser.parseField(CronFieldFormat.DAY_OF_MONTH, segments[2]),
                MONTH_OF_YEAR, parser.parseField(CronFieldFormat.MONTH, segments[3]),
                DAY_OF_WEEK, parser.parseField(CronFieldFormat.DAY_OF_WEEK, segments[4])
        ));
    }
}
