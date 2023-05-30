package dev.mzarnowski.cinema.show.policy;

import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.room.Room;
import dev.mzarnowski.cinema.show.Policy;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

public class ShowDuringOperatingHoursPolicy implements Policy {
    private final LocalTime opening;
    private final LocalTime closing;

    public ShowDuringOperatingHoursPolicy(LocalTime opening, LocalTime closing) {
        this.opening = opening;
        this.closing = closing;
    }

    @Override
    public Optional<Veto> verify(Movie movie, Room.Id room, ZonedDateTime start) {
        if (start.toLocalTime().isBefore(opening) || start.toLocalTime().plus(movie.duration()).isAfter(closing)){
            return Optional.of(new OutsideOperatingHours(opening, closing));
        }

        return Optional.empty();
    }

    public record OutsideOperatingHours(LocalTime opening, LocalTime closing ) implements Policy.Veto {}
}
