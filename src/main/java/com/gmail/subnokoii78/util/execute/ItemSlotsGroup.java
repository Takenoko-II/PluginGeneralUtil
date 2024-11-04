package com.gmail.subnokoii78.util.execute;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * アイテムスロットのグループを表現するクラス
 * @param <T> そのスロットグループを取得できるクラス
 * @param <U> スロットグループからスロットを取得するときに必要な値の型
 */
public abstract class ItemSlotsGroup<T, U> {
    /**
     * コンテナスロット
     */
    public static final ItemSlotsGroup<InventoryHolder, Integer> CONTAINER = new ItemSlotsGroup<>(InventoryHolder.class) {
        @Override
        protected @Nullable ItemStack getItemStack(@NotNull InventoryHolder target, @NotNull Integer arg) {
            final LevelRange range = LevelRange.of("0..53");

            if (!(range.min() <= arg && arg <= range.max())) {
                throw new IllegalArgumentException("containerの値は0～53です");
            }

            return target.getInventory().getItem(arg);
        }

        @Override
        protected @NotNull List<Integer> getArgumentCandidates() {
            final List<Integer> list = new ArrayList<>();
            for (int i = 0; i <= 53; i++) {
                list.add(i);
            }
            return list;
        }
    };

    /**
     * エンダーチェスト
     */
    public static final ItemSlotsGroup<HumanEntity, Integer> ENDERCHEST = new ItemSlotsGroup<>(HumanEntity.class) {
        @Override
        protected @Nullable ItemStack getItemStack(@NotNull HumanEntity target, @NotNull Integer arg) {
            final LevelRange range = LevelRange.of("0..26");

            if (!(range.min() <= arg && arg <= range.max())) {
                throw new IllegalArgumentException("enderchestの値は0～26です");
            }

            return target.getEnderChest().getItem(arg);
        }

        @Override
        protected @NotNull List<Integer> getArgumentCandidates() {
            final List<Integer> list = new ArrayList<>();
            for (int i = 0; i <= 26; i++) {
                list.add(i);
            }
            return list;
        }
    };

    /**
     * ホットバー
     */
    public static final ItemSlotsGroup<HumanEntity, Integer> HOTBAR = new ItemSlotsGroup<>(HumanEntity.class) {
        @Override
        protected @Nullable ItemStack getItemStack(@NotNull HumanEntity target, @NotNull Integer arg) {
            final LevelRange range = LevelRange.of("0..8");

            if (!(range.min() <= arg && arg <= range.max())) {
                throw new IllegalArgumentException("hotbarの値は0～8です");
            }

            return target.getInventory().getItem(arg);
        }

        @Override
        protected @NotNull List<Integer> getArgumentCandidates() {
            final List<Integer> list = new ArrayList<>();
            for (int i = 0; i <= 8; i++) {
                list.add(i);
            }
            return list;
        }
    };

    /**
     * インベントリ
     */
    public static final ItemSlotsGroup<InventoryHolder, Integer> INVENTORY = new ItemSlotsGroup<>(InventoryHolder.class) {
        @Override
        protected @Nullable ItemStack getItemStack(@NotNull InventoryHolder target, @NotNull Integer arg) {
            final LevelRange range = LevelRange.of("0..26");

            if (!(range.min() <= arg && arg <= range.max())) {
                throw new IllegalArgumentException("inventoryの値は0～26です");
            }

            return target.getInventory().getItem(arg);
        }

        @Override
        protected @NotNull List<Integer> getArgumentCandidates() {
            final List<Integer> list = new ArrayList<>();
            for (int i = 0; i <= 26; i++) {
                list.add(i);
            }
            return list;
        }
    };

    /**
     * 馬のインベントリ
     */
    public static final ItemSlotsGroup<AbstractHorse, Integer> HORSE = new ItemSlotsGroup<>(AbstractHorse.class) {
        @Override
        protected @Nullable ItemStack getItemStack(@NotNull AbstractHorse target, @NotNull Integer arg) {
            final LevelRange range = LevelRange.of("0..14");

            if (!(range.min() <= arg && arg <= range.max())) {
                throw new IllegalArgumentException("horseの値は0～14です");
            }

            return target.getInventory().getItem(arg);
        }

        @Override
        protected @NotNull List<Integer> getArgumentCandidates() {
            final List<Integer> list = new ArrayList<>();
            for (int i = 0; i <= 14; i++) {
                list.add(i);
            }
            return list;
        }
    };

    /**
     * 村人のインベントリ
     */
    public static final ItemSlotsGroup<Villager, Integer> VILLAGER = new ItemSlotsGroup<>(Villager.class) {
        @Override
        protected @Nullable ItemStack getItemStack(@NotNull Villager target, @NotNull Integer arg) {
            final LevelRange range = LevelRange.of("0..7");

            if (!(range.min() <= arg && arg <= range.max())) {
                throw new IllegalArgumentException("villagerの値は0～7です");
            }

            return target.getInventory().getItem(arg);
        }

        @Override
        protected @NotNull List<Integer> getArgumentCandidates() {
            final List<Integer> list = new ArrayList<>();
            for (int i = 0; i <= 7; i++) {
                list.add(i);
            }
            return list;
        }
    };

