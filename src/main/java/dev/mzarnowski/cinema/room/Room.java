package dev.mzarnowski.cinema.room;

import dev.mzarnowski.cinema.Event;
import dev.mzarnowski.cinema.OperatingHours;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public final class Room {
    private final Id id;
    // embedding the operating hours here, means these are the same every day
    private final OperatingHours operatingHours;

    private final Map<LocalDate, Set<LocalTime>> showings = new ConcurrentHashMap<>();

    public Room(Id id, OperatingHours operatingHours) {
        this.id = id;
        this.operatingHours = operatingHours;
    }

    public List<Event> schedule(ZonedDateTime start, Duration duration) {
        if (!operatingHours.contains(start) || !operatingHours.contains(start.plus(duration))) {
            return List.of(new RejectedSchedulingOutsideOperatingHours(id, start, operatingHours));
        }

        var showings = this.showings.computeIfAbsent(start.toLocalDate(), date -> new ConcurrentSkipListSet<>());
        if (showings.add(start.toLocalTime())){
            return List.of(new MovieScheduled(id, start));
        } else {
            return List.of(new RejectedOverlappingSchedule(start));
        }
    }

    public record Id(String value) {}

    public record MovieScheduled(Id id, ZonedDateTime start) implements Event {}

    public record RejectedSchedulingOutsideOperatingHours(Id id, ZonedDateTime time, OperatingHours hours) implements Event {}

    public record RejectedOverlappingSchedule(ZonedDateTime startTime) implements Event {}
}
