package dev.mzarnowski.cinema.movie;

public interface MovieProvision {
    record Requires3DGlasses() implements MovieProvision {}
}