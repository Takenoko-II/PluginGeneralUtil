package com.gmail.subnokoii78.util.execute;

public enum IfUnless {
    IF(true),

    UNLESS(false);

    private final boolean bool;

    IfUnless(boolean bool) {
        this.bool = bool;
    }

    public boolean invertOrNot(boolean condition) {
        if (bool) return condition;
        else return !condition;
    }
}
