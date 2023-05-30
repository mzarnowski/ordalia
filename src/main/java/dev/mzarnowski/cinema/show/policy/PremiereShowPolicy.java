package dev.mzarnowski.cinema.show.policy;

import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.room.Room;
import dev.mzarnowski.cinema.show.Policy;
import dev.mzarnowski.cinema.show.ShowArchive;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

public class PremiereShowPolicy implements Policy {
    private final ShowArchive archive;
    private final LocalTime earliestScreeningTime;

    public PremiereShowPolicy(ShowArchive archive, LocalTime earliestScreeningTime) {
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
