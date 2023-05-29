package dev.mzarnowski.cinema.room;

import dev.mzarnowski.cinema.Event;
import dev.mzarnowski.cinema.OperatingHours;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static dev.mzarnowski.cinema.TestParsers.operatingHours;

public class RoomTest {
    private static final Room.Id ROOM_ID = new Room.Id("foo");
    private static final OperatingHours OPERATING_HOURS = operatingHours("08:00", "22:00");
    private static final ZonedDateTime START_TIME = ZonedDateTime.of(LocalDate.now(), OPERATING_HOURS.since(), ZoneOffset.UTC);
    private static final Duration DURATION = Duration.of(30, ChronoUnit.MINUTES);
    private static final Duration CLEANING_TIME = Duration.of(10, ChronoUnit.MINUTES);

    @Test
    public void can_schedule_movie_during_operating_hours() {
        // given an empty room and a time during the operating hours
        var room = new Room(ROOM_ID, OPERATING_HOURS, CLEANING_TIME);

        // then a movie is scheduled
        var events = room.schedule(START_TIME, DURATION);
        Assertions.assertThat(events)
                .containsOnly(new Room.MovieScheduled(ROOM_ID, START_TIME, DURATION));
    }

    @Test
    public void cleaning_can_start_after_operating_hours() {
        // given an empty room and a time movie to finish at the closing time
        var room = new Room(ROOM_ID, OPERATING_HOURS, CLEANING_TIME);
        var start = ZonedDateTime.of(LocalDate.now(), OPERATING_HOURS.until().minus(DURATION), ZoneOffset.UTC);

        // then a movie is scheduled
        var events = room.schedule(start, DURATION);
        Assertions.assertThat(events)
                .containsOnly(new Room.MovieScheduled(ROOM_ID, start, DURATION));
    }

    @Test
    public void movie_cannot_start_before_operating_hours() {
        // given an empty room and a time before opening
        var room = new Room(ROOM_ID, OPERATING_HOURS, CLEANING_TIME);
        var timeBeforeOpening = ZonedDateTime.of(LocalDate.now(), OPERATING_HOURS.since().minusMinutes(1), ZoneOffset.UTC);

        // then a schedule request is rejected
        var events = room.schedule(timeBeforeOpening, DURATION);
        Assertions.assertThat(events)
                .containsOnly(new Room.RejectedSchedulingOutsideOperatingHours(ROOM_ID, timeBeforeOpening, OPERATING_HOURS));
    }

    @Test
    public void movie_cannot_start_after_operating_hours() {
        // given an empty room and a time after closing
        var room = new Room(ROOM_ID, OPERATING_HOURS, CLEANING_TIME);
        var timeAfterClosing = ZonedDateTime.of(LocalDate.now(), OPERATING_HOURS.until().plusMinutes(1), ZoneOffset.UTC);

        // then a schedule request is rejected
        var events = room.schedule(timeAfterClosing, DURATION);
        Assertions.assertThat(events)
                .containsOnly(new Room.RejectedSchedulingOutsideOperatingHours(ROOM_ID, timeAfterClosing, OPERATING_HOURS));
    }

    @Test
    public void movie_cannot_end_after_operating_hours() {
        // given an empty room and a time just before closing
        var room = new Room(ROOM_ID, OPERATING_HOURS, CLEANING_TIME);
        var timeJustBeforeClosing = ZonedDateTime.of(LocalDate.now(), OPERATING_HOURS.until().minusMinutes(1), ZoneOffset.UTC);

        // then a schedule request is rejected
        var events = room.schedule(timeJustBeforeClosing, DURATION);
        Assertions.assertThat(events)
                .containsOnly(new Room.RejectedSchedulingOutsideOperatingHours(ROOM_ID, timeJustBeforeClosing, OPERATING_HOURS));
    }

    @Test
    public void only_one_movie_can_be_scheduled_at_a_time() {
        // given a room with a scheduled movie
        var room = new Room(ROOM_ID, OPERATING_HOURS, CLEANING_TIME);
        Assertions.assertThat(room.schedule(START_TIME, DURATION))
                .containsOnly(new Room.MovieScheduled(ROOM_ID, START_TIME, DURATION));

        // then another movie cannot be scheduled at the same time
        var events = room.schedule(START_TIME, DURATION);
        Assertions.assertThat(events).containsOnly(new Room.RejectedOverlappingSchedule(START_TIME));
    }

    @RepeatedTest(100)
    public void only_one_movie_can_be_scheduled_at_a_time_concurrently() {
        // given an empty room and multiple planners
        var room = new Room(ROOM_ID, OPERATING_HOURS, CLEANING_TIME);
        var planners = 256;

        // then only one planner can schedule the same time
        var events = new ArrayBlockingQueue<Event>(planners);
        try (var executor = Executors.newFixedThreadPool(planners)) {
            var barrier = new CountDownLatch(planners);
            for (int i = 0; i < planners; i++) {
                executor.execute(() -> {
                    try {
                        barrier.countDown();
                        barrier.await();
                        events.addAll(room.schedule(START_TIME, DURATION));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        Assertions.assertThat(events).containsOnlyOnce(new Room.MovieScheduled(ROOM_ID, START_TIME, DURATION));
    }
}
