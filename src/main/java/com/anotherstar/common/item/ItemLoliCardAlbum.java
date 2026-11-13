package com.anotherstar.common.item;

import com.anotherstar.util.LoliData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/** "萝莉卡片册" – collects up to 54 card ids in custom data. */
public class ItemLoliCardAlbum extends Item {

    public static final int CAPACITY = 54;

    public ItemLoliCardAlbum(Properties properties) {
        super(properties);
    }

    public List<String> getCards(ItemStack album) {
        List<String> cards = new ArrayList<>();
        var list = LoliData.getList(album, "Cards");
        for (int i = 0; i < list.size(); i++) {
            cards.add(list.getString(i).orElse(""));
        }
        return cards;
    }

    public boolean addCard(ItemStack album, String card) {
        List<String> cards = getCards(album);
        if (cards.size() >= CAPACITY || cards.contains(card)) {
            return false;
        }
        cards.add(card);
        var list = new net.minecraft.nbt.ListTag();
        for (String c : cards) {
            list.add(net.minecraft.nbt.StringTag.valueOf(c));
        }
        LoliData.setList(album, "Cards", list);
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack album = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            List<String> cards = getCards(album);
            player.displayClientMessage(Component.translatable("gui.loliCardAlbum.count", cards.size()), false);
            for (String card : cards) {
                player.displayClientMessage(Component.literal(" - " + card), false);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("gui.loliCardAlbum.count", getCards(stack).size()).withStyle(ChatFormatting.GRAY));
    }
}
