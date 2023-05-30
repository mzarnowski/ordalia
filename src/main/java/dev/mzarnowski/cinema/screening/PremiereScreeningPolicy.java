package dev.mzarnowski.cinema.screening;

import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.room.Room;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

public class PremiereScreeningPolicy implements Policy {
    private final ScreeningArchive archive;
    private final LocalTime earliestScreeningTime;

    public PremiereScreeningPolicy(ScreeningArchive archive, LocalTime earliestScreeningTime) {
        this.archive = archive;
        this.earliestScreeningTime = earliestScreeningTime;
    }

    @Override
    public Optional<Veto> verify(Movie movie, Room.Id room, ZonedDateTime start) {
        if (archive.contains(movie.id())) {
            return Optional.empty(); // not a premiere
        }

        if (earliestScreeningTime.isBefore(start.toLocalTime())) {
            return Optional.empty();
        }

        return Optional.of(new BeforePremiereScreeningTime(earliestScreeningTime));
    }

    public record BeforePremiereScreeningTime(LocalTime earliestScreeningTime) implements Veto {}
}
