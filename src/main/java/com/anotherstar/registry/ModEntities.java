package com.anotherstar.registry;

import com.anotherstar.LoliPickaxe;
import com.anotherstar.common.entity.EntityLoli;
import com.anotherstar.common.entity.EntityLoliBuffAttackTNT;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, LoliPickaxe.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<EntityLoli>> LOLI = ENTITIES.register("loli",
            () -> EntityType.Builder.of(EntityLoli::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, LoliPickaxe.id("loli"))));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityLoliBuffAttackTNT>> BUFF_ATTACK_TNT =
            ENTITIES.register("loli_buff_attack_tnt",
                    () -> EntityType.Builder.<EntityLoliBuffAttackTNT>of(EntityLoliBuffAttackTNT::new, MobCategory.MISC)
                            .sized(0.98F, 0.98F)
                            .clientTrackingRange(10)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, LoliPickaxe.id("loli_buff_attack_tnt"))));

    public static void register(IEventBus modBus) {
        ENTITIES.register(modBus);
    }

    private ModEntities() {
    }
}
