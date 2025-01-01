package com.gmail.subnokoii78.util.file.mojangson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class MojangsonParser {
    private static final Set<Character> WHITESPACE = Set.of(' ', '\n');

    private static final char COMMA = ',';

    private static final char COLON = ':';

    private static final char SEMICOLON = ';';

    private static final char ESCAPE = '\\';

    private static final Set<Character> QUOTES = Set.of('"', '\'');

    private static final char[] COMPOUND_BRACES = {'{', '}'};

    private static final char[] ARRAY_LIST_BRACES = {'[', ']'};

    private static final char[] SIGNS = {'+', '-'};

    private static final char DECIMAL_POINT = '.';

    private static final Set<Character> NUMBERS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private static final String[] BOOLEANS = {"false", "true"};

    private static final Function<String, ? extends Number> DEFAULT_INT_PARSER = Integer::parseInt;

    private static final Function<String, ? extends Number> DEFAULT_DECIMAL_PARSER = Double::parseDouble;

    private static final Map<Character, Function<String, ? extends Number>> NUMBER_PARSERS = new HashMap<>(Map.of(
        'b', Byte::parseByte,
        's', Short::parseShort,
        'l', Long::parseLong,
        'f', Float::parseFloat,
        'd', Double::parseDouble,
        'B', Byte::parseByte,
        'S', Short::parseShort,
        'L', Long::parseLong,
        'F', Float::parseFloat,
        'D', Double::parseDouble
    ));

    private static final Map<Character, Function<ObjectList, Object>> PRIMITIVE_ARRAY_CONVERTERS = new HashMap<>(Map.of(
        'B', list -> {
            byte[] array = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                final Object value = list.get(i);
                if (value instanceof Byte byteValue) {
                    array[i] = byteValue;
                }
                else return value.getClass();
            }
            return array;
        },
        'I', list -> {
            int[] array = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                final Object value = list.get(i);
                if (value instanceof Integer intValue) {
                    array[i] = intValue;
                }
                else return value.getClass();
            }
            return array;
        },
        'L', list -> {
            long[] array = new long[list.size()];
            for (int i = 0; i < list.size(); i++) {
                final Object value = list.get(i);
                if (value instanceof Long longValue) {
                    array[i] = longValue;
                }
                else return value.getClass();
            }
            return array;
        }
    ));

    private static final Set<Character> SYMBOLS_ON_STRING = new HashSet<>();

    static {
        SYMBOLS_ON_STRING.addAll(WHITESPACE);
        SYMBOLS_ON_STRING.add(COMMA);
        SYMBOLS_ON_STRING.add(COLON);
        SYMBOLS_ON_STRING.add(SEMICOLON);
        SYMBOLS_ON_STRING.add(ESCAPE);
        SYMBOLS_ON_STRING.addAll(QUOTES);
        SYMBOLS_ON_STRING.add(COMPOUND_BRACES[0]);
        SYMBOLS_ON_STRING.add(COMPOUND_BRACES[1]);
        SYMBOLS_ON_STRING.add(ARRAY_LIST_BRACES[0]);
        SYMBOLS_ON_STRING.add(ARRAY_LIST_BRACES[1]);
        SYMBOLS_ON_STRING.add(SIGNS[0]);
        SYMBOLS_ON_STRING.add(SIGNS[1]);
        SYMBOLS_ON_STRING.add(DECIMAL_POINT);
    }

    private static final class StringObjectMap extends HashMap<String, Object> {}

    private static final class ObjectList extends ArrayList<Object> {}

    private String text;

    private int location = 0;

    private MojangsonParser() {}

    private boolean isOver() {
        return location >= text.length();
    }

    private char next(boolean ignorable) {
        if (isOver()) {
            throw new MojangsonParseException("文字列の長さが期待より不足しています");
        }

        final char next = text.charAt(location++);

        if (ignorable) {
            return WHITESPACE.contains(next) ? next(true) : next;
        }
        return next;
    }

    private void back() {
        if (location > 0) location--;
    }

    private void whitespace() {
        if (isOver()) return;

        final char current = text.charAt(location++);

        if (WHITESPACE.contains(current)) {
            whitespace();
        }
        else {
            location--;
        }
    }

    private boolean test(@NotNull String next) {
        if (isOver()) return false;

        whitespace();

        final String str = text.substring(location);

        return str.startsWith(next);
    }

    private boolean test(char next) {
        return test(String.valueOf(next));
    }

    private boolean next(@NotNull String next) {
        if (isOver()) return false;

        whitespace();

        final String str = text.substring(location);

        if (str.startsWith(next)) {
            location += next.length();
            whitespace();
            return true;
        }

        return false;
    }

    private boolean next(char next) {
        return next(String.valueOf(next));
    }

    private void expect(@NotNull String next) {
        if (!next(next)) {
            throw new MojangsonParseException("期待された文字列は" + next + "でしたが、テストが偽を返しました");
        }
    }

    private void expect(char next) {
        expect(String.valueOf(next));
    }

    private @NotNull String string() {
        final StringBuilder sb = new StringBuilder();
        char current = next(true);

        if (QUOTES.contains(current)) {
            final char quote = current;
            char previous = current;
            current = next(false);

            while (previous == ESCAPE || current != quote) {
                if (previous == ESCAPE && current == quote) {
                    sb.delete(sb.length() - 1, sb.length());
                }

                sb.append(current);

                previous = current;
                current = next(false);
            }
        }
        else {
            while (!WHITESPACE.contains(current) && current != COLON && current != COMPOUND_BRACES[1] && current != ARRAY_LIST_BRACES[1] && current != COMMA) {
                if (SYMBOLS_ON_STRING.contains(current)) {
                    throw new MojangsonParseException("クオーテーションで囲まれていない文字列において利用できない文字("+ current +")を検出しました");
                }

                sb.append(current);
                current = next(false);
            }

            back();
        }

        return sb.toString();
    }

    private @Nullable Number number() {
        final int loc = location;

        final StringBuilder sb = new StringBuilder();
        char current = next(false);

        if (current == SIGNS[0] || current == SIGNS[1] || NUMBERS.contains(current)) {
            sb.append(current);
        }
        else {
            location = loc;
            return null;
        }

        char previous;
        boolean decimalPointAppeared = false;

        Function<String, ? extends Number> parser = null;

        while (!isOver()) {
            previous = current;
            current = next(false);

            if (NUMBERS.contains(current)) {
                sb.append(current);
            }
            else if (NUMBERS.contains(previous) && current == DECIMAL_POINT && !decimalPointAppeared) {
                sb.append(current);
                decimalPointAppeared = true;
            }
            else if (SYMBOLS_ON_STRING.contains(current)) {
                back();
                break;
            }
            else if (NUMBER_PARSERS.containsKey(current)) {
                parser = NUMBER_PARSERS.get(current);
                break;
            }
            else {
                location = loc;
                return null;
            }
        }

        if (!NUMBERS.contains(sb.charAt(sb.length() - 1))) {
            throw new MojangsonParseException("数値は数字で終わる必要があります");
        }

        if (parser == null) {
            parser = decimalPointAppeared ? DEFAULT_DECIMAL_PARSER : DEFAULT_INT_PARSER;
        }

        return parser.apply(sb.toString());
    }

    private @Nullable Byte booleanAsByte() {
        int loc = location;
        final String s = string();
        if (s.equals(BOOLEANS[0])) {
            return 0;
        }
        else if (s.equals(BOOLEANS[1])) {
            return 1;
        }
        else {
            location = loc;
            return null;
        }
    }

    private @NotNull StringObjectMap compound() {
        expect(COMPOUND_BRACES[0]);

        final StringObjectMap map = new StringObjectMap();

        if (next(COMPOUND_BRACES[1])) {
            return map;
        }

        keyValues(map);

        expect(COMPOUND_BRACES[1]);

        return map;
    }

    private @NotNull Object arrayOrList() {
        expect(ARRAY_LIST_BRACES[0]);

        final ObjectList list = new ObjectList();
        Function<ObjectList, Object> arrayConverter = null;

        for (char c : PRIMITIVE_ARRAY_CONVERTERS.keySet()) {
            final String prefix = String.valueOf(c) + SEMICOLON;

            if (next(prefix)) {
                arrayConverter = PRIMITIVE_ARRAY_CONVERTERS.get(c);
                break;
            }
        }

        if (next(ARRAY_LIST_BRACES[1])) {
            return arrayConverter == null ? list : primitiveArray(list, arrayConverter);
        }

        elements(list);

        expect(ARRAY_LIST_BRACES[1]);

        return arrayConverter == null ? list : primitiveArray(list, arrayConverter);
    }

    private @NotNull Object primitiveArray(@NotNull ObjectList list, @NotNull Function<ObjectList, Object> converter) {
        final Object value = converter.apply(list);

        if (value instanceof Class<?> clazz) {
            throw new MojangsonParseException(clazz.getSimpleName().toLowerCase() + "型の値が見つかったため、プリミティブ配列への変換に失敗しました");
        }
        else return value;
    }

    private void keyValues(@NotNull StringObjectMap map) {
        final String key = string();
        if (!next(COLON)) throw new MojangsonParseException("コロンが必要です");
        map.put(key, value());

        final char commaOrBrace = next(true);

        if (commaOrBrace == COMMA) keyValues(map);
        else if (commaOrBrace == COMPOUND_BRACES[1]) back();
        else throw new MojangsonParseException("閉じ括弧が見つかりません");
    }

    private void elements(@NotNull ObjectList list) {
        list.add(value());

        final char commaOrBrace = next(true);

        if (commaOrBrace == COMMA) elements(list);
        else if (commaOrBrace == ARRAY_LIST_BRACES[1]) back();
        else throw new MojangsonParseException("閉じ括弧が見つかりません");
    }

    private @NotNull Object value() {
        if (test(COMPOUND_BRACES[0])) {
            return compound();
        }
        else if (test(ARRAY_LIST_BRACES[0])) {
            return arrayOrList();
        }
        else {
            final Number number = number();
            if (number != null) {
                return number;
            }

            final Byte byteValue = booleanAsByte();
            if (byteValue != null) {
                return byteValue;
            }

            return string();
        }
    }

    private void remainingChars() {
        if (!isOver()) throw new MojangsonParseException("解析終了後、末尾に無効な文字列(" + text.substring(location) + ")を検出しました");
    }

    private @NotNull Object parse() {
        if (text == null) {
            throw new MojangsonParseException("textがnullです");
        }

        final Object value = value();
        remainingChars();
        return value;
    }

    private static <T> @NotNull T parseAs(@NotNull String text, @NotNull Class<T> clazz) {
        final MojangsonParser parser = new MojangsonParser();
        parser.text = text;
        final Object value = parser.parse();

        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        else throw new MojangsonParseException("期待された型(" + clazz.getName() + ")と取得した値(" + value.getClass().getName() + ")が一致しません");
    }

    public static @NotNull Object object(@NotNull String text) {
        final MojangsonParser parser = new MojangsonParser();
        parser.text = text;
        return parser.parse();
    }

    public static @NotNull Map<String, Object> compound(@NotNull String text) {
        return parseAs(text, StringObjectMap.class);
    }

    public static @NotNull List<Object> list(@NotNull String text) {
        return parseAs(text, ObjectList.class);
    }

    public static byte[] byteArray(@NotNull String text) {
        return parseAs(text, byte[].class);
    }

    public static int[] intArray(@NotNull String text) {
        return parseAs(text, int[].class);
    }

    public static long[] longArray(@NotNull String text) {
        return parseAs(text, long[].class);
    }
}
