package dev.mzarnowski.cinema.room;

import dev.mzarnowski.cinema.Movie;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class Room {
    private final Id id;
    private final Duration cleaningTime;

    private final Map<LocalDate, List<TimeSlot>> showings = new ConcurrentHashMap<>();

    public Room(Id id, Duration cleaningTime) {
        this.id = id;
        this.cleaningTime = cleaningTime;
    }

    public Optional<TimeSlot> schedule(Movie movie, ZonedDateTime start) {
        var end = start.plus(movie.duration()).plus(cleaningTime);

        var showings = this.showings.computeIfAbsent(start.toLocalDate(), date -> new ArrayList<>());

        synchronized (this) {
            for (var block : showings) {
                if (!(block.start().isAfter(end) || block.end().isBefore(start))) {
                    return Optional.empty();
                }
            }

            TimeSlot slot = new TimeSlot(start, end);
            showings.add(slot);
            return Optional.of(slot);
        }
    }

    public Id id() {
        return id;
    }

    public record Id(String value) {}
}
