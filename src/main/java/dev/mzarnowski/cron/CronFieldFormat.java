package dev.mzarnowski.cron;

import java.util.List;

public enum CronFieldFormat implements FieldFormat{
    MINUTE(0, 60),
    HOUR(0, 24),
    DAY_OF_MONTH(1, 31),
    DAY_OF_WEEK(0, "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"),
    MONTH(1,"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
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

    public int min(){
        return min;
    }

    public int max(){
        return max;
    }

    public List<String> mnemonics(){
        return mnemonics;
    }
}
