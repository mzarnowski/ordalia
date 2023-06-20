package dev.mzarnowski.cron;

record FieldFormat(int min, int max, String... mnemonics) {
    static final FieldFormat MINUTE = of(0, 60);
    static final FieldFormat HOUR = of(0, 24);
    static final FieldFormat DAY_OF_MONTH = of(1, 31);
    static final FieldFormat DAY_OF_WEEK = of(0,
            "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT");
    static final FieldFormat MONTH = of(1,
            "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC");

    static FieldFormat of(int offset, int count) {
        return new FieldFormat(offset, offset + count - 1);
    }

    static FieldFormat of(int offset, String... mnemonics) {
        return new FieldFormat(offset, offset + mnemonics.length - 1, mnemonics);
    }
}
