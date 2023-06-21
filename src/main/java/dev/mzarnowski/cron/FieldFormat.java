package dev.mzarnowski.cron;

import java.util.List;

interface FieldFormat {
    int min();
    int max();
    List<String> mnemonics();
}
