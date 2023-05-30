package dev.mzarnowski.cinema.show.policy;

import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.room.Room;
import dev.mzarnowski.cinema.show.Policy;
import dev.mzarnowski.cinema.show.policy.ShowDuringOperatingHoursPolicy.OutsideOperatingHours;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static dev.mzarnowski.cinema.TestParsers.time;

class ScreeningDuringOperatingHoursPolicyTest {
    private final Movie MOVIE = new Movie(new Movie.Id("foo"), Duration.ofMinutes(30));
    private final Room.Id ROOM = new Room.Id("foo-bar");
    private final Policy policy = new ShowDuringOperatingHoursPolicy(time("08:00"), time("22:00"));

    @Test
    public void reject_screenings_before_operating_hours() {
        var time = ZonedDateTime.of(LocalDate.now(), time("07:59"), ZoneOffset.UTC);

        var veto = policy.verify(MOVIE, ROOM, time);
        Assertions.assertThat(veto).contains(new OutsideOperatingHours(time("08:00"), time("22:00")));
    }

    @Test
    public void movie_cannot_start_after_operating_hours() {
        var time = ZonedDateTime.of(LocalDate.now(), time("22:01"), ZoneOffset.UTC);

        var veto = policy.verify(MOVIE, ROOM, time);
        Assertions.assertThat(veto).contains(new OutsideOperatingHours(time("08:00"), time("22:00")));
    }

    @Test
    public void movie_cannot_end_after_operating_hours() {
        var time = ZonedDateTime.of(LocalDate.now(), time("21:59"), ZoneOffset.UTC);

        var veto = policy.verify(MOVIE, ROOM, time);
        Assertions.assertThat(veto).contains(new OutsideOperatingHours(time("08:00"), time("22:00")));
    }


}