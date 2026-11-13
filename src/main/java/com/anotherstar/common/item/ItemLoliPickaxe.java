package com.anotherstar.common.item;

import com.anotherstar.common.config.Config;
import com.anotherstar.registry.ModSounds;
import com.anotherstar.util.LoliData;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/**
 * The "氪金萝莉" (Loli Pickaxe): a creative-tier pickaxe that instantly kills
 * whatever it touches. Ported from {@code ItemLoliPickaxe}.
 */
public class ItemLoliPickaxe extends Item implements ILoliStorageHolder {

    public ItemLoliPickaxe(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        // Store potion effects from a potion in the off hand ("药水氪金萝莉").
        ItemStack potion = player.getOffhandItem();
        if (potion.is(net.minecraft.world.item.Items.POTION)) {
            var contents = potion.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
            if (contents != null && contents.hasEffects()) {
                net.minecraft.nbt.ListTag list = LoliData.getList(stack, "LoliPotion");
                for (var effect : contents.getAllEffects()) {
                    String id = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT
                            .getKey(effect.getEffect().value()).toString();
                    boolean replaced = false;
                    for (int i = 0; i < list.size(); i++) {
                        var entry = list.getCompoundOrEmpty(i);
                        if (entry.getStringOr("id", "").equals(id)) {
                            entry.putInt("lvl", Math.max(entry.getIntOr("lvl", 0), effect.getAmplifier()));
                            replaced = true;
                            break;
                        }
                    }
                    if (!replaced) {
                        var entry = new net.minecraft.nbt.CompoundTag();
                        entry.putString("id", id);
                        entry.putInt("lvl", effect.getAmplifier());
                        list.add(entry);
                    }
                }
                LoliData.setList(stack, "LoliPotion", list);
                potion.shrink(1);
                player.displayClientMessage(Component.translatable("loliPickaxe.potion"), true);
                playSuccess(level, player.blockPosition());
                return InteractionResult.SUCCESS;
            }
        }
        // Apply an enchanted book held in the off hand (the 1.12.2 GUI did this through a menu).
        ItemStack offhand = player.getOffhandItem();
        if (offhand.is(net.minecraft.world.item.Items.ENCHANTED_BOOK)) {
            var stored = offhand.get(net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS);
            if (stored != null && !stored.isEmpty()) {
                net.minecraft.world.item.enchantment.EnchantmentHelper.updateEnchantments(stack, mutable -> {
                    for (var entry : stored.entrySet()) {
                        mutable.upgrade(entry.getKey(), entry.getIntValue());
                    }
                });
                offhand.shrink(1);
                player.displayClientMessage(Component.translatable("loliPickaxe.enchant"), true);
                playSuccess(level, player.blockPosition());
                return InteractionResult.SUCCESS;
            }
        }
        if (player.isShiftKeyDown()) {
            if (Config.getBoolean(stack, "loliPickaxeKillRangeEntity")) {
                int range = Config.getInt(stack, "loliPickaxeKillRange");
                int count = com.anotherstar.util.LoliUtil.killRangeEntity(level, player, range);
                player.displayClientMessage(Component.translatable("loliPickaxe.killrangeentity", range * 2, count), false);
                playSuccess(level, player.blockPosition());
            }
        } else {
            int current = getRange(stack) + 1;
            if (current > Config.MAX_RANGE.get()) {
                current = 0;
            }
            LoliData.setInt(stack, "range", current);
            player.displayClientMessage(Component.translatable("loliPickaxe.range", 1 + 2 * current), false);
            playSuccess(level, player.blockPosition());
        }
        return InteractionResult.SUCCESS;
    }

    public static void playSuccess(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.LOLISUCCESS.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        if (entity instanceof Player player && !LoliData.has(stack, "Owner")) {
            LoliData.setString(stack, "Owner", player.getScoreboardName());
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId()).withStyle(ChatFormatting.RED);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.literal("已在 GitHub 上开源").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("loliPickaxe.curRange", 1 + 2 * getRange(stack)));

        addIf(stack, tooltip, "loliPickaxeMandatoryDrop", "loliPickaxe.mandatoryDrop");
        addIf(stack, tooltip, "loliPickaxeAutoAccept", "loliPickaxe.autoAccept");
        addIf(stack, tooltip, "loliPickaxeThorns", "loliPickaxe.thorns");
        if (Config.getBoolean(stack, "loliPickaxeKillRangeEntity")) {
            tooltip.accept(Component.translatable("loliPickaxe.killRange", 2 * Config.getInt(stack, "loliPickaxeKillRange")));
        }
        if (Config.getBoolean(stack, "loliPickaxeAutoKillRangeEntity")) {
            tooltip.accept(Component.translatable("loliPickaxe.autoKillRange", 2 * Config.getInt(stack, "loliPickaxeAutoKillRange")));
        }
        addIf(stack, tooltip, "loliPickaxeCompulsoryRemove", "loliPickaxe.compulsoryRemove");
        addIf(stack, tooltip, "loliPickaxeValidToAmityEntity", "loliPickaxe.validToAmityEntity");
        addIf(stack, tooltip, "loliPickaxeValidToAllEntity", "loliPickaxe.validToAllEntity");
        addIf(stack, tooltip, "loliPickaxeClearInventory", "loliPickaxe.clearInventory");
        addIf(stack, tooltip, "loliPickaxeDropItems", "loliPickaxe.dropItems");
        addIf(stack, tooltip, "loliPickaxeKickPlayer", "loliPickaxe.kickPlayer");
        addIf(stack, tooltip, "loliPickaxeReincarnation", "loliPickaxe.reincarnation");
        addIf(stack, tooltip, "loliPickaxeBeyondRedemption", "loliPickaxe.beyondRedemption");
        addIf(stack, tooltip, "loliPickaxeBlueScreenAttack", "loliPickaxe.blueScreenAttack");
        addIf(stack, tooltip, "loliPickaxeExitAttack", "loliPickaxe.exitAttack");
        addIf(stack, tooltip, "loliPickaxeFailRespondAttack", "loliPickaxe.failRespondAttack");
        if (Config.getBoolean(stack, "loliPickaxeKillFacing")) {
            tooltip.accept(Component.translatable("loliPickaxe.killFacing",
                    Config.getInt(stack, "loliPickaxeKillFacingRange"),
                    Config.getDouble(stack, "loliPickaxeKillFacingSlope")));
        }
    }

    private static void addIf(ItemStack stack, Consumer<Component> tooltip, String flag, String key) {
        if (Config.getBoolean(stack, flag)) {
            tooltip.accept(Component.translatable(key));
        }
    }

    public int getRange(ItemStack stack) {
        return LoliData.getInt(stack, "range", 1);
    }

    public String getOwner(ItemStack stack) {
        return LoliData.getString(stack, "Owner", "");
    }

    @Override
    public int storagePages(ItemStack stack) {
        return com.anotherstar.common.config.Config.MAX_PAGE.get();
    }
}
