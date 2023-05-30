package dev.mzarnowski.cinema;

import dev.mzarnowski.cinema.room.Room;
import dev.mzarnowski.cinema.show.Policy;
import dev.mzarnowski.cinema.show.ShowScheduled;
import dev.mzarnowski.cinema.show.ShowRejected;

import java.time.ZonedDateTime;
import java.util.Optional;

public class Cinema {
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

        if (!start.toLocalDate().equals(start.plus(movie.duration()).toLocalDate())) {
            throw new IllegalArgumentException("");
        }

        return consultPolicy(movie, room, start)
                .orElseGet(() -> claimTimeSlot(this.room, movie, start));
    }

    private Optional<Event> consultPolicy(Movie movie, Room.Id room, ZonedDateTime start){
        return policy.verify(movie, room, start).map(veto -> showRejected(movie, room, start, veto));
    }

    private Event claimTimeSlot(Room room, Movie movie, ZonedDateTime start){
        return this.room.schedule(movie, start)
                .map(it -> showScheduled(movie, room.id(), start))
                .orElseGet(() -> showRejected(movie, room.id(), start, new SlotAlreadyTaken()));
    }

    private static Event showRejected(Movie movie, Room.Id room, ZonedDateTime start, Policy.Veto reason) {
        return new ShowRejected(movie, room, start, reason);
    }

    private static Event showScheduled(Movie movie, Room.Id room, ZonedDateTime start) {
        return new ShowScheduled(movie, room, start);
    }

    public record SlotAlreadyTaken() implements Policy.Veto {}
}
