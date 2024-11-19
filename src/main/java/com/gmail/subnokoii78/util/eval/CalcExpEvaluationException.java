package com.gmail.subnokoii78.util.eval;

import org.jetbrains.annotations.NotNull;

public final class CalcExpEvaluationException extends RuntimeException {
    CalcExpEvaluationException(@NotNull String message) {
        super(message);
    }

    CalcExpEvaluationException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
