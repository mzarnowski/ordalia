package dev.mzarnowski.cinema.show;

import dev.mzarnowski.cinema.Event;
import dev.mzarnowski.cinema.movie.Movie;
import dev.mzarnowski.cinema.room.Room.Id;

import java.time.ZonedDateTime;

public record ShowRejected(Movie movie, Id room, ZonedDateTime start, Policy.Veto reason) implements Event {}
