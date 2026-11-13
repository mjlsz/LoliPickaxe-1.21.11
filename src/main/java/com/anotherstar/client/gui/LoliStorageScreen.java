package com.anotherstar.client.gui;

import com.anotherstar.common.gui.LoliStorageMenu;
import com.anotherstar.network.ChangePagePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/** Screen of the Small Loli Pickaxe backpack (6 rows + page buttons). */
public class LoliStorageScreen extends AbstractContainerScreen<LoliStorageMenu> {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("lolipickaxe", "textures/gui/container/loli_pickaxe_container.png");

    public LoliStorageScreen(LoliStorageMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 222;
        this.inventoryLabelY = 1000;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> ClientPacketDistributor.sendToServer(new ChangePagePayload(-1)))
                .bounds(this.leftPos + 8, this.topPos + 126, 20, 12).build());
        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> ClientPacketDistributor.sendToServer(new ChangePagePayload(1)))
                .bounds(this.leftPos + 148, this.topPos + 126, 20, 12).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        String page = (this.menu.page() + 1) + " / " + this.menu.pages();
        graphics.drawString(this.font, page, this.leftPos + 70, this.topPos + 128, 0x404040, false);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE,
                this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}
