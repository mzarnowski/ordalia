package dev.mzarnowski.cinema.room;

import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.OperatingHours;
import dev.mzarnowski.cinema.screening.Policy;

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
    // embedding the operating hours here means these are the same every day
    private final OperatingHours operatingHours;
    private final Duration cleaningTime;

    private final Map<LocalDate, List<TimeSlot>> showings = new ConcurrentHashMap<>();

    public Room(Id id, OperatingHours operatingHours, Duration cleaningTime) {
        this.id = id;
        this.operatingHours = operatingHours;
        this.cleaningTime = cleaningTime;
    }

    public Optional<Policy.Veto> schedule(Movie movie, ZonedDateTime start) {
        var end = start.plus(movie.duration());

        if (!start.toLocalDate().equals(end.toLocalDate())) {
            throw new IllegalArgumentException("");
        }

        if (!operatingHours.contains(start) || !operatingHours.contains(end)) {
            return Optional.of(new OutsideOperatingHours(operatingHours));
        }

        if (claimTimeSlot(start, end.plus(cleaningTime))) {
            return Optional.empty();
        } else {
            return Optional.of(new SlotAlreadyTaken());
        }
    }

    private synchronized boolean claimTimeSlot(ZonedDateTime start, ZonedDateTime end) {
        var showings = this.showings.computeIfAbsent(start.toLocalDate(), date -> new ArrayList<>());

        for (var block : showings) {
            if (!(block.start().isAfter(end) || block.end().isBefore(start))) {
                return false;
            }
        }

        return showings.add(new TimeSlot(start, end));
    }

    public Id id() {
        return id;
    }

    public record Id(String value) {}

    public record OutsideOperatingHours(OperatingHours hours) implements Policy.Veto {}

    public record SlotAlreadyTaken() implements Policy.Veto {}
}
