package dev.mzarnowski.cinema.room;

import dev.mzarnowski.cinema.Event;
import dev.mzarnowski.cinema.OperatingHours;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

public final class Room {
    private final Id id;
    // embedding the operating hours here, means these are the same every day
    private final OperatingHours operatingHours;

    private final Map<LocalDate, Set<LocalTime>> showings = new HashMap<>();

    public Room(Id id, OperatingHours operatingHours) {
        this.id = id;
        this.operatingHours = operatingHours;
    }

    public List<Event> schedule(ZonedDateTime start) {
        if (!operatingHours.contains(start)) {
            return List.of(new RejectedSchedulingOutsideOperatingHours(id, start, operatingHours));
        }

        var showings = this.showings.computeIfAbsent(start.toLocalDate(), date -> new HashSet<>());
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
