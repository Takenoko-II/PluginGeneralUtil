package com.gmail.subnokoii78.util.execute;

public abstract class ScoreComparator {
    public static final ScoreComparator EQUALS = new ScoreComparator() {
        @Override
        public boolean compare(int a, int b) {
            return a == b;
        }
    };

    public static final ScoreComparator MORE = new ScoreComparator() {
        @Override
        public boolean compare(int a, int b) {
            return a > b;
        }
    };

    public static final ScoreComparator LESS = new ScoreComparator() {
        @Override
        public boolean compare(int a, int b) {
            return a < b;
        }
    };

    public static final ScoreComparator EQUALS_OR_MORE = new ScoreComparator() {
        @Override
        public boolean compare(int a, int b) {
            return a >= b;
        }
    };

    public static final ScoreComparator EQUALS_OR_LESS = new ScoreComparator() {
        @Override
        public boolean compare(int a, int b) {
            return a <= b;
        }
    };

    private ScoreComparator() {
    }

    public abstract boolean compare(int a, int b);
}
