package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * コンポーネントタイプを表現するクラス
 * @param <T> コンポーネントクラス
 */
public class ItemStackComponentType<T extends ItemStackComponent> {
    private final Class<T> clazz;

    private static final Map<Class<? extends ItemStackComponent>, ItemStackComponentType<? extends ItemStackComponent>> types = new HashMap<>();

    protected ItemStackComponentType(Class<T> type) {
        if (types.containsKey(type)) {
            throw new IllegalArgumentException("既に作成されています");
        }

        this.clazz = type;
        types.put(type, this);
    }

    @NotNull T getInstance(ItemStack itemStack) {
        try {
            final Constructor<T> constructor = clazz.getDeclaredConstructor(ItemStack.class);
            constructor.setAccessible(true);
            return constructor.newInstance(itemStack);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InvalidComponentTypeException("コンポーネントの取得に失敗しました", e);
        }
    }

    /**
     * 全種類のコンポーネントタイプを取得します。
     *
     * @return 読み取り専用のコンポーネントタイプSet
     */
    public static Set<ItemStackComponentType<? extends ItemStackComponent>> values() {
        return Set.copyOf(types.values());
    }

    public static final ItemStackComponentType<AttributeModifiersComponent> ATTRIBUTE_MODIFIERS = new ItemStackComponentType<>(AttributeModifiersComponent.class);

    // minecraft:banner_patterns

    public static final ItemStackComponentType<BaseColorComponent> BASE_COLOR = new ItemStackComponentType<>(BaseColorComponent.class);

    // minecraft:bees

    // minecraft:block_entity_data

    // minecraft:block_state

    // minecraft:bucket_entity_data

    // minecraft:bundle_contents

    private static final ItemStackComponentType<CanBreakComponent> CAN_BREAK = new ItemStackComponentType<>(CanBreakComponent.class);

    private static final ItemStackComponentType<CanPlaceOnComponent> CAN_PLACE_ON = new ItemStackComponentType<>(CanPlaceOnComponent.class);

    public static final ItemStackComponentType<ChargedProjectilesComponent> CHARGED_PROJECTILES = new ItemStackComponentType<>(ChargedProjectilesComponent.class);

    // minecraft:container

    // minecraft:container_loot

    // minecraft:custom_data

    public static final ItemStackComponentType<CustomModelDataComponent> CUSTOM_MODEL_DATA = new ItemStackComponentType<>(CustomModelDataComponent.class);

    public static final ItemStackComponentType<CustomNameComponent> CUSTOM_NAME = new ItemStackComponentType<>(CustomNameComponent.class);

    public static final ItemStackComponentType<DamageComponent> DAMAGE = new ItemStackComponentType<>(DamageComponent.class);

    // minecraft:debug_stick_state

    public static final ItemStackComponentType<DyedColorComponent> DYED_COLOR = new ItemStackComponentType<>(DyedColorComponent.class);

    private static final ItemStackComponentType<EnchantableComponent> ENCHANTABLE = new ItemStackComponentType<>(EnchantableComponent.class);

    public static final ItemStackComponentType<EnchantmentGlintOverrideComponent> ENCHANT_GLINT_OVERRIDE = new ItemStackComponentType<>(EnchantmentGlintOverrideComponent.class);

    public static final ItemStackComponentType<EnchantmentsComponent> ENCHANTMENTS = new ItemStackComponentType<>(EnchantmentsComponent.class);

    // minecraft:entity_data

    public static final ItemStackComponentType<FireResistantComponent> FIRE_RESISTANT = new ItemStackComponentType<>(FireResistantComponent.class);

    // minecraft:firework_explosion 実装予定

    // minecraft:fireworks 実装予定

    public static final ItemStackComponentType<FoodComponent> FOOD = new ItemStackComponentType<>(FoodComponent.class);

    public static final ItemStackComponentType<HideAdditionalTooltipComponent> HIDE_ADDITIONAL_TOOLTIP = new ItemStackComponentType<>(HideAdditionalTooltipComponent.class);

    public static final ItemStackComponentType<HideTooltipComponent> HIDE_TOOLTIP = new ItemStackComponentType<>(HideTooltipComponent.class);

    // minecraft:instrument

    // minecraft:intangible_projectile

    public static final ItemStackComponentType<ItemNameComponent> ITEM_NAME = new ItemStackComponentType<>(ItemNameComponent.class);

    // minecraft:jukebox_playable 実装予定

    // minecraft:lock

    // minecraft:lodestone_tracker 実装予定

    public static final ItemStackComponentType<LoreComponent> LORE = new ItemStackComponentType<>(LoreComponent.class);

    // minecraft:map_color

    // minecraft:map_decorations

    // minecraft:map_id

    public static final ItemStackComponentType<MaxDamageComponent> MAX_DAMAGE = new ItemStackComponentType<>(MaxDamageComponent.class);

    public static final ItemStackComponentType<MaxStackSizeComponent> MAX_STACK_SIZE = new ItemStackComponentType<>(MaxStackSizeComponent.class);

    // minecraft:note_block_sound

    // minecraft:ominous_bottle_amplifier

    // minecraft:pot_decorations

    public static final ItemStackComponentType<PotionContentsComponent> POTION_CONTENTS = new ItemStackComponentType<>(PotionContentsComponent.class);

    public static final ItemStackComponentType<ProfileComponent> PROFILE = new ItemStackComponentType<>(ProfileComponent.class);

    // minecraft:rarity

    public static final ItemStackComponentType<RecipesComponent> RECIPES = new ItemStackComponentType<>(RecipesComponent.class);

    public static final ItemStackComponentType<RepairCostComponent> REPAIR_COST = new ItemStackComponentType<>(RepairCostComponent.class);

    // minecraft:repairable

    public static final ItemStackComponentType<StoredEnchantmentsComponent> STORED_ENCHANTMENTS = new ItemStackComponentType<>(StoredEnchantmentsComponent.class);

    // minecraft:suspicious_stew_effects

    private static final ItemStackComponentType<ToolComponent> TOOL = new ItemStackComponentType<>(ToolComponent.class);

    public static final ItemStackComponentType<TrimComponent> TRIM = new ItemStackComponentType<>(TrimComponent.class);

    public static final ItemStackComponentType<UnbreakableComponent> UNBREAKABLE = new ItemStackComponentType<>(UnbreakableComponent.class);

    // minecraft:writable_book_content

    // minecraft:written_book_content
}
