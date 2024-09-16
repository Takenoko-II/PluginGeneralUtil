package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.jetbrains.annotations.NotNull;

public final class EntityAnchor {
    private final EntityAnchorType anchor;

    private final SourceStack stack;

    EntityAnchor(@NotNull EntityAnchorType anchor, @NotNull SourceStack stack) {
        this.anchor = anchor;
        this.stack = stack;
    }

    public @NotNull EntityAnchorType getType() {
        return anchor;
    }

    public @NotNull Vector3Builder getOffset() {
        return anchor.provideOffset(stack.getExecutor());
    }
}
