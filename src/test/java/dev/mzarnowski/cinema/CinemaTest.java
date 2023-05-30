package dev.mzarnowski.cinema;

import dev.mzarnowski.cinema.room.Room;
import dev.mzarnowski.cinema.screening.Policy;
import dev.mzarnowski.cinema.screening.ScreeningRejected;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

import static dev.mzarnowski.cinema.TestParsers.time;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CinemaTest {
    private static final Movie MOVIE = new Movie(new Movie.Id("foo-bar"), Duration.ofMinutes(30));

    private static final Room.Id ROOM_ID = new Room.Id("foo");
    private static final OperatingHours OPERATING_HOURS = new OperatingHours(time("08:00"), time("22:00"));

    @Test
    public void policy_veto_prevents_interactions_with_the_room() {
        var policy = mock(Policy.class);
        var veto = mock(Policy.Veto.class);
        when(policy.verify(any(), any(), any())).thenReturn(Optional.of(veto));

        var room = spy(new Room(ROOM_ID, Duration.ofMinutes(10)));

        var cinema = new Cinema(policy, room);

        // when scheduling a movie
        ZonedDateTime start = ZonedDateTime.now();
        var result = cinema.schedule(MOVIE, ROOM_ID, start);

        // then policy is consulted before claiming the room time-slot
        verify(policy, times(1)).verify(MOVIE, ROOM_ID, start);
        verify(room, never()).schedule(any(), any());
        Assertions.assertThat(result).isEqualTo(new ScreeningRejected(MOVIE, ROOM_ID, start, veto));
    }

    @Test
    public void room_tries_to_schedule_a_movie_when_no_cinema_policy_vetoes() {
        var policy = mock(Policy.class);
        when(policy.verify(any(), any(), any())).thenReturn(Optional.empty());

        var room = spy(new Room(ROOM_ID, Duration.ofMinutes(10)));

        var cinema = new Cinema(policy, room);

        // when scheduling a movie
        ZonedDateTime start = ZonedDateTime.now();
        var result = cinema.schedule(MOVIE, ROOM_ID, start);

        // then policy is consulted before claiming the room time-slot
        verify(policy, times(1)).verify(MOVIE, ROOM_ID, start);
        verify(room, times(1)).schedule(MOVIE, start);
        Assertions.assertThat(result).isEqualTo(new ScreeningScheduled(MOVIE, ROOM_ID, start));
    }
}
