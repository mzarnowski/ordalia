package dev.mzarnowski.cinema;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static dev.mzarnowski.cinema.TestParsers.operatingHours;

public class RoomTest {
    private static final Room.Id ROOM_ID = new Room.Id("foo");
    private static final OperatingHours OPERATING_HOURS = operatingHours("08:00", "22:00");

    @Test
    public void can_schedule_movie_during_operating_hours() {
        // given an empty room and a time during the operating hours
        var room = new Room(ROOM_ID, OPERATING_HOURS);
        var startTime = ZonedDateTime.of(LocalDate.now(), OPERATING_HOURS.since(), ZoneOffset.UTC);

        // then a movie is scheduled
        var events = room.schedule(startTime);
        Assertions.assertThat(events)
                .containsOnly(new Room.MovieScheduled(ROOM_ID, startTime));
    }
    
    @Test
    public void cannot_schedule_movie_before_operating_hours() {
        // given an empty room and a time before the operating hours
        var room = new Room(ROOM_ID, OPERATING_HOURS);
        var timeBeforeOperatingHours = ZonedDateTime.of(LocalDate.now(), OPERATING_HOURS.since().minusMinutes(1), ZoneOffset.UTC);

        // then a schedule request is rejected
        var events = room.schedule(timeBeforeOperatingHours);
        Assertions.assertThat(events)
                .containsOnly(new Room.RejectedSchedulingOutsideOperatingHours(ROOM_ID, timeBeforeOperatingHours, OPERATING_HOURS));
    }

    @Test
    public void cannot_schedule_movie_after_operating_hours() {
        // given an empty room and a time after the operating hours
        var room = new Room(ROOM_ID, OPERATING_HOURS);
        var timeAfterOperatingHours = ZonedDateTime.of(LocalDate.now(), OPERATING_HOURS.until().plusMinutes(1), ZoneOffset.UTC);

        // then a schedule request is rejected
        var events = room.schedule(timeAfterOperatingHours);
        Assertions.assertThat(events)
                .containsOnly(new Room.RejectedSchedulingOutsideOperatingHours(ROOM_ID, timeAfterOperatingHours, OPERATING_HOURS));
    }
}
