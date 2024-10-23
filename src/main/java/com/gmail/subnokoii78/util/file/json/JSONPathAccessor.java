package com.gmail.subnokoii78.util.file.json;

import com.gmail.subnokoii78.util.other.TupleLR;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class JSONPathAccessor {
    private @NotNull List<String> splitPath(@NotNull String path) {
        final Pattern pattern = Pattern.compile("^([^\\[\\]]+)(?:\\[([+-]?\\d+)])+$");

        return Arrays.stream(path.split("\\."))
            .flatMap(key -> {
                final Matcher matcher = pattern.matcher(key);
                if (matcher.matches()) {
                    final List<String> list = new ArrayList<>();
                    list.add(matcher.group(1));
                    final String indexes = key.replaceFirst(matcher.group(1), "");
                    list.addAll(
                        Arrays.stream(indexes.substring(1, indexes.length() - 1).split("]\\[?"))
                            .map(index -> {
                                if (index.matches("^[+-]?\\d+$")) {
                                    if (index.startsWith("+")) return ARRAY_INDEX_PREFIX + index.substring(1);
                                    else if (index.equals("-0")) return ARRAY_INDEX_PREFIX + "0";
                                    else return ARRAY_INDEX_PREFIX + index;
                                }
                                else throw new IllegalArgumentException("インデックスの解析に失敗しました");
                            })
                            .toList()
                    );
                    return list.stream();
                }
                else if (key.contains("[") || key.contains("]")) {
                    throw new IllegalArgumentException("配列を含むキーは次の形式に従う必要があります: '^([^\\[\\]]+)(?:\\[([+-]?\\d+)])+$'");
                }
                else return Stream.of(key);
            })
            .toList();
    }

    private int parseIndexKey(@NotNull String key) {
        try {
            return Integer.parseInt(key.replace(ARRAY_INDEX_PREFIX, ""));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("無効なインデックスキーです: 範囲外の値を使用している可能性があります");
        }
    }

    private final JSONValue<?> jsonValue;

    public JSONPathAccessor(@NotNull JSONValue<?> value) {
        this.jsonValue = value;
    }

    private TupleLR<?, ?> finalPair(@NotNull List<String> path, boolean createWay) {
        Object value = jsonValue;

        while (path.size() >= 2) {
            final String key = path.removeFirst();

            if (value == null) {
                return null;
            }

            value = switch (value) {
                case JSONObject jsonObject -> {
                    if (jsonObject.has(key)) {
                        yield jsonObject.get(key, jsonObject.getTypeOf(key));
                    }
                    else if (createWay) {
                        final JSONObject newObject = new JSONObject();
                        jsonObject.set(key, newObject);
                        yield newObject;
                    }
                    else yield null;
                }
                case JSONArray jsonArray -> {
                    if (key.startsWith(ARRAY_INDEX_PREFIX)) {
                        final int index = parseIndexKey(key);
                        if (jsonArray.has(index)) {
                            yield jsonArray.get(index, jsonArray.getTypeAt(index));
                        }
                        else yield null;
                    }
                    else yield null;
                }
                default -> value;
            };
        }

        final String last = path.removeFirst();
        if (value instanceof JSONArray jsonArray && last.startsWith(ARRAY_INDEX_PREFIX)) {
            final int index = parseIndexKey(last);
            if (jsonArray.has(index)) return new TupleLR<>(jsonArray, index);
            else return null;
        }
        else if (value instanceof JSONObject jsonObject) {
            if (jsonObject.has(last)) return new TupleLR<>(jsonObject, last);
            else return null;
        }
        else return null;
    }

    public <S extends JSONValue<?>, T, U> @Nullable U access(@NotNull String path, boolean createWay, Class<S> s, Class<T> t, @NotNull BiFunction<S, T, U> accessor) {
        final TupleLR<?, ?> pair = finalPair(new ArrayList<>(splitPath(path)), createWay);
        if (pair == null) return null;

        final Object structure = pair.left();
        final Object key = pair.right();

        if (s.isInstance(structure) && t.isInstance(key)) {
            return accessor.apply(s.cast(structure), t.cast(key));
        }
        else {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("予期しないクラスがaccess()の引数に渡されました: ");
            if (!s.isInstance(structure)) {
                stringBuilder
                    .append("s=")
                    .append(s)
                    .append("(期待された型: ")
                    .append(structure.getClass())
                    .append(") ");
            }

            if (!t.isInstance(key)) {
                stringBuilder
                    .append("t=")
                    .append(t)
                    .append("(期待された型: ")
                    .append(key.getClass())
                    .append(") ");
            }

            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private static final String ARRAY_INDEX_PREFIX =  "ARRAY_INDEX(" + UUID.randomUUID() + ")=";

    /*static {
        final JSONObject s = new JSONPathAccessor(new JSONObject()).access("foo.bar", true, JSONObject.class, String.class, (s, t) -> {
            s.set(t, "value");
            return s;
        });
    }*/
}
