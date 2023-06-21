package dev.mzarnowski.cron;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableScheduleFormatterTest {
    @Test
    public void formats_schedule(){
        var expression = "*/15 0 1,15 * 1-5 /usr/bin/find";
        var expected = """
                minute        0 15 30 45
                hour          0
                day of month  1 15
                month         1 2 3 4 5 6 7 8 9 10 11 12
                day of week   1 2 3 4 5
                command       /usr/bin/find""";

        var schedule = Schedule.parse(expression);
        var formatted = TableScheduleFormatter.format(schedule);

        assertEquals(expected, formatted);
    }
}