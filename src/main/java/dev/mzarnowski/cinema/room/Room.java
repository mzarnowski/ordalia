package dev.mzarnowski.cinema.room;

import dev.mzarnowski.cinema.Event;
import dev.mzarnowski.cinema.OperatingHours;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public List<Event> schedule(ZonedDateTime start, Duration duration) {
        var end = start.plus(duration);

        if (!start.toLocalDate().equals(end.toLocalDate())) {
            throw new IllegalArgumentException("");
        }

        if (!operatingHours.contains(start) || !operatingHours.contains(end)) {
            return List.of(new MovieScheduleRejected(id, start, duration, new OutsideOperatingHours(operatingHours)));
        }

        if (claimTimeSlot(start, end.plus(cleaningTime))) {
            return List.of(new MovieScheduled(id, start, duration));
        } else {
            return List.of(new MovieScheduleRejected(id, start, duration, new SlotAlreadyTaken()));
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

    public record Id(String value) {}

    public record MovieScheduled(Id id, ZonedDateTime start, Duration duration) implements Event {}

    public record MovieScheduleRejected(Id id, ZonedDateTime start, Duration duration,
                                        Reason reason) implements Event {}

    public sealed interface Reason {}

    public record OutsideOperatingHours(OperatingHours hours) implements Reason {}

    public record SlotAlreadyTaken() implements Reason {}
}
