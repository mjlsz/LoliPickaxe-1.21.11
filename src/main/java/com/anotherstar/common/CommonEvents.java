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
            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
        } else if (!player.isCreative() && player.getAbilities().mayfly && !player.isSpectator()) {
            // only remove flight that we granted: tracked by mayFly default
        }

        // small loli pickaxe: fly upgrade
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof ItemSmallLoliPickaxe small && small.canFly(stack)) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof ItemSmallLoliPickaxe)) {
            return;
        }
        LoliUtil.mineArea(player, stack, event.getPos());
    }
}
