package dev.mzarnowski.cinema;

import java.time.LocalTime;

// small functions to make the tests look nicer
public final class TestParsers {
    public static OperatingHours operatingHours(String since, String until) {
        var sinceTime = LocalTime.parse(since);
        var untilTime = LocalTime.parse(until);
        return new OperatingHours(sinceTime, untilTime);
    }

    public static LocalTime time(String value) {
        return LocalTime.parse(value);
    }
}
