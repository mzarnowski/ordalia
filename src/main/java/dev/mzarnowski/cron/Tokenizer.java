package dev.mzarnowski.cron;

import java.text.CharacterIterator;
import java.util.function.IntPredicate;

class Tokenizer {
    private final CharacterIterator iterator;

    Tokenizer(CharacterIterator iterator) {
        this.iterator = iterator;
    }

    boolean skip(char value) {
        if (iterator.current() == value) {
            iterator.next();
            return true;
        }

        return false;
    }

    boolean eol() {
        return iterator.current() == CharacterIterator.DONE;
    }

    int number() {
        var token = takeWhile(Character::isDigit);
        if (token == null) throw new ParseException("Expected digit, got " + iterator.current());
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new ParseException(e);
        }
    }

    String takeWhile(IntPredicate predicate) {
        var c = iterator.current();
        var token = new StringBuilder();
        while (predicate.test(c)) {
            token.append(c);
            c = iterator.next();
        }

        if (token.isEmpty()) return null;
        return token.toString();
    }
}
