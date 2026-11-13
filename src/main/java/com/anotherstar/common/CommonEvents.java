package com.anotherstar.common;

import com.anotherstar.common.config.Config;
import com.anotherstar.common.item.ItemLoliPickaxe;
import com.anotherstar.common.item.ItemSmallLoliPickaxe;
import com.anotherstar.util.LoliUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Replacement for the old {@code LoliPickaxeEvent} / {@code SmallLoliFlyEvent} classes. */
public final class CommonEvents {

    /** Players we granted flight to, so it can be revoked when they stop holding the item. */
    private static final java.util.Set<java.util.UUID> GRANTED_FLIGHT = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private CommonEvents() {
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ItemLoliPickaxe && !player.level().isClientSide()) {
            if (LoliUtil.attackWithLoliPickaxe(player, event.getTarget(), stack)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        ItemStack pickaxe = LoliUtil.getLoliPickaxe(entity);
        if (pickaxe.isEmpty()) {
            return;
        }
        if (Config.getBoolean(pickaxe, "loliPickaxeThorns")) {
            var sourceEntity = event.getSource().getEntity();
            if (sourceEntity instanceof LivingEntity attacker && attacker != entity) {
                LoliUtil.killEntity(entity.level(), attacker, entity);
            }
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        ItemStack pickaxe = LoliUtil.getLoliPickaxe(player);
        boolean hasLoli = !pickaxe.isEmpty();

        if (hasLoli) {
            if (Config.getBoolean(pickaxe, "loliPickaxeAutoKillRangeEntity")) {
                int range = Config.getInt(pickaxe, "loliPickaxeAutoKillRange");
                LoliUtil.killRangeEntity(player.level(), player, range);
            }
            if (Config.getBoolean(pickaxe, "loliPickaxeKillFacing")) {
                // left click empty is client side only; the server sweep is done on attack
            }
            if (player.getFoodData().getFoodLevel() < 20) {
                player.getFoodData().setFoodLevel(20);
            }
            if (player.getHealth() < player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
            var potions = com.anotherstar.util.LoliData.getList(pickaxe, "LoliPotion");
            for (int i = 0; i < potions.size(); i++) {
                var entry = potions.getCompoundOrEmpty(i);
                String id = entry.getStringOr("id", "");
                if (id.isEmpty()) {
                    continue;
                }
                var key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.MOB_EFFECT,
                        net.minecraft.resources.Identifier.parse(id));
                net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.get(key).ifPresent(holder ->
                        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(holder, 410, entry.getIntOr("lvl", 0), false, false)));
            }
            // flight handled below
        }

        // Flight: allowed while holding the Loli Pickaxe or a Small Loli Pickaxe with the fly upgrade.
        // Never force `flying` - that made the player unable to walk or land.
        boolean flightAllowed = hasLoli;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof ItemSmallLoliPickaxe small && small.canFly(stack)) {
                flightAllowed = true;
                break;
            }
        }
        if (flightAllowed) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
                GRANTED_FLIGHT.add(player.getUUID());
            }
        } else if (GRANTED_FLIGHT.remove(player.getUUID()) && !player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    /**
     * The Loli Pickaxe also breaks blocks that are normally unbreakable (bedrock, barrier...),
     * mirroring the 1.12.2 DestroyBedrockEvent.
     */
    @SubscribeEvent
    public static void onLeftClickBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() != net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action.START) {
            return;
        }
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ItemLoliPickaxe)
                || !Config.getBoolean(stack, "loliPickaxeBreakUnbreakable")) {
            return;
        }
        var level = player.level();
        var pos = event.getPos();
        var state = level.getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(level, pos) >= 0.0F) {
            return;
        }
        boolean bedrock = state.is(net.minecraft.world.level.block.Blocks.BEDROCK);
        level.destroyBlock(pos, !player.isCreative() && !bedrock, player);
        if (bedrock && !player.isCreative()) {
            net.minecraft.world.level.block.Block.popResource(level, pos, new ItemStack(net.minecraft.world.item.Items.BEDROCK));
        }
        event.setCanceled(true);
    }

    /** Fallback: any block the tool rules miss still breaks instantly. */
    @SubscribeEvent
    public static void onBreakSpeed(net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed event) {
        if (event.getEntity().getMainHandItem().getItem() instanceof ItemLoliPickaxe) {
            event.setNewSpeed(Math.max(event.getNewSpeed(), 200.0F));
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ItemLoliPickaxe loliPickaxe) {
            int radius = loliPickaxe.getRange(stack);
            if (radius > 0) {
                LoliUtil.mineCube(player.level(), player, event.getPos(), radius, stack);
            }
        } else if (stack.getItem() instanceof ItemSmallLoliPickaxe) {
            LoliUtil.mineArea(player, stack, event.getPos());
        }
    }
}
