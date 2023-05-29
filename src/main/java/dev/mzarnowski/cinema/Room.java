package dev.mzarnowski.cinema;

import java.time.ZonedDateTime;
import java.util.List;

public final class Room {
    private final Id id;
    // embedding the operating hours here, means these are the same every day
    private final OperatingHours operatingHours;

    public Room(Id id, OperatingHours operatingHours) {
        this.id = id;
        this.operatingHours = operatingHours;
    }

    public List<Object> schedule(ZonedDateTime start) {
        if (!operatingHours.contains(start)) {
            return List.of(new RejectedSchedulingOutsideOperatingHours(id, start, operatingHours));
        }

        return List.of(new MovieScheduled(id, start));
    }

    public record Id(String value) {}

    public record MovieScheduled(Id id, ZonedDateTime start) {}

    public record RejectedSchedulingOutsideOperatingHours(Id id, ZonedDateTime time, OperatingHours hours) {}
}