    /**
     * プレイヤーのインベントリ内カーソル
     */
    public static final ItemSlotsGroup<HumanEntity, Void> PLAYER_CURSOR = new ItemSlotsGroup<>(HumanEntity.class) {
        @Override
        protected @Nullable ItemStack getItemStack(@NotNull HumanEntity target, @NotNull Void arg) {
            final ItemStack itemStack = target.getItemOnCursor();

            if (itemStack.getItemMeta() == null) return null;
            else return itemStack;
        }

        @Override
        protected @NotNull List<Void> getArgumentCandidates() {
            try {
                final var constructor = Void.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                return List.of(constructor.newInstance());
            }
            catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * 防具スロット
     */
    public static final ItemSlotsGroup<LivingEntity, ArmorSlots> ARMOR = new ItemSlotsGroup<>(LivingEntity.class) {
        @Override
        protected @Nullable ItemStack getItemStack(@NotNull LivingEntity target, @NotNull ArmorSlots arg) {
            final EntityEquipment equipment = target.getEquipment();
            if (equipment == null) return null;
            return arg.get(equipment);
        }

        @Override
        protected @NotNull List<ArmorSlots> getArgumentCandidates() {
            return Arrays.stream(ArmorSlots.values()).toList();
        }
    };

    /**
     * メインハンドまたはオフハンド
     */
    public static final ItemSlotsGroup<LivingEntity, WeaponSlots> WEAPON = new ItemSlotsGroup<>(LivingEntity.class) {
        @Override
        protected @Nullable ItemStack getItemStack(@NotNull LivingEntity target, @NotNull WeaponSlots arg) {
            final EntityEquipment equipment = target.getEquipment();
            if (equipment == null) return null;
            return arg.get(equipment);
        }

        @Override
        protected @NotNull List<WeaponSlots> getArgumentCandidates() {
            return Arrays.stream(WeaponSlots.values()).toList();
        }
    };

    private final Class<T> clazz;

    private ItemSlotsGroup(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    protected @Nullable T tryCastTarget(@NotNull Object target) {
        if (clazz.isInstance(target)) {
            return clazz.cast(target);
        }
        else return null;
    }

    protected abstract @Nullable ItemStack getItemStack(@NotNull T target, @NotNull U arg);

    protected abstract @NotNull List<U> getArgumentCandidates();

    private boolean matches(@NotNull T target, @NotNull U arg, @NotNull Predicate<ItemStack> predicate) {
        return predicate.test(getItemStack(target, arg));
    }

    private boolean matches(@NotNull T target, @NotNull Predicate<ItemStack> predicate) {
        for (final U candidate : getArgumentCandidates()) {
            if (predicate.test(getItemStack(target, candidate))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 引数に基づいてスロットを取得します。
     * @param argument スロットの種類に応じた引数
     * @return スロット
     */
    public @NotNull ItemSlotsMatcher<T, U> getSlot(@NotNull U argument) {
        return new ItemSlotsMatcher<>(this, argument);
    }

    /**
     * このスロットグループのスロットを全て取得します。
     * @return 全スロット
     */
    public @NotNull ItemSlotsMatcher<T, U> getAllSlots() {
        return new ItemSlotsMatcher<>(this);
    }

    /**
     * アイテムスロットの集合を表現するクラス
     * @param <T> スロットグループを取得できるクラス
     * @param <U> スロットグループからスロットを取得するときに必要な値の型
     */
    public static final class ItemSlotsMatcher<T, U> {
        private final ItemSlotsGroup<T, U> group;

        private final U argument;

        private ItemSlotsMatcher(@NotNull ItemSlotsGroup<T, U> group, @NotNull U argument) {
            this.group = group;
            this.argument = argument;
        }

        private ItemSlotsMatcher(@NotNull ItemSlotsGroup<T, U> group) {
            this.group = group;
            this.argument = null;
        }

        public @NotNull ItemSlotsGroup<T, U> getGroup() {
            return group;
        }

        /**
         * このスロット群に条件に一致するスロットが含まれているかをテストします。
         * @param target 調べる対象
         * @param predicate 条件
         * @return 条件に一致するスロットが含まれていればtrue、それ以外はfalse
         */
        public boolean matches(@NotNull T target, @NotNull Predicate<ItemStack> predicate) {
            if (argument == null) {
                return group.matches(target, predicate);
            }
            else return group.matches(target, argument, predicate);
        }
    }

    /**
     * 防具スロットの列挙型
     */
    public enum ArmorSlots {
        /**
         * ヘルメットスロット
         */
        HEAD {
            @Override
            protected @Nullable ItemStack get(@NotNull EntityEquipment equipment) {
                return equipment.getHelmet();
            }
        },

        /**
         * チェストプレートスロット
         */
        CHEST {
            @Override
            protected @Nullable ItemStack get(@NotNull EntityEquipment equipment) {
                return equipment.getChestplate();
            }
        },

        /**
         * レギンススロット
         */
        LEGS {
            @Override
            protected @Nullable ItemStack get(@NotNull EntityEquipment equipment) {
                return equipment.getLeggings();
            }
        },

        /**
         * ブーツスロット
         */
        FEET {
            @Override
            protected @Nullable ItemStack get(@NotNull EntityEquipment equipment) {
                return equipment.getBoots();
            }
        };

        protected abstract @Nullable ItemStack get(@NotNull EntityEquipment equipment);
    }

    /**
     * メインハンドまたはオフハンドの列挙型
     */
    public enum WeaponSlots {
        /**
         * メインハンドスロット
         */
        MAINHAND {
            @Override
            protected @Nullable ItemStack get(@NotNull EntityEquipment equipment) {
                final ItemStack itemStack = equipment.getItem(EquipmentSlot.HAND);
                if (itemStack.getItemMeta() == null) return null;
                return itemStack;
            }
        },

        /**
         * オフハンドスロット
         */
        OFFHAND {
            @Override
            protected @Nullable ItemStack get(@NotNull EntityEquipment equipment) {
                final ItemStack itemStack = equipment.getItem(EquipmentSlot.OFF_HAND);
                if (itemStack.getItemMeta() == null) return null;
                return itemStack;
            }
        };

        protected abstract @Nullable ItemStack get(@NotNull EntityEquipment equipment);
    }
}
