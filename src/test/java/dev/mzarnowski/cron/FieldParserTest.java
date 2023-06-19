package dev.mzarnowski.cron;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static dev.mzarnowski.cron.ScheduleParser.parseField;
import static java.util.function.Function.identity;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class FieldParserTest {
    public static final FieldFormat RANGE_FORMAT = FieldFormat.of(4, 4);

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(FieldFormatSpec.class)
    public void parses_valid_expression(String expression, int[] expected) {
        var parsed = parseField(RANGE_FORMAT, expression);

        Assertions.assertArrayEquals(expected, parsed);
    }

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(InvalidFieldExpressionExamples.class)
    public void does_not_parse_single_value_out_of_bounds(String expression) {
        Assertions.assertThrows(ParseException.class, () -> parseField(RANGE_FORMAT, expression));
    }

    static class FieldFormatSpec implements ArgumentsProvider {
        private Arguments spec(String expression, int... expected) {
            return arguments(expression, expected);
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            Stream<Stream<Arguments>> specs = Stream.of(
                    singleValues(), offsetIntervals(),
                    valueRanges(), rangeIntervals(),
                    wildcard(), wildcardIntervals());

            return specs.flatMap(identity());
        }

        private Stream<Arguments> singleValues() {
            return Stream.of(
                    spec("4", 4),
                    spec("5", 5),
                    spec("6", 6),
                    spec("7", 7)
            );
        }

        private Stream<Arguments> valueRanges() {
            return Stream.of(
                    spec("4-5", 4, 5),
                    spec("4-7", 4, 5, 6, 7),
                    spec("6-6", 6)
            );
        }

        private Stream<Arguments> wildcard() {
            return Stream.of(
                    spec("*", 4, 5, 6, 7)
            );
        }


        private Stream<Arguments> offsetIntervals() {
            return Stream.of(
                    spec("4/1", 4, 5, 6, 7),
                    spec("4/2", 4, 6),
                    spec("4/3", 4, 7),
                    spec("4/4", 4)
            );
        }

        private Stream<Arguments> rangeIntervals() {
            return Stream.of(
                    spec("4-6/1", 4, 5, 6),
                    spec("4-6/2", 4, 6),
                    spec("4-6/3", 4),
                    spec("4/4", 4)
            );
        }

        private Stream<Arguments> wildcardIntervals() {
            return Stream.of(
                    spec("*/1", 4, 5, 6, 7),
                    spec("*/2", 4, 6),
                    spec("*/3", 4, 7),
                    spec("*/4", 4)
            );
        }
    }

    static class InvalidFieldExpressionExamples implements ArgumentsProvider {
        private Stream<Arguments> examples(String... examples) {
            return Arrays.stream(examples).map(Arguments::of);
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            var streams = Stream.of(
                    invalidValues(), invalidRanges(), invalidWildcards(),
                    invalidValueIntervals(), invalidRangeIntervals());

            return streams.flatMap(identity());
        }

        private Stream<Arguments> invalidValues() {
            return examples("1", "-1", "3", "8", "100");
        }

        private Stream<Arguments> invalidRanges() {
            return examples(
                    "4-8", "3-7", "1-3", "1-5", "5-10", "9-10",
                    "5-", "-6", "-", "4a-5b", "a5-a6"
            );
        }

        private Stream<Arguments> invalidWildcards() {
            return examples(
                    "*5", "5*", "*a", "*-a", "a-*",
                    "*-5", "5-*", "*/a"
            );
        }

        private Stream<Arguments> invalidValueIntervals() {
            return examples("4/*", "4/", "4/a", "a/a", "5/a");
        }

        private Stream<Arguments> invalidRangeIntervals() {
            return examples("4-5/", "4-5/a", "4-5/*");
        }

    }
}
