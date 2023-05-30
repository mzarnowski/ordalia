package dev.mzarnowski.cinema.screening;

import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.room.Room;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static dev.mzarnowski.cinema.TestParsers.time;

class PremiereScreeningPolicyTest {
    private static final Movie MOVIE = new Movie(new Movie.Id("foo-bar"), Duration.ofMinutes(30));
    private static final Room.Id ROOM = new Room.Id("foo");

    @Test
    public void premiere_screening_cannot_start_before_certain_hour() {
        // given a cinema
        var policy = new PremiereScreeningPolicy((movie) -> false, time("17:00"));

        // when scheduling a movie before premiere-screening-time-threshold
        var start = ZonedDateTime.of(LocalDate.now(), time("16:59"), ZoneOffset.UTC);
        var result = policy.verify(MOVIE, ROOM, start);

        // then the screening is vetoed
        Assertions.assertThat(result).contains(new PremiereScreeningPolicy.BeforePremiereScreeningTime(time("17:00")));
    }

    @Test
    public void premiere_screening_can_start_after_certain_hour() {
        // given a cinema
        var policy = new PremiereScreeningPolicy((movie) -> false, time("17:00"));

        // when scheduling a movie before premiere-screening-time-threshold
        var start = ZonedDateTime.of(LocalDate.now(), time("17:01"), ZoneOffset.UTC);
        var result = policy.verify(MOVIE, ROOM, start);

        // then the screening is vetoed
        Assertions.assertThat(result).isEmpty();
    }
}