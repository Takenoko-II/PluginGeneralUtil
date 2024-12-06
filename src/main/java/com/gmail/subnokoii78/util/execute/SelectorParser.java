package com.gmail.subnokoii78.util.execute;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ApiStatus.Experimental
public class SelectorParser {
    private static final Set<Character> IGNORED = Set.of(' ', '\n');

    private static final Map<String, EntitySelector.Builder<? extends Entity>> ENTITY_SELECTOR_TYPES = new HashMap<>(Map.of(
        "@p", EntitySelector.P,
        "@a", EntitySelector.A,
        "@r", EntitySelector.R,
        "@s", EntitySelector.S,
        "@e", EntitySelector.E,
        "@n", EntitySelector.N
    ));

    private static final List<Character> PARAMETER_BRACES = List.of('[', ']');

    private static final Map<String, SelectorArgument.Builder<?>> PARAMETERS = new HashMap<>(Map.of(
        "type", SelectorArgument.TYPE
    ));

    private final String text;

    private int location = 0;

    private SelectorParser(@NotNull String text) {
        this.text = text;
    }

    private boolean isOver() {
        return location >= text.length();
    }

    private char next() {
        if (isOver()) {
            throw new IllegalStateException();
        }

        final char current = text.charAt(location++);

        if (IGNORED.contains(current)) return next();

        return current;
    }

    private boolean test(@NotNull String next) {
        if (isOver()) return false;

        ignore();

        final String str = text.substring(location);

        return str.startsWith(next);
    }

    private boolean test(char next) {
        return test(String.valueOf(next));
    }

    private boolean expect(@NotNull String next) {
        if (isOver()) return false;

        ignore();

        final String str = text.substring(location);

        if (str.startsWith(next)) {
            location += next.length();
            ignore();
            return true;
        }

        return false;
    }

    private boolean expect(char next) {
        return expect(String.valueOf(next));
    }

    private void ignore() {
        if (isOver()) return;

        final char current = text.charAt(location++);

        if (IGNORED.contains(current)) {
            ignore();
        }
        else {
            location--;
        }
    }

    private @NotNull EntitySelector<? extends Entity> type() {
        if (isOver()) {
            throw new IllegalStateException();
        }

        ignore();

        for (final String type : ENTITY_SELECTOR_TYPES.keySet().stream().sorted((a, b) -> b.length() - a.length()).toList()) {
            if (expect(type)) {
                return ENTITY_SELECTOR_TYPES.get(type).build();
            }
        }

        throw new IllegalStateException();
    }
/*
    private @NotNull Set<SelectorArgument> parameters() {
        if (isOver()) {
            throw new IllegalStateException();
        }

        final Set<SelectorArgument> arguments = new HashSet<>();

        if (expect(PARAMETER_BRACES.get(0))) {
            for (final var name : PARAMETERS.keySet().stream().sorted((a, b) -> b.length() - a.length()).toList()) {
                if (expect(name) && expect('=')) {
                    final SelectorArgument.Builder<?> builder = PARAMETERS.get(name);
                    //arguments.add(builder.build(parameterValue()));
                }
            }

            expect(PARAMETER_BRACES.get(1));
        }
    }

    private <U> @NotNull U parameterValue(@NotNull SelectorArgument.Builder<U> builder) {
        //builder.getId();
    }

    public @NotNull EntitySelector<? extends Entity> parse() {
        return List.of(type(), parameters());
    }*/
}
