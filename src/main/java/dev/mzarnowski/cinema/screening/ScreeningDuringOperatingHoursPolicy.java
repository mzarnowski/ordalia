package dev.mzarnowski.cinema.screening;

import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.room.Room;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

public class ScreeningDuringOperatingHoursPolicy implements Policy{
    private final LocalTime opening;
    private final LocalTime closing;

    public ScreeningDuringOperatingHoursPolicy(LocalTime opening, LocalTime closing) {
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
