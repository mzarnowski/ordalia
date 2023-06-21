package dev.mzarnowski.cron;


import java.util.Map;

public record Schedule(String command, Map<Component, int[]> components) {
    public static Schedule parse(String expression) {
        var parser = CronExpressionParser.create();
        return parser.parse(expression);
    }

    public enum Component {
        MINUTE_OF_HOUR(CronFieldFormat.MINUTE_OF_HOUR),
        HOUR_OF_DAY(CronFieldFormat.HOUR_OF_DAY),
        DAY_OF_WEEK(CronFieldFormat.DAY_OF_WEEK),
        DAY_OF_MONTH(CronFieldFormat.DAY_OF_MONTH),
        MONTH_OF_YEAR(CronFieldFormat.MONTH_OF_YEAR);
        final CronFieldFormat format;

        Component(CronFieldFormat format) {
            this.format = format;
        }
    }
}
