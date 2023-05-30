package dev.mzarnowski.cinema.room;

import dev.mzarnowski.cinema.Movie;
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

import static dev.mzarnowski.cinema.TestParsers.time;

public class RoomTest {
    private static final Room.Id ROOM_ID = new Room.Id("foo");
    private static final ZonedDateTime START_TIME = ZonedDateTime.of(LocalDate.now(), time("08:00"), ZoneOffset.UTC);
    private static final Duration DURATION = Duration.of(30, ChronoUnit.MINUTES);
    private static final Duration CLEANING_TIME = Duration.of(10, ChronoUnit.MINUTES);
    private static final Movie MOVIE = new Movie(new Movie.Id("foo-bar"), DURATION);
    private static final TimeSlot EXPECTED_TIME_SLOT = new TimeSlot(START_TIME, START_TIME.plus(MOVIE.duration()).plus(CLEANING_TIME));

    @Test
    public void can_schedule_movie_during_operating_hours() {
        // given an empty room and a time during the operating hours
        var room = new Room(ROOM_ID, CLEANING_TIME);

        // then a movie is scheduled
        var slot = room.schedule(MOVIE, START_TIME);
        Assertions.assertThat(slot).contains(EXPECTED_TIME_SLOT);
    }

    @Test
    public void movie_cannot_start_when_the_room_is_being_cleaned() {
        // given a room with movie scheduled
        var room = new Room(ROOM_ID, CLEANING_TIME);
        Assertions.assertThat(room.schedule(MOVIE, START_TIME)).contains(EXPECTED_TIME_SLOT);

        // when trying to schedule a movie when the room is being cleaned after the previous show
        var offendingStartTime = START_TIME.plus(DURATION).plusMinutes(1);
        var slot = room.schedule(MOVIE, offendingStartTime);

        // then scheduling fails
        Assertions.assertThat(slot).isEmpty();
    }

    @Test
    public void only_one_movie_can_be_scheduled_at_a_time() {
        // given a room with a scheduled movie
        var room = new Room(ROOM_ID, CLEANING_TIME);
        Assertions.assertThat(room.schedule(MOVIE, START_TIME)).contains(EXPECTED_TIME_SLOT);

        // then another movie cannot be scheduled at the same time
        var slot = room.schedule(MOVIE, START_TIME);
        Assertions.assertThat(slot).isEmpty();
    }

    @RepeatedTest(100)
    public void only_one_movie_can_be_scheduled_at_a_time_concurrently() {
        // given an empty room and multiple planners
        var room = new Room(ROOM_ID, CLEANING_TIME);
        var planners = 256;

        // then only one planner can schedule the same time
        var claimedSlots = new ArrayBlockingQueue<TimeSlot>(planners);
        try (var executor = Executors.newFixedThreadPool(planners)) {
            var barrier = new CountDownLatch(planners);
            for (int i = 0; i < planners; i++) {
                executor.execute(() -> {
                    try {
                        barrier.countDown();
                        barrier.await();
                        room.schedule(MOVIE, START_TIME).ifPresent(claimedSlots::add);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        Assertions.assertThat(claimedSlots).containsOnlyOnce(EXPECTED_TIME_SLOT);
    }
}
