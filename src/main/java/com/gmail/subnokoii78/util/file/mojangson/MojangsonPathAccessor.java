package com.gmail.subnokoii78.util.file.mojangson;

import com.gmail.subnokoii78.util.file.mojangson.values.MojangsonCompound;
import com.gmail.subnokoii78.util.file.mojangson.values.MojangsonList;
import com.gmail.subnokoii78.util.other.TupleLR;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class MojangsonPathAccessor {
    private final MojangsonCompound initialCompound;

    public MojangsonPathAccessor(@NotNull MojangsonCompound initialCompound) {
        this.initialCompound = initialCompound;
    }

    private static @NotNull List<String> parsePath(@NotNull String path) {
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

    private static int parseIndexKey(@NotNull String key) {
        try {
            return Integer.parseInt(key.replace(ARRAY_INDEX_PREFIX, ""));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("無効なインデックスキーです: 範囲外の値を使用している可能性があります");
        }
    }

    private <T> @Nullable T onFinalPair(@NotNull MojangsonLocationAccess<?, ?> previousPair, @Nullable Object currentStructure, @NotNull List<String> path, boolean createWay, @NotNull Function<MojangsonLocationAccess<?, ?>, T> callback) {
        if (currentStructure == null) return null;

        final String key = path.removeFirst();

        if (path.isEmpty()) {
            if (currentStructure instanceof MojangsonList list && key.startsWith(ARRAY_INDEX_PREFIX)) {
                final int index = parseIndexKey(key);
                final T r = callback.apply(MojangsonLocationAccess.listAccess(list, index));
                previousPair.set(list);
                return r;
            }
            else if (currentStructure instanceof MojangsonCompound compound) {
                final T r = callback.apply(MojangsonLocationAccess.compoundAccess(compound, key));
                previousPair.set(compound);
                return r;
            }
            else return null;
        }

        final TupleLR<Object, MojangsonLocationAccess<?, ?>> nextObjects = getNextObjects(currentStructure, key, createWay);

        if (nextObjects == null) return null;

        return onFinalPair(nextObjects.right(), nextObjects.left(), path, createWay, callback);
    }

    private @Nullable TupleLR<Object, MojangsonLocationAccess<?, ?>> getNextObjects(@NotNull Object currentStructure, @NotNull String key, boolean createWay) {
        return switch (currentStructure) {
            case MojangsonCompound compound -> {
                if (compound.hasKey(key)) {
                    final Object nextStructure = compound.getKey(key, compound.getTypeOfKey(key));
                    yield new TupleLR<>(nextStructure, MojangsonLocationAccess.compoundAccess(compound, key));
                }
                else if (createWay) {
                    final MojangsonCompound nextStructure = new MojangsonCompound();
                    compound.setKey(key, nextStructure);
                    yield new TupleLR<>(nextStructure, MojangsonLocationAccess.compoundAccess(compound, key));
                }
                else yield null;
            }
            case MojangsonList list -> {
                if (key.startsWith(ARRAY_INDEX_PREFIX)) {
                    final int index = parseIndexKey(key);
                    if (list.has(index)) {
                        final Object nextStructure = list.get(index, list.getTypeAt(index));
                        yield new TupleLR<>(nextStructure, MojangsonLocationAccess.listAccess(list, index));
                    }
                    else yield null;
                }
                else yield null;
            }
            default -> null;
        };
    }

    public <T> @Nullable T access(@NotNull String path, boolean createWay, @NotNull Function<MojangsonLocationAccess<?, ?>, T> callback) {
        final List<String> paths = new ArrayList<>(parsePath(path));

        if (paths.isEmpty()) {
            throw new IllegalArgumentException("パスがいかれてるぜ");
        }
        else if (paths.size() == 1) {
            return callback.apply(MojangsonLocationAccess.compoundAccess(initialCompound, paths.getFirst()));
        }
        else {
            final TupleLR<Object, MojangsonLocationAccess<?, ?>> nextObjects = getNextObjects(initialCompound, paths.removeFirst(), createWay);
            if (nextObjects == null) return null;
            return onFinalPair(nextObjects.right(), nextObjects.left(), paths, createWay, callback);
        }
    }

    public boolean has(@NotNull String path) {
        final Boolean flag = access(path, false, MojangsonLocationAccess::has);
        if (flag == null) return false;
        else return flag;
    }

    public @NotNull MojangsonValueType<?> getTypeOf(@NotNull String path) {
        final MojangsonValueType<?> type = access(path, false, MojangsonLocationAccess::getType);
        if (type == null) {
            throw new IllegalArgumentException("パス '" + path + "' は存在しません");
        }
        return type;
    }

    public <T extends MojangsonValue<?>> @NotNull T get(@NotNull String path, @NotNull MojangsonValueType<T> type) {
        final T value = access(path, false, accessor -> accessor.get(type));
        if (value == null) {
            throw new IllegalArgumentException("パス '" + path + "' は存在しません");
        }
        return value;
    }

    public <T> void set(@NotNull String path, @NotNull T value) {
        access(path, true, accessor -> {
            accessor.set(value);
            return null;
        });
    }

    public void delete(@NotNull String path) {
        access(path, false, accessor -> {
            accessor.delete();
            return null;
        });
    }

    public static boolean isValidPath(@NotNull String path) {
        try {
            parsePath(path);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static final String ARRAY_INDEX_PREFIX = "ARRAY_INDEX(" + UUID.randomUUID() + ")=";

    public static abstract class MojangsonLocationAccess<T extends MojangsonValue<?>, U> {
        protected final T structure;

        protected final U key;

        protected MojangsonLocationAccess(@NotNull T value, @NotNull U key) {
            this.structure = value;
            this.key = key;
        }

        public abstract boolean has();

        public abstract @NotNull MojangsonValueType<?> getType();

        public abstract <P extends MojangsonValue<?>> @NotNull P get(@NotNull MojangsonValueType<P> type);

        public abstract <P> void set(P value);

        public abstract void delete();

        private static @NotNull MojangsonLocationAccess<MojangsonCompound, String> compoundAccess(@NotNull MojangsonCompound compound, @NotNull String key) {
            return new CompoundLocationAccess(compound, key);
        }

        private static @NotNull MojangsonLocationAccess<MojangsonList, Integer> listAccess(@NotNull MojangsonList list, @NotNull Integer key) {
            return new ListLocationAccess(list, key);
        }

        private static final class CompoundLocationAccess extends MojangsonLocationAccess<MojangsonCompound, String> {
            private CompoundLocationAccess(@NotNull MojangsonCompound value, @NotNull String key) {
                super(value, key);
            }

            @Override
            public boolean has() {
                return structure.hasKey(key);
            }

            @Override
            public @NotNull MojangsonValueType<?> getType() {
                return structure.getTypeOfKey(key);
            }

            @Override
            public <P extends MojangsonValue<?>> @NotNull P get(@NotNull MojangsonValueType<P> type) {
                return structure.getKey(key, type);
            }

            @Override
            public <P> void set(P value) {
                structure.setKey(key, value);
            }

            @Override
            public void delete() {
                structure.deleteKey(key);
            }
        }

        private static final class ListLocationAccess extends MojangsonLocationAccess<MojangsonList, Integer> {
            private ListLocationAccess(@NotNull MojangsonList value, @NotNull Integer key) {
                super(value, key);
            }

            @Override
            public boolean has() {
                return structure.has(key);
            }

            @Override
            public @NotNull MojangsonValueType<?> getType() {
                return structure.getTypeAt(key);
            }

            @Override
            public <P extends MojangsonValue<?>> @NotNull P get(@NotNull MojangsonValueType<P> type) {
                return structure.get(key, type);
            }

            @Override
            public <P> void set(P value) {
                structure.set(key, value);
            }

            @Override
            public void delete() {
                structure.delete(key);
            }
        }
    }
}
