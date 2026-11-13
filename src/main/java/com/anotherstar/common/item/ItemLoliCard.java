package com.anotherstar.common.item;

import com.anotherstar.util.LoliData;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/** "萝莉卡片" – a collectible card carrying an image id and a display name. */
public class ItemLoliCard extends Item {

    public ItemLoliCard(Properties properties) {
        super(properties);
    }

    public String getCardId(ItemStack stack) {
        return LoliData.getString(stack, "Card", "");
    }

    public ItemStack create(String cardId, String displayName) {
        ItemStack stack = new ItemStack(this);
        LoliData.setString(stack, "Card", cardId);
        LoliData.setString(stack, "CardName", displayName);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = LoliData.getString(stack, "CardName", "");
        return name.isEmpty() ? super.getName(stack) : Component.literal(name);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack album = player.getOffhandItem();
        if (!(album.getItem() instanceof ItemLoliCardAlbum cardAlbum)) {
            return InteractionResult.PASS;
        }
        String card = getCardId(player.getItemInHand(hand));
        if (card.isEmpty() || level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (cardAlbum.addCard(album, card)) {
            player.getItemInHand(hand).shrink(1);
            player.displayClientMessage(Component.translatable("gui.loliCardAlbum.add", card), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        String id = getCardId(stack);
        if (!id.isEmpty()) {
            tooltip.accept(Component.literal(id).withStyle(net.minecraft.ChatFormatting.GRAY));
        }
    }
}
