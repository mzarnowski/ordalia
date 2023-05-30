package dev.mzarnowski.cinema;

import java.time.Duration;

public record Movie(Id id, Duration duration) {
    public record Id(String value) {}
}
