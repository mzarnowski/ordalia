package dev.mzarnowski.cinema.screening;

import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.room.Room;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface Policy {
    Optional<Veto> verify(Movie movie, Room.Id room, ZonedDateTime start);

    interface Veto {}
}
