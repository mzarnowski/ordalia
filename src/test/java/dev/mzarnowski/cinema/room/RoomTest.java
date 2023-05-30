package dev.mzarnowski.cinema.room;

import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.OperatingHours;
import dev.mzarnowski.cinema.screening.Policy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static dev.mzarnowski.cinema.TestParsers.operatingHours;
import static dev.mzarnowski.cinema.TestParsers.time;

public class RoomTest {
    private static final Room.Id ROOM_ID = new Room.Id("foo");
    private static final ZonedDateTime START_TIME = ZonedDateTime.of(LocalDate.now(), time("08:00"), ZoneOffset.UTC);
    private static final Duration DURATION = Duration.of(30, ChronoUnit.MINUTES);
    private static final Duration CLEANING_TIME = Duration.of(10, ChronoUnit.MINUTES);
    private static final Movie MOVIE = new Movie(new Movie.Id("foo-bar"), DURATION);

    @Test
    public void can_schedule_movie_during_operating_hours() {
        // given an empty room and a time during the operating hours
        var room = new Room(ROOM_ID, CLEANING_TIME);

        // then a movie is scheduled
        var veto = room.schedule(MOVIE, START_TIME);
        Assertions.assertThat(veto).isEmpty();
    }

    @Test
    public void movie_cannot_start_when_the_room_is_being_cleaned() {
        // given a room with movie scheduled
        var room = new Room(ROOM_ID, CLEANING_TIME);
        Assertions.assertThat(room.schedule(MOVIE, START_TIME)).isEmpty();

        // when trying to schedule a movie when the room is being cleaned after the previous show
        var offendingStartTime = START_TIME.plus(DURATION).plusMinutes(1);
        var veto = room.schedule(MOVIE, offendingStartTime);

        // then scheduling fails
        Assertions.assertThat(veto).contains(new Room.SlotAlreadyTaken());
    }

    @Test
    public void only_one_movie_can_be_scheduled_at_a_time() {
        // given a room with a scheduled movie
        var room = new Room(ROOM_ID, CLEANING_TIME);
        Assertions.assertThat(room.schedule(MOVIE, START_TIME)).isEmpty();

        // then another movie cannot be scheduled at the same time
        var veto = room.schedule(MOVIE, START_TIME);
        Assertions.assertThat(veto).contains(new Room.SlotAlreadyTaken());
    }

    @RepeatedTest(100)
    public void only_one_movie_can_be_scheduled_at_a_time_concurrently() {
        // given an empty room and multiple planners
        var room = new Room(ROOM_ID, CLEANING_TIME);
        var planners = 256;

        // then only one planner can schedule the same time
        var vetoes = new ArrayBlockingQueue<Optional<Policy.Veto>>(planners);
        try (var executor = Executors.newFixedThreadPool(planners)) {
            var barrier = new CountDownLatch(planners);
            for (int i = 0; i < planners; i++) {
                executor.execute(() -> {
                    try {
                        barrier.countDown();
                        barrier.await();
                        vetoes.add(room.schedule(MOVIE, START_TIME));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        Assertions.assertThat(vetoes).containsOnlyOnce(Optional.empty());
    }
}
