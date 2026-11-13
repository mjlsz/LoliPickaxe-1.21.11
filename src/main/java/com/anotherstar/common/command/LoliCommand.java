package com.anotherstar.common.command;

import com.anotherstar.common.config.Config;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Port of {@code ConfigCommand}: {@code /loli <flag> [value]}. */
public final class LoliCommand {

    private LoliCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("loli")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("flag", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            Config.FLAGS.keySet().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String flag = StringArgumentType.getString(context, "flag");
                            String value = Config.flagValue(flag);
                            if (value == null) {
                                context.getSource().sendFailure(Component.literal("未知的配置: " + flag));
                                return 0;
                            }
                            context.getSource().sendSuccess(() -> Component.literal(flag + " = " + value), false);
                            return 1;
                        })
                        .then(Commands.argument("value", StringArgumentType.greedyString())
                                .executes(context -> {
                                    String flag = StringArgumentType.getString(context, "flag");
                                    String value = StringArgumentType.getString(context, "value");
                                    String error = Config.setFlag(flag, value);
                                    if (error != null) {
                                        context.getSource().sendFailure(Component.literal(error));
                                        return 0;
                                    }
                                    context.getSource().sendSuccess(() -> Component.literal(flag + " = " + value), true);
                                    return 1;
                                }))));
    }
}
