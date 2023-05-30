package dev.mzarnowski.cinema;

import dev.mzarnowski.cinema.room.Room;
import dev.mzarnowski.cinema.screening.Policy;
import dev.mzarnowski.cinema.screening.ScreeningRejected;

import java.time.ZonedDateTime;

public class Cinema {
    // embedding the operating hours here means these are the same every day
    private final Room room;
    private final Policy policy;

    public Cinema(Policy policy, Room room) {
        this.policy = policy;
        this.room = room;
    }

    public Event schedule(Movie movie, Room.Id room, ZonedDateTime start) {
        if (room != this.room.id()) {
            throw new IllegalArgumentException("Unknown room: " + room);
        }

        return policy.verify(movie, room, start)
                .or(() -> this.room.schedule(movie, start))
                .map(it -> screeningRejected(movie, room, start, it))
                .orElseGet(() -> screeningScheduled(movie, room, start));
    }

    private static Event screeningRejected(Movie movie, Room.Id room, ZonedDateTime start, Policy.Veto reason) {
        return new ScreeningRejected(movie, room, start, reason);
    }

    private static Event screeningScheduled(Movie movie, Room.Id room, ZonedDateTime start) {
        return new ScreeningScheduled(movie, room, start);
    }
}
