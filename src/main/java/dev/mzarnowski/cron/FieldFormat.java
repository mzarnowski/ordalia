package dev.mzarnowski.cron;

record FieldFormat(int min, int max) {
    static final FieldFormat MINUTE = of(0, 60);
    static final FieldFormat HOUR = of(0, 24);
    static final FieldFormat DAY_OF_WEEK= of(0, 7);
    static final FieldFormat DAY_OF_MONTH= of(1, 31);
    static final FieldFormat MONTH = of(1, 12);

    static FieldFormat of(int offset, int count) {
        return new FieldFormat(offset, offset + count - 1);
    }
}
