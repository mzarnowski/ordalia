package dev.mzarnowski.cron;

import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class FieldParser {
    int[] parseField(FieldFormat format, String segment) {
        return Arrays.stream(segment.split(","))
                .flatMapToInt(it -> parseValues(format, it))
                .distinct()
                .sorted()
                .toArray();
    }

    private IntStream parseValues(FieldFormat format, String segment) {
        var iterator = new StringCharacterIterator(segment);
        var tokenizer = new Tokenizer(iterator);

        if (tokenizer.skip('*')) {
            return parseWildcard(format, tokenizer);
        }

        var first = parseValue(format, tokenizer);

        var last = -1;
        if (tokenizer.skip('-')) {
            last = parseValue(format, tokenizer);
        }

        var interval = -1;
        if (tokenizer.skip('/')) {
            interval = tokenizer.number();
            if (last == -1) last = format.max();
        }

        if (last == -1) last = first;
        if (interval == -1) interval = 1;

        if (!tokenizer.eol()) {
            throw new ParseException("Could not parse fully: " + segment);
        }

        if (first < format.min() || first > format.max()) {
            throw new ParseException("Invalid value: " + interval);
        }

        if (last < format.min() || last > format.max()) {
            throw new ParseException("Invalid value: " + interval);
        }

        if (last < first) {
            throw new ParseException(String.format("Invalid range: %d < %d", first, last));
        }

        return enumerate(first, last, interval);
    }

    private static int parseValue(FieldFormat format, Tokenizer tokenizer) {
        return Stream.<Parser>of(() -> parseInteger(tokenizer), () -> parseMnemonic(format, tokenizer))
                .flatMapToInt(it -> it.get().stream())
                .findFirst()
                .orElseThrow(() -> new ParseException("Value not present"));
    }

    private static OptionalInt parseInteger(Tokenizer tokenizer) {
        var token = tokenizer.takeWhile(Character::isDigit);
        if (token == null) return OptionalInt.empty();

        try {
            int value = Integer.parseInt(token);
            return OptionalInt.of(value);
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }

    private static OptionalInt parseMnemonic(FieldFormat format, Tokenizer tokenizer) {
        var token = tokenizer.takeWhile(Character::isAlphabetic);
        if (token == null) return OptionalInt.empty();

        var mnemonics = format.mnemonics();
        for (int i = 0; i < mnemonics.size(); i++) {
            if (token.equalsIgnoreCase(mnemonics.get(i))) {
                return OptionalInt.of(format.min() + i);
            }
        }

        throw new ParseException("Invalid token: " + token);
    }

    private static IntStream parseWildcard(FieldFormat format, Tokenizer tokenizer) {
        if (tokenizer.eol()) return enumerate(format.min(), format.max(), 1);
        else if (tokenizer.skip('/')) {
            var interval = tokenizer.number();
            return enumerate(format.min(), format.max(), interval);
        } else {
            throw new ParseException("Unsupported wildcard: [%s] != */\\d+");
        }
    }

    private static IntStream enumerate(int start, int end, int step) {
        if (step == 1) return IntStream.range(start, end + 1);
        return IntStream.iterate(start, (it) -> it < end + 1, (it) -> it + step);
    }

    @FunctionalInterface
    private interface Parser extends Supplier<OptionalInt> {
    }
}
