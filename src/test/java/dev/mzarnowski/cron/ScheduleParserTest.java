package dev.mzarnowski.cron;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleParserTest {
    @Test
    public void parses_command(){
        var string = "*/15 0 1,15 * 1-5 /usr/bin/find";

        var parsed = ScheduleParser.parse(string);

        assertEquals("/usr/bin/find", parsed.command());
    }
}