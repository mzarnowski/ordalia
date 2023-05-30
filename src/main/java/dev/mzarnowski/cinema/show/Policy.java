package dev.mzarnowski.cinema.show;

import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.room.Room;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

@FunctionalInterface
public interface Policy {
    Optional<Veto> verify(Movie movie, Room.Id room, ZonedDateTime start);

    static Policy either(Policy... policies){
        return (movie, room, start) -> Arrays.stream(policies)
                .flatMap(it -> it.verify(movie, room, start).stream())
                .findFirst();
    }

    interface Veto {}
}
