package com.anotherstar.registry;

import com.anotherstar.LoliPickaxe;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, LoliPickaxe.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> LOLISUCCESS = SOUNDS.register("lolisuccess",
            () -> SoundEvent.createVariableRangeEvent(LoliPickaxe.id("lolisuccess")));
    public static final DeferredHolder<SoundEvent, SoundEvent> LOLIRECORD = SOUNDS.register("lolirecord",
            () -> SoundEvent.createVariableRangeEvent(LoliPickaxe.id("lolirecord")));

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }

    private ModSounds() {
    }
}
