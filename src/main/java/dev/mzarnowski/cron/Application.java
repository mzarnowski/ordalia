package dev.mzarnowski.cron;

public class Application {
    public static void main(String[] args) {
        var expression = expression(args);
        var schedule = Schedule.parse(expression);
        var formatted = TableScheduleFormatter.format(schedule);
        System.out.println(formatted);
    }

    private static String expression(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected a single argument");
        }

        return args[0];
    }
}
