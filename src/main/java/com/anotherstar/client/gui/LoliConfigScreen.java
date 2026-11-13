package com.anotherstar.client.gui;

import com.anotherstar.common.config.Config;
import com.anotherstar.common.item.ILoliStorageHolder;
import com.anotherstar.network.SetFlagPayload;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/** Per-item configuration GUI ("配置氪金萝莉"), ported from {@code GUILoliConfig}. */
public class LoliConfigScreen extends Screen {

    private static final int COLUMNS = 2;
    private static final int ROWS = 8;
    private final List<String> flags = new ArrayList<>();
    private int page;

    public LoliConfigScreen() {
        super(Component.translatable("gui.lolipickaxe.config"));
        for (var entry : Config.FLAGS.entrySet()) {
            if (entry.getValue() instanceof net.neoforged.neoforge.common.ModConfigSpec.BooleanValue) {
                this.flags.add(entry.getKey());
            }
        }
    }

    private ItemStack held() {
        return this.minecraft == null || this.minecraft.player == null
                ? ItemStack.EMPTY
                : this.minecraft.player.getMainHandItem();
    }

    @Override
    protected void init() {
        super.init();
        int perPage = COLUMNS * ROWS;
        int start = page * perPage;
        for (int i = 0; i < perPage && start + i < flags.size(); i++) {
            String flag = flags.get(start + i);
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = this.width / 2 - 150 + col * 155;
            int y = 40 + row * 20;
            this.addRenderableWidget(Button.builder(label(flag), button -> toggle(flag))
                    .bounds(x, y, 145, 18).build());
        }
        if (page > 0) {
            this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
                page--;
                this.rebuildWidgets();
            }).bounds(this.width / 2 - 60, this.height - 32, 24, 18).build());
        }
        if (start + perPage < flags.size()) {
            this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
                page++;
                this.rebuildWidgets();
            }).bounds(this.width / 2 + 36, this.height - 32, 24, 18).build());
        }
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.onClose())
                .bounds(this.width / 2 - 40, this.height - 32, 80, 18).build());
    }

    private Component label(String flag) {
        ItemStack stack = held();
        boolean value = Config.getBoolean(stack, flag);
        String shortName = flag.replace("loliPickaxe", "");
        return Component.literal(shortName + ": " + (value ? "§aON" : "§cOFF"));
    }

    private void toggle(String flag) {
        ItemStack stack = held();
        boolean next = !Config.getBoolean(stack, flag);
        Config.set(stack, flag, next);
        ClientPacketDistributor.sendToServer(new SetFlagPayload(flag, next));
        this.rebuildWidgets();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        if (!(held().getItem() instanceof ILoliStorageHolder)) {
            graphics.drawCenteredString(this.font, Component.translatable("gui.lolipickaxe.config.hold"),
                    this.width / 2, this.height - 50, 0xFF5555);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
