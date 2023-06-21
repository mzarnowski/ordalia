package dev.mzarnowski.cron;

final class ParseException extends RuntimeException {
    ParseException(String message) {
        super(message);
    }

    ParseException(Exception cause) {
        super(cause);
    }
}
