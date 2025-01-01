package com.gmail.subnokoii78.util.file.mojangson;

import com.gmail.subnokoii78.util.execute.NumberRange;
import org.jetbrains.annotations.NotNull;

public class MojangsonParseException extends RuntimeException {
    private MojangsonParseException(@NotNull String message) {
        super(message);
    }

    public static class ExceptionCreator {
        private final MojangsonParser parser;

        protected ExceptionCreator(@NotNull MojangsonParser parser) {
            this.parser = parser;
        }

        public @NotNull MojangsonParseException create(@NotNull String message) {
            return new MojangsonParseException(message(message, parser.text, parser.location));
        }

        private static @NotNull String message(@NotNull String message, @NotNull String text, int location) {
            if (text.isEmpty()) {
                return message;
            }

            final NumberRange<Integer> range = NumberRange.of(0, text.length() - 1);

            return message +
                "; 終了位置: " +
                text.substring(range.clamp(location - 8), range.clamp(location)) +
                " >> " +
                text.charAt(range.clamp(location)) +
                " << " +
                text.substring(range.clamp(location + 1), range.clamp(location + 8));
        }
    }
}
