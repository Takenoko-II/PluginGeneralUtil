package com.gmail.subnokoii78.util.execute;

import java.util.function.BiConsumer;

/**
 * {@link ResultCallback}にチェーンするときに使用する関数型インターフェース
 */
@FunctionalInterface
public interface ResultConsumer extends BiConsumer<SourceStack, Integer> {}
