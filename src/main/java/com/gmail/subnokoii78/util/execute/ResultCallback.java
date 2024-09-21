package com.gmail.subnokoii78.util.execute;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * {@link Execute.Run#callback(Function)}実行後に実行されるチェーン可能なコールバック
 */
@FunctionalInterface
public interface ResultCallback {
    ResultCallback EMPTY = (successful, returnValue) -> {};

    void onResult(boolean successful, int returnValue);

    default void onSuccess(int returnValue) {
        this.onResult(true, returnValue);
    }

    default void onFailure() {
        this.onResult(false, 0);
    }

    default @NotNull ResultCallback chain(@NotNull StoreTarget storeTarget, @NotNull ResultCallback other) {
        if (equals(EMPTY)) {
            return (successful, returnValue) -> {
                other.onResult(successful, switch (storeTarget) {
                    case RESULT -> returnValue;
                    case SUCCESS -> successful ? 1 : 0;
                });
            };
        }
        else if (other.equals(EMPTY)) {
            return (successful, returnValue) -> {
                onResult(successful, switch (storeTarget) {
                    case RESULT -> returnValue;
                    case SUCCESS -> successful ? 1 : 0;
                });
            };
        }
        else {
            return (successful, returnValue) -> {
                final int value = switch (storeTarget) {
                    case RESULT -> returnValue;
                    case SUCCESS -> successful ? 1 : 0;
                };
                onResult(successful, value);
                other.onResult(successful, value);
            };
        }
    }
}
