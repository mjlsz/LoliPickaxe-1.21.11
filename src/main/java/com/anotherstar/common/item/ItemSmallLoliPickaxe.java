package com.anotherstar.common.item;

import com.anotherstar.common.config.Config;
import com.anotherstar.registry.ModSounds;
import com.anotherstar.util.LoliData;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/**
 * "普通萝莉" (Small Loli Pickaxe): the survival-obtainable pickaxe.
 *
 * <p>Upgrades live in the stack's custom data as integer levels, exactly like the
 * 1.12.2 version stored them in {@code NBTTagCompound}. The {@code getXxxValue}
 * methods are copied verbatim from the original.</p>
 */
public class ItemSmallLoliPickaxe extends Item implements ILoliStorageHolder {

    /** addon item id -> nbt key, in the same order as the original. */
    public static final Map<String, String> ADDON_KEYS = new LinkedHashMap<>();

    static {
        ADDON_KEYS.put("loli_coal_addon", "LoliDodge");
        ADDON_KEYS.put("loli_iron_addon", "LoliDiggingSpeed");
        ADDON_KEYS.put("loli_gold_addon", "LoliAttackDamage");
        ADDON_KEYS.put("loli_redstone_addon", "LoliAttackSpeed");
        ADDON_KEYS.put("loli_lapis_addon", "LoliFortuneLevel");
        ADDON_KEYS.put("loli_diamond_addon", "LoliDiggingLevel");
        ADDON_KEYS.put("loli_emerald_addon", "LoliDiggingRange");
        ADDON_KEYS.put("loli_obsidian_addon", "LoliAntiInjury");
        ADDON_KEYS.put("loli_glow_addon", "LoliBuff");
        ADDON_KEYS.put("loli_quartz_addon", "LoliHitRange");
        ADDON_KEYS.put("loli_nether_star_addon", "LoliBackpackPage");
        ADDON_KEYS.put("loli_auto_furnace_addon", "LoliAutoFurnace");
        ADDON_KEYS.put("loli_fly_addon", "LoliFly");
    }

    public ItemSmallLoliPickaxe(Properties properties) {
        super(properties);
    }

    /** A pickaxe with every upgrade maxed, used by the creative tab. */
    public static ItemStack createFull() {
        ItemStack stack = new ItemStack(com.anotherstar.registry.ModItems.SMALL_LOLI_PICKAXE.get());
        for (Map.Entry<String, String> e : ADDON_KEYS.entrySet()) {
            int max = com.anotherstar.registry.ModItems.addonMaxLevel(e.getKey());
            LoliData.setInt(stack, e.getValue(), max);
        }
        return stack;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && !level.isClientSide()) {
            int maxPage = getMaxPage(stack);
            if (maxPage > 0) {
                com.anotherstar.common.gui.LoliStorageMenu.open(player, player.getInventory().getSelectedSlot());
                return InteractionResult.SUCCESS;
            }
        }
        if (!LoliData.has(stack, "LoliDiggingRange")) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        int maxRange = (getTransformValue("LoliDiggingRange", LoliData.getInt(stack, "LoliDiggingRange", 0)) - 1) / 2 + 1;
        int current = (LoliData.getInt(stack, "LoliCurrentDiggingRange", 1) + 1) % maxRange;
        LoliData.setInt(stack, "LoliCurrentDiggingRange", current);
        player.displayClientMessage(Component.translatable("loliPickaxe.range", 1 + 2 * current), false);
        level.playSound(null, player.blockPosition(), ModSounds.LOLISUCCESS.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId()).withStyle(ChatFormatting.RED);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        for (String key : ADDON_KEYS.values()) {
            if (LoliData.has(stack, key)) {
                int level = LoliData.getInt(stack, key, 0);
                double asDouble = getDoubleTransformValue(key, level);
                if (asDouble == Integer.MIN_VALUE) {
                    tooltip.accept(Component.translatable("smallLoliPickaxe." + key, getTransformValue(key, level)));
                } else {
                    tooltip.accept(Component.translatable("smallLoliPickaxe." + key, asDouble));
                }
            }
        }
    }

    public int getTransformValue(String key, int level) {
        switch (key) {
            case "LoliDiggingSpeed":
                return 4 << level;
            case "LoliDiggingLevel":
                return switch (level) {
                    case 0 -> 1;
                    case 1 -> 3;
                    case 2 -> 7;
                    case 3 -> 13;
                    case 4 -> 21;
                    case 5 -> 32;
                    default -> -1;
                };
            case "LoliDiggingRange":
                return level * 2 + 3;
            case "LoliAttackSpeed":
                return 2 << level;
            case "LoliFortuneLevel":
                return 1 << level;
            case "LoliBackpackPage":
                return 2 << level;
            case "LoliBuff":
                return level + 1;
            case "LoliHitRange":
                return level * 10 + 6;
            case "LoliAutoFurnace":
                return level;
            case "LoliFly":
                return level;
            default:
                return Integer.MIN_VALUE;
        }
    }

    public double getDoubleTransformValue(String key, int level) {
        return switch (key) {
            case "LoliDodge" -> (level + 1) / 10.0;
            case "LoliAttackDamage" -> 4.0 + Math.pow(2.0, Math.pow(2.0, level));
            case "LoliAntiInjury" -> (level + 1) / 10.0;
            default -> Integer.MIN_VALUE;
        };
    }

    public int getRange(ItemStack stack) {
        return LoliData.getInt(stack, "LoliCurrentDiggingRange", 0);
    }

    public boolean canFly(ItemStack stack) {
        return LoliData.has(stack, "LoliFly") && getTransformValue("LoliFly", LoliData.getInt(stack, "LoliFly", 0)) == 0;
    }

    public double getDodge(ItemStack stack) {
        return LoliData.has(stack, "LoliDodge") ? getDoubleTransformValue("LoliDodge", LoliData.getInt(stack, "LoliDodge", 0)) : 0.0D;
    }

    public double getAntiInjury(ItemStack stack) {
        return LoliData.has(stack, "LoliAntiInjury") ? getDoubleTransformValue("LoliAntiInjury", LoliData.getInt(stack, "LoliAntiInjury", 0)) : 0.0D;
    }

    public int buffLevel(ItemStack stack) {
        return LoliData.has(stack, "LoliBuff") ? getTransformValue("LoliBuff", LoliData.getInt(stack, "LoliBuff", 0)) : -1;
    }

    public int getMaxPage(ItemStack stack) {
        if (!LoliData.has(stack, "LoliBackpackPage")) {
            return 0;
        }
        return Math.min(getTransformValue("LoliBackpackPage", LoliData.getInt(stack, "LoliBackpackPage", 0)), Config.MAX_PAGE.get());
    }

    public double attackDamage(ItemStack stack) {
        return LoliData.has(stack, "LoliAttackDamage") ? getDoubleTransformValue("LoliAttackDamage", LoliData.getInt(stack, "LoliAttackDamage", 0)) : 1.0D;
    }

    public int miningRange(ItemStack stack) {
        return getRange(stack);
    }

    public static void playSuccessAt(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.LOLISUCCESS.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public int storagePages(ItemStack stack) {
        return Math.max(1, getMaxPage(stack));
    }
}

