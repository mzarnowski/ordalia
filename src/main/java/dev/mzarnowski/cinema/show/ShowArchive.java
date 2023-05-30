package dev.mzarnowski.cinema.show;

import dev.mzarnowski.cinema.movie.Movie;

public interface ShowArchive {
    boolean contains(Movie.Id movie);
}
