package com.gmail.subnokoii78.util.execute;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ResultConsumer extends BiConsumer<SourceStack, Integer> {}
