package dev.mzarnowski.cron;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static dev.mzarnowski.cron.ScheduleParser.parse;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleParserTest {
    @Test
    public void parses_command(){
        var string = "*/15 0 1,15 * 1-5 /usr/bin/find";

        var schedule = parse(string);

        assertEquals("/usr/bin/find", schedule.command());
        assertArrayEquals(new int[]{0, 15, 30, 45}, schedule.minutes());
    }

    @Test
    void parse_minute_wildcard() {
        var string = "* * * * * /usr/bin/find";

        var schedule = parse(string);

        assertArrayEquals(IntStream.range(0, 60).toArray(), schedule.minutes());
    }

    @Test
    void parse_minute_value() {
        var string = "21 * * * * /usr/bin/find";

        var schedule = parse(string);

        assertArrayEquals(new int[] {21}, schedule.minutes());
    }

    @Test
    void cannot_parse_invalid_minutes() {
        assertThrows(ParseException.class, () -> parse("-1 * * * * foo"));
        assertThrows(ParseException.class, () -> parse("60 * * * * foo"));
    }
}