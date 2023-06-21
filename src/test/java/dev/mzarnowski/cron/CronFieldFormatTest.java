package dev.mzarnowski.cron;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CronFieldFormatTest {
    private static final Map<FieldFormat, Integer> FIELD_POSITION = Map.of(
            CronFieldFormat.MINUTE, 0,
            CronFieldFormat.HOUR, 1,
            CronFieldFormat.DAY_OF_MONTH, 2,
            CronFieldFormat.MONTH, 3,
            CronFieldFormat.DAY_OF_WEEK, 4
    );

    @ParameterizedTest(name = "{0} - {1}")
    @ArgumentsSource(CronExpressionTestCases.class)
    public void parse_cron_field(FieldFormat format, String expression, int... expressed) {
        var parser = new FieldParser();

        var parsed = parser.parseField(format,expression);

        assertArrayEquals(expressed, parsed);
    }

    static class CronExpressionTestCases implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Arrays.stream(CronFieldFormat.values()).flatMap(this::testCases);
        }

        private Stream<Arguments> testCases(CronFieldFormat format) {
            var spec = VALUES.get(format);

            var values = Arrays.stream(spec.values())
                    .mapToObj(it -> spec(format, String.valueOf(it), it));
            var mnemonics = spec.mnemonics.entrySet().stream()
                    .map(entry -> spec(format, entry.getKey(), entry.getValue()));
            return Stream.concat(values, mnemonics);
        }

        private Arguments spec(CronFieldFormat format, String expression, int... values) {
            return Arguments.arguments(format, expression, values);
        }
    }


    record CronFieldFormatSpec(int[] values, Map<String, Integer> mnemonics) {
    }

    public static final Map<CronFieldFormat, CronFieldFormatSpec> VALUES = Map.of(
            CronFieldFormat.MINUTE, spec(range(0, 60), Map.of()),
            CronFieldFormat.HOUR, spec(range(0, 24), Map.of()),
            CronFieldFormat.DAY_OF_MONTH, spec(rangeClosed(1, 31), Map.of()),
            CronFieldFormat.DAY_OF_WEEK, spec(rangeClosed(0, 6), Map.ofEntries(
                    mnemonic("MON", 1),
                    mnemonic("TUE", 2),
                    mnemonic("WED", 3),
                    mnemonic("THU", 4),
                    mnemonic("FRI", 5),
                    mnemonic("SAT", 6),
                    mnemonic("SUN", 0))),
            CronFieldFormat.MONTH, spec(rangeClosed(1, 12), Map.ofEntries(
                    mnemonic("JAN", 1),
                    mnemonic("FEB", 2),
                    mnemonic("MAR", 3),
                    mnemonic("APR", 4),
                    mnemonic("MAY", 5),
                    mnemonic("JUN", 6),
                    mnemonic("JUL", 7),
                    mnemonic("AUG", 8),
                    mnemonic("SEP", 9),
                    mnemonic("OCT", 10),
                    mnemonic("NOV", 11),
                    mnemonic("DEC", 12)))
    );

    private static Map.Entry<String, Integer> mnemonic(String name, Integer value) {
        return new AbstractMap.SimpleImmutableEntry<>(name, value);
    }

    private static CronFieldFormatSpec spec(IntStream values, Map<String, Integer> mnemonics) {
        return new CronFieldFormatSpec(values.toArray(), mnemonics);
    }
}
