package dev.mzarnowski.cinema.screening;

import dev.mzarnowski.cinema.Movie;

public interface ScreeningArchive {
    boolean contains(Movie.Id movie);
}
