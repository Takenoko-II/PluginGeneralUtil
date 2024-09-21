package com.gmail.subnokoii78.util.execute;

import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.jetbrains.annotations.NotNull;

/**
 * ソーススタックと結びついたエンティティアンカーを表現するクラス
 */
public final class EntityAnchor {
    private final EntityAnchorType anchor;

    private final SourceStack stack;

    EntityAnchor(@NotNull EntityAnchorType anchor, @NotNull SourceStack stack) {
        this.anchor = anchor;
        this.stack = stack;
    }

    /**
     * エンティティアンカーの種類を取得します。
     * @return eyesまたはfeet
     */
    public @NotNull EntityAnchorType getType() {
        return anchor;
    }

    /**
     * エンティティアンカーによる実行座標のオフセットを取得します。
     * @return アンカーオフセット
     */
    public @NotNull Vector3Builder getOffset() {
        return anchor.provideOffset(stack.getExecutor());
    }
}
