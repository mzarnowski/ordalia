package dev.mzarnowski.cron;

final class ParseException extends RuntimeException {
    ParseException(String message) {
        super(message);
    }

    public ParseException(Exception cause) {
        super(cause);
    }
}
