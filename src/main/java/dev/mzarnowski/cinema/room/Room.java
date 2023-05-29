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
    private final Duration cleaningTime;

    private final Map<LocalDate, Set<LocalTime>> showings = new ConcurrentHashMap<>();

    public Room(Id id, OperatingHours operatingHours, Duration cleaningTime) {
        this.id = id;
        this.operatingHours = operatingHours;
        this.cleaningTime = cleaningTime;
    }

    public List<Event> schedule(ZonedDateTime start, Duration duration) {
        if (!operatingHours.contains(start) || !operatingHours.contains(start.plus(duration))) {
            return List.of(new MovieScheduleRejected(id, start, duration, new OutsideOperatingHours(operatingHours)));
        }

        var showings = this.showings.computeIfAbsent(start.toLocalDate(), date -> new ConcurrentSkipListSet<>());
        if (showings.add(start.toLocalTime())){
            return List.of(new MovieScheduled(id, start, duration));
        } else {
            return List.of(new MovieScheduleRejected(id, start, duration, new SlotAlreadyTaken()));
        }
    }

    public record Id(String value) {}

    public record MovieScheduled(Id id, ZonedDateTime start, Duration duration) implements Event {}

    public record MovieScheduleRejected(Id id, ZonedDateTime start, Duration duration, Reason reason) implements Event {}

    public sealed interface Reason {}

    public record OutsideOperatingHours(OperatingHours hours) implements Reason {}

    public record SlotAlreadyTaken() implements Reason {}
}
