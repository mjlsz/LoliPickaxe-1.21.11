package com.anotherstar.registry;

import com.anotherstar.LoliPickaxe;
import com.anotherstar.common.item.ItemAddon;
import com.anotherstar.common.item.ItemBugEntityClear;
import com.anotherstar.common.item.ItemLoliCard;
import com.anotherstar.common.item.ItemLoliCardAlbum;
import com.anotherstar.common.item.ItemLoliCardOnline;
import com.anotherstar.common.item.ItemLoliDispersal;
import com.anotherstar.common.item.ItemLoliPickaxe;
import com.anotherstar.common.item.ItemLoliRecord;
import com.anotherstar.common.item.ItemSmallLoliPickaxe;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LoliPickaxe.MODID);

    /** addon registry name -> max level (mirrors the 1.12.2 ItemLoader constructor arguments). */
    private static final Map<String, Integer> ADDON_LEVELS = new LinkedHashMap<>();
    private static final Map<String, Boolean> ADDON_DIFFERENT_END = new LinkedHashMap<>();

    static {
        addon("loli_coal_addon", 10, false);
        addon("loli_iron_addon", 10, false);
        addon("loli_gold_addon", 7, false);
        addon("loli_redstone_addon", 4, false);
        addon("loli_lapis_addon", 6, false);
        addon("loli_diamond_addon", 6, false);
        addon("loli_emerald_addon", 5, false);
        addon("loli_obsidian_addon", 10, false);
        addon("loli_glow_addon", 3, false);
        addon("loli_quartz_addon", 3, false);
        addon("loli_nether_star_addon", 5, false);
        addon("loli_auto_furnace_addon", 1, false);
        addon("loli_fly_addon", 1, false);
        addon("loli_entity_soul_addon", 7, true);
    }

    private static void addon(String name, int maxLevel, boolean differentEnd) {
        ADDON_LEVELS.put(name, maxLevel);
        ADDON_DIFFERENT_END.put(name, differentEnd);
    }

    public static int addonMaxLevel(String name) {
        return ADDON_LEVELS.getOrDefault(name, 1);
    }

    public static DeferredItem<ItemLoliPickaxe> LOLI_PICKAXE = ITEMS.registerItem("loli_pickaxe",
            ItemLoliPickaxe::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    public static DeferredItem<ItemSmallLoliPickaxe> SMALL_LOLI_PICKAXE = ITEMS.registerItem("small_loli_pickaxe",
            ItemSmallLoliPickaxe::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant());

    public static final DeferredItem<ItemAddon> COAL_ADDON = addonItem("loli_coal_addon");
    public static final DeferredItem<ItemAddon> IRON_ADDON = addonItem("loli_iron_addon");
    public static final DeferredItem<ItemAddon> GOLD_ADDON = addonItem("loli_gold_addon");
    public static final DeferredItem<ItemAddon> REDSTONE_ADDON = addonItem("loli_redstone_addon");
    public static final DeferredItem<ItemAddon> LAPIS_ADDON = addonItem("loli_lapis_addon");
    public static final DeferredItem<ItemAddon> DIAMOND_ADDON = addonItem("loli_diamond_addon");
    public static final DeferredItem<ItemAddon> EMERALD_ADDON = addonItem("loli_emerald_addon");
    public static final DeferredItem<ItemAddon> OBSIDIAN_ADDON = addonItem("loli_obsidian_addon");
    public static final DeferredItem<ItemAddon> GLOW_ADDON = addonItem("loli_glow_addon");
    public static final DeferredItem<ItemAddon> QUARTZ_ADDON = addonItem("loli_quartz_addon");
    public static final DeferredItem<ItemAddon> NETHER_STAR_ADDON = addonItem("loli_nether_star_addon");
    public static final DeferredItem<ItemAddon> AUTO_FURNACE_ADDON = addonItem("loli_auto_furnace_addon");
    public static final DeferredItem<ItemAddon> FLY_ADDON = addonItem("loli_fly_addon");
    public static final DeferredItem<ItemAddon> ENTITY_SOUL_ADDON = addonItem("loli_entity_soul_addon");

    public static java.util.List<DeferredItem<ItemAddon>> addonItems() {
        return java.util.List.of(COAL_ADDON, IRON_ADDON, GOLD_ADDON, REDSTONE_ADDON, LAPIS_ADDON, DIAMOND_ADDON,
                EMERALD_ADDON, OBSIDIAN_ADDON, GLOW_ADDON, QUARTZ_ADDON, NETHER_STAR_ADDON, AUTO_FURNACE_ADDON, FLY_ADDON,
                ENTITY_SOUL_ADDON);
    }

    public static final DeferredItem<ItemLoliDispersal> LOLI_DISPERSAL = ITEMS.registerItem("loli_dispersal",
            ItemLoliDispersal::new, new Item.Properties().stacksTo(16));
    public static final DeferredItem<ItemBugEntityClear> BUG_ENTITY_CLEAR = ITEMS.registerItem("bug_entity_clear",
            ItemBugEntityClear::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<ItemLoliCard> LOLI_CARD = ITEMS.registerItem("loli_card",
            ItemLoliCard::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<ItemLoliCardAlbum> LOLI_CARD_ALBUM = ITEMS.registerItem("loli_card_album",
            ItemLoliCardAlbum::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<ItemLoliCardOnline> LOLI_CARD_ONLINE = ITEMS.registerItem("loli_card_online",
            ItemLoliCardOnline::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<ItemLoliRecord> LOLI_RECORD = ITEMS.registerItem("loli_record",
            ItemLoliRecord::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    private static DeferredItem<ItemAddon> addonItem(String name) {
        int max = ADDON_LEVELS.get(name);
        boolean end = ADDON_DIFFERENT_END.get(name);
        return ITEMS.registerItem(name, p -> new ItemAddon(p, name, max, end), new Item.Properties().stacksTo(1));
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    private ModItems() {
    }
}
