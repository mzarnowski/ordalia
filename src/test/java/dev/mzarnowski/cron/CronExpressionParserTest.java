package dev.mzarnowski.cron;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static dev.mzarnowski.cron.CronFieldFormat.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CronExpressionParserTest {

    @Test
    public void parses_command() {
        var fieldParser = Mockito.mock(FieldParser.class);
        Mockito.when(fieldParser.parseField(any(), any())).thenReturn(new int[0]);

        var parser = new CronExpressionParser(fieldParser);
        var expression = "minute hour dayofmonth month dayofweek foo bar baz";

        var schedule = parser.parse(expression);

        Assertions.assertEquals("foo bar baz", schedule.command());
        Mockito.verify(fieldParser, times(1)).parseField(MINUTE, "minute");
        Mockito.verify(fieldParser, times(1)).parseField(HOUR, "hour");
        Mockito.verify(fieldParser, times(1)).parseField(DAY_OF_MONTH, "dayofmonth");
        Mockito.verify(fieldParser, times(1)).parseField(MONTH, "month");
        Mockito.verify(fieldParser, times(1)).parseField(DAY_OF_WEEK, "dayofweek");
        verifyNoMoreInteractions(fieldParser);
    }
}