package dev.mzarnowski.cron;

import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CronExpressionParserTest {

    @Test
    public void parses_command() {
        var expression = "*/15 0 1 * 1-5 /usr/bin/find";

        var parser = CronExpressionParser.create();
        var schedule = parser.parse(expression);

        assertEquals("/usr/bin/find", schedule.command());
        assertArrayEquals(new int[]{0, 15, 30, 45}, schedule.minutes().toArray());
        assertArrayEquals(new int[]{0}, schedule.hours().toArray());
        assertArrayEquals(new int[]{1}, schedule.daysOfMonth().toArray());
        assertArrayEquals(rangeClosed(1, 12).toArray(), schedule.months().toArray());
        assertArrayEquals(rangeClosed(1, 5).toArray(), schedule.daysOfWeek().toArray());
    }
}