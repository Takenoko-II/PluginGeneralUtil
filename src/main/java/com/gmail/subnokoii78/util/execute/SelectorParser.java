package com.gmail.subnokoii78.util.execute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ApiStatus.Experimental
public class SelectorParser {
    private static final Set<Character> IGNORED = Set.of(' ', '\n');

    private static final Set<Character> INTEGERS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private static final char DECIMAL_POINT = '.';

    private static final Set<Character> SIGNS = Set.of('+', '-');

    private static final Map<String, EntitySelector.Builder<? extends Entity>> SELECTOR_TYPES = new HashMap<>(Map.of(
        "@p", EntitySelector.P,
        "@a", EntitySelector.A,
        "@r", EntitySelector.R,
        "@s", EntitySelector.S,
        "@e", EntitySelector.E,
        "@n", EntitySelector.N
    ));

    private static final List<Character> SELECTOR_ARGUMENT_BRACES = List.of('[', ']');

    private static final Map<String, SelectorArgument.Builder<?>> ARGUMENT_TYPES = new HashMap<>();

    static {
        ARGUMENT_TYPES.put("x", SelectorArgument.X);
        ARGUMENT_TYPES.put("y", SelectorArgument.Y);
        ARGUMENT_TYPES.put("z", SelectorArgument.Z);
        ARGUMENT_TYPES.put("type", SelectorArgument.TYPE);
        ARGUMENT_TYPES.put("name", SelectorArgument.NAME);
        ARGUMENT_TYPES.put("tag", SelectorArgument.TAG);
        ARGUMENT_TYPES.put("distance", SelectorArgument.DISTANCE); // distanceRange()
        ARGUMENT_TYPES.put("sort", SelectorArgument.SORT);
        ARGUMENT_TYPES.put("dxyz", SelectorArgument.DXYZ); // vec3()?
        ARGUMENT_TYPES.put("gamemode", SelectorArgument.GAMEMODE);
        ARGUMENT_TYPES.put("level", SelectorArgument.LEVEL); // levelRange()
        ARGUMENT_TYPES.put("x_rotation", SelectorArgument.X_ROTATION); // rotationRange()
        ARGUMENT_TYPES.put("y_rotation", SelectorArgument.Y_ROTATION); // rotationRange()
        ARGUMENT_TYPES.put("team", SelectorArgument.TEAM);
        ARGUMENT_TYPES.put("advancements", SelectorArgument.ADVANCEMENTS); // advancements()
        ARGUMENT_TYPES.put("scores", SelectorArgument.SCORES); // scores()
        ARGUMENT_TYPES.put("limit", SelectorArgument.LIMIT);
        ARGUMENT_TYPES.put("predicate", SelectorArgument.PREDICATE); // entityPredicate()
    }

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

    private void back() {
        location--;
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

    private boolean next(@NotNull String next) {
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

    private boolean next(char next) {
        return next(String.valueOf(next));
    }

    private void expect(@NotNull String next) {
        if (!test(next)) {
            throw new SelectorParseException("位置 " + location + "では文字列 '" + next + "' が期待されていましたが、テストが偽を返しました");
        }
    }

    private void expect(char next) {
        expect(String.valueOf(next));
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

        for (final String type : SELECTOR_TYPES.keySet().stream().sorted((a, b) -> b.length() - a.length()).toList()) {
            if (next(type)) {
                return SELECTOR_TYPES.get(type).build();
            }
        }

        throw new IllegalStateException();
    }

    private @NotNull Set<SelectorArgument> arguments() {
        if (isOver()) {
            throw new IllegalStateException();
        }

        final Set<SelectorArgument> arguments = new HashSet<>();

        expect(SELECTOR_ARGUMENT_BRACES.get(0));

        do {
            for (final var name : ARGUMENT_TYPES.keySet().stream().sorted((a, b) -> b.length() - a.length()).toList()) {
                if (next(name) && next('=')) {
                    final SelectorArgument.Builder<Object> builder = (SelectorArgument.Builder<Object>) ARGUMENT_TYPES.get(name);
                    arguments.add(builder.build(
                        argumentValue(builder.getArgumentType())
                    ));
                }
            }
        }
        while (next(','));

        expect(SELECTOR_ARGUMENT_BRACES.get(1));

        return arguments;
    }

    private @NotNull Object argumentValue(@NotNull Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return string();
        }
        else if (clazz.equals(Integer.class)) {
            return integer();
        }
        else if (clazz.equals(Boolean.class)) {
            return bool();
        }
        else if (clazz.equals(Double.class)) {
            return decimal();
        }
        else if (clazz.equals(Float.class)) {
            return (float) decimal();
        }
        else if (clazz.equals(EntityType.class)) {
            final EntityType type = EntityType.fromName(string());
            if (type == null) {
                throw new SelectorParseException("不明なエンティティタイプです");
            }
            return type;
        }

        throw new SelectorParseException("パーサーが対応していない型が渡されました");
    }

    private @NotNull String string() {
        final String string;

        if (next('"')) {
            string = text.substring(location).split("(?<!\\\\)\"")[0];
            location += string.length();
            expect('"');
        }
        else {
           string = text.substring(location).split(",")[0];
           location += string.length();
        }

        return string;
    }

    private int integer() {
        final StringBuilder stringBuilder = new StringBuilder();

        final char initial = next();

        if (SIGNS.contains(initial)) {
            stringBuilder.append(initial);
        }

        while (!isOver()) {
           final char next = next();

           if (INTEGERS.contains(next)) {
               stringBuilder.append(next);
           }
           else {
               back();
               break;
           }
        }

        try {
            return Integer.parseInt(stringBuilder.toString());
        }
        catch (NumberFormatException e) {
            throw new SelectorParseException("小数の解析に失敗しました", e);
        }
    }

    private boolean bool() {
        if (next("true")) return true;
        else if (next("false")) return false;
        else throw new SelectorParseException("真偽値の解析に失敗しました");
    }

    private double decimal() {
        final StringBuilder stringBuilder = new StringBuilder();

        boolean intAppeared = false;
        boolean pointAppeared = false;

        final char initial = next();

        if (SIGNS.contains(initial)) {
            stringBuilder.append(initial);
        }

        while (!isOver()) {
            final char next = next();

            if (INTEGERS.contains(next)) {
                stringBuilder.append(next);
                intAppeared = true;
            }
            else if (next == DECIMAL_POINT && intAppeared && !pointAppeared) {
                stringBuilder.append(next);
                pointAppeared = true;
            }
            else {
                back();
                break;
            }
        }

        try {
            return Double.parseDouble(stringBuilder.toString());
        }
        catch (NumberFormatException e) {
            throw new SelectorParseException("小数の解析に失敗しました", e);
        }
    }

    /*
    private NumberRange.DistanceRange distanceRange() {
        final String s = text.substring(location);
        for (int i = 0; i < s.length() - 1; i++) {

        }
    }
*/
    private void extra() {
        ignore();

        if (!text.substring(location).isEmpty()) {
            throw new SelectorParseException("解析終了後に無効な文字列を検知しました: " + text.substring(location));
        }
    }

    private @NotNull EntitySelector<? extends Entity> parse() {
        final EntitySelector<? extends Entity> selector = type();
        for (final SelectorArgument argument : arguments()) {
            selector.addArgument(argument);
        }

        extra();

        return selector;
    }

    @ApiStatus.Experimental
    public static @NotNull EntitySelector<? extends Entity> parse(@NotNull String selector) {
        return new SelectorParser(selector).parse();
    }
}
