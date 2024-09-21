package com.gmail.subnokoii78.util.execute;

/**
 * 二つのスコアを比較するための関数
 */
@FunctionalInterface
public interface ScoreComparator {
    /**
     * 引数に渡された2値を比較します。
     */
    boolean compare(int a, int b);

    /**
     * 数値の一致を条件にするオプション
     */
    ScoreComparator EQUALS = (a, b) -> a == b;

    /**
     * 数値AがBより大きいことを条件にするオプション
     */
    ScoreComparator MORE = (a, b) -> a > b;

    /**
     * 数値AがBより小さいことを条件にするオプション
     */
    ScoreComparator LESS = (a, b) -> a < b;

    /**
     * 数値AがB以上なことを条件にするオプション
     */
    ScoreComparator EQUALS_OR_MORE = (a, b) -> a >= b;

    /**
     * 数値AがB以下なことを条件にするオプション
     */
    ScoreComparator EQUALS_OR_LESS = (a, b) -> a <= b;
}
