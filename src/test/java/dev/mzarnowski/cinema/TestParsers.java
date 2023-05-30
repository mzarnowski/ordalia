package dev.mzarnowski.cinema;

import java.time.LocalTime;

// small functions to make the tests look nicer
public final class TestParsers {
    public static LocalTime time(String value) {
        return LocalTime.parse(value);
    }
}
