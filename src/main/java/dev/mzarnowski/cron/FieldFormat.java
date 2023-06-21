package dev.mzarnowski.cron;

import java.util.List;

public interface FieldFormat {
    int min();
    int max();
    List<String> mnemonics();
}
