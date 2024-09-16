package com.gmail.subnokoii78.util.execute;

public abstract class ScoreComparator {
    /**
     * 数値の一致を条件にするオプション
     */
    public static final ScoreComparator EQUALS = new ScoreComparator() {
        @Override
        public boolean compare(int a, int b) {
            return a == b;
        }
    };

    /**
     * 数値AがBより大きいことを条件にするオプション
     */
    public static final ScoreComparator MORE = new ScoreComparator() {
        @Override
        public boolean compare(int a, int b) {
            return a > b;
        }
    };

    /**
     * 数値AがBより小さいことを条件にするオプション
     */
    public static final ScoreComparator LESS = new ScoreComparator() {
        @Override
        public boolean compare(int a, int b) {
            return a < b;
        }
    };

    /**
     * 数値AがB以上なことを条件にするオプション
     */
    public static final ScoreComparator EQUALS_OR_MORE = new ScoreComparator() {
        @Override
        public boolean compare(int a, int b) {
            return a >= b;
        }
    };

    /**
     * 数値AがB以下なことを条件にするオプション
     */
    public static final ScoreComparator EQUALS_OR_LESS = new ScoreComparator() {
        @Override
        public boolean compare(int a, int b) {
            return a <= b;
        }
    };

    private ScoreComparator() {}

    /**
     * 引数に渡された2値を比較します。
     */
    public abstract boolean compare(int a, int b);
}
