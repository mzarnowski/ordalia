package dev.mzarnowski.cron;

import java.util.List;

enum CronFieldFormat implements FieldFormat {
    MINUTE_OF_HOUR(0, 60),
    HOUR_OF_DAY(0, 24),
    DAY_OF_MONTH(1, 31),
    DAY_OF_WEEK(0, "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"),
    MONTH_OF_YEAR(1, "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC");

    private final int min;
    private final int max;
    private final List<String> mnemonics;

    CronFieldFormat(int offset, int count) {
        this(offset, offset + count - 1, new String[0]);
    }

    CronFieldFormat(int offset, String... mnemonics) {
        this(offset, offset + mnemonics.length - 1, mnemonics);
    }

    CronFieldFormat(int min, int max, String[] mnemonics) {
        this.min = min;
        this.max = max;
        this.mnemonics = List.of(mnemonics);
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public List<String> mnemonics() {
        return mnemonics;
    }
}
