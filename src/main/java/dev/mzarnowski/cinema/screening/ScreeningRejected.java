package dev.mzarnowski.cinema.screening;

import dev.mzarnowski.cinema.Event;
import dev.mzarnowski.cinema.Movie;
import dev.mzarnowski.cinema.room.Room.Id;

import java.time.ZonedDateTime;

public record ScreeningRejected(Movie movie, Id room, ZonedDateTime start, Policy.Veto reason) implements Event {}
