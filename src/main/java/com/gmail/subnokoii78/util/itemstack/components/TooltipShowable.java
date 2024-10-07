package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class TooltipShowable extends ItemStackComponent {
    protected TooltipShowable(@NotNull ItemStack itemStack) {
        super(itemStack);
    }

    protected abstract @NotNull ItemFlag getItemFlag();

    /**
     * このコンポーネントがツールチップに表示されるかどうかを返します。
     * @return 表示されるなら真
     */
    public final boolean getShowInTooltip() {
        return !itemStack.getItemMeta().hasItemFlag(getItemFlag());
    }

    /**
     * このコンポーネントをツールチップに表示するかどうかを変更します。
     * @param flag 真であれば表示
     */
     public final void setShowInTooltip(boolean flag) {
         itemMetaModifier(itemMeta -> {
             if (flag) itemMeta.removeItemFlags(getItemFlag());
             else itemMeta.addItemFlags(getItemFlag());
         });
     }
}
