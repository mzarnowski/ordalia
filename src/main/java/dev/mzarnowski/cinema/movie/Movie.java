package dev.mzarnowski.cinema.movie;

import java.time.Duration;
import java.util.List;

public record Movie(Id id, Duration duration, List<MovieProvision> provisions) {
    public record Id(String value) {}
}