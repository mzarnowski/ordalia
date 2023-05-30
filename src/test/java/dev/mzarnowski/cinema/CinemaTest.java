package dev.mzarnowski.cinema;

import dev.mzarnowski.cinema.room.Room;
import dev.mzarnowski.cinema.screening.PremiereScreeningPolicy;
import dev.mzarnowski.cinema.screening.ScreeningArchive;
import dev.mzarnowski.cinema.screening.ScreeningRejected;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static dev.mzarnowski.cinema.TestParsers.time;

public class CinemaTest {
    private static final Movie MOVIE = new Movie(new Movie.Id("foo-bar"), Duration.ofMinutes(30));

    private static final Room.Id ROOM_ID = new Room.Id("foo");
    private static final OperatingHours OPERATING_HOURS = new OperatingHours(time("08:00"), time("22:00"));
    private static final Room ROOM = new Room(ROOM_ID, OPERATING_HOURS, Duration.ofMinutes(10));


    private static final ScreeningArchive ONLY_PREMIERES = (movie) -> false;

    @Test
    public void cannot_schedule_premiere_movie_too_early() {
        // given a cinema
        var premierePolicy = new PremiereScreeningPolicy(ONLY_PREMIERES, time("17:00"));
        var cinema = new Cinema(premierePolicy, ROOM);

        // when scheduling a movie before premiere-screening-time-threshold
        var start = ZonedDateTime.of(LocalDate.now(), time("16:59"), ZoneOffset.UTC);
        var result = cinema.schedule(MOVIE, ROOM_ID, start);

        // then the screening is vetoed
        Assertions.assertThat(result).isEqualTo(new ScreeningRejected(MOVIE, ROOM_ID, start,
                new PremiereScreeningPolicy.BeforePremiereScreeningTime(time("17:00"))));
    }
}
