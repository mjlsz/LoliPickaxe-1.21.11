package com.anotherstar.common.config;

import com.anotherstar.util.LoliData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Global configuration.
 *
 * <p>The 1.12.2 version used an annotation driven ini/json configuration with a
 * per-item override stored in the item's NBT (so a single pickaxe could be
 * configured independently). That concept is preserved: every getter first looks
 * at the item's NBT, then falls back to the global value.</p>
 */
public final class Config {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_RANGE = BUILDER.comment("最大采掘范围").defineInRange("loliPickaxeMaxRange", 5, 0, 64);
    public static final ModConfigSpec.BooleanValue MANDATORY_DROP = BUILDER.define("loliPickaxeMandatoryDrop", true);
    public static final ModConfigSpec.BooleanValue STOP_ON_LIQUID = BUILDER.define("loliPickaxeStopOnLiquid", false);
    public static final ModConfigSpec.IntValue MAX_PAGE = BUILDER.defineInRange("loliPickaxeMaxPage", 20, 1, 200);
    public static final ModConfigSpec.BooleanValue CANCEL_STACK_LIMIT = BUILDER.define("loliPickaxeCancelStackLimit", true);
    public static final ModConfigSpec.IntValue SLOT_STACK_LIMIT = BUILDER.defineInRange("loliPickaxeSlotStackLimit", 2000000000, 1, Integer.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue AUTO_ACCEPT = BUILDER.define("loliPickaxeAutoAccept", true);
    public static final ModConfigSpec.BooleanValue THORNS = BUILDER.define("loliPickaxeThorns", true);
    public static final ModConfigSpec.BooleanValue KILL_RANGE_ENTITY = BUILDER.define("loliPickaxeKillRangeEntity", true);
    public static final ModConfigSpec.IntValue KILL_RANGE = BUILDER.defineInRange("loliPickaxeKillRange", 50, 0, 100);
    public static final ModConfigSpec.IntValue MAX_KILL_RANGE = BUILDER.defineInRange("loliPickaxeMaxKillRange", 100, 1, 1000);
    public static final ModConfigSpec.BooleanValue AUTO_KILL_RANGE_ENTITY = BUILDER.define("loliPickaxeAutoKillRangeEntity", false);
    public static final ModConfigSpec.IntValue AUTO_KILL_RANGE = BUILDER.defineInRange("loliPickaxeAutoKillRange", 5, 0, 100);
    public static final ModConfigSpec.IntValue MAX_AUTO_KILL_RANGE = BUILDER.defineInRange("loliPickaxeMaxAutoKillRange", 10, 1, 1000);
    public static final ModConfigSpec.IntValue DURATION = BUILDER.defineInRange("loliPickaxeDuration", 200, 1, 100000);
    public static final ModConfigSpec.IntValue DROP_PROTECT_TIME = BUILDER.defineInRange("loliPickaxeDropProtectTime", 200, 0, 100000);
    public static final ModConfigSpec.BooleanValue COMPULSORY_REMOVE = BUILDER.define("loliPickaxeCompulsoryRemove", true);
    public static final ModConfigSpec.BooleanValue VALID_TO_AMITY_ENTITY = BUILDER.define("loliPickaxeValidToAmityEntity", true);
    public static final ModConfigSpec.BooleanValue VALID_TO_ALL_ENTITY = BUILDER.define("loliPickaxeValidToAllEntity", true);
    public static final ModConfigSpec.BooleanValue CLEAR_INVENTORY = BUILDER.define("loliPickaxeClearInventory", true);
    public static final ModConfigSpec.BooleanValue DROP_ITEMS = BUILDER.define("loliPickaxeDropItems", true);
    public static final ModConfigSpec.BooleanValue KICK_PLAYER = BUILDER.define("loliPickaxeKickPlayer", true);
    public static final ModConfigSpec.ConfigValue<String> KICK_MESSAGE = BUILDER.define("loliPickaxeKickMessage", "你被氪金萝莉踢出了游戏");
    public static final ModConfigSpec.BooleanValue REINCARNATION = BUILDER.define("loliPickaxeReincarnation", true);
    public static final ModConfigSpec.BooleanValue BEYOND_REDEMPTION = BUILDER.define("loliPickaxeBeyondRedemption", true);
    public static final ModConfigSpec.BooleanValue FIND_OWNER = BUILDER.define("loliPickaxeFindOwner", true);
    public static final ModConfigSpec.IntValue FIND_OWNER_RANGE = BUILDER.defineInRange("loliPickaxeFindOwnerRange", 20, 1, 256);
    public static final ModConfigSpec.BooleanValue BLUE_SCREEN_ATTACK = BUILDER.define("loliPickaxeBlueScreenAttack", true);
    public static final ModConfigSpec.BooleanValue EXIT_ATTACK = BUILDER.define("loliPickaxeExitAttack", true);
    public static final ModConfigSpec.BooleanValue FAIL_RESPOND_ATTACK = BUILDER.define("loliPickaxeFailRespondAttack", true);
    public static final ModConfigSpec.BooleanValue KILL_FACING = BUILDER.define("loliPickaxeKillFacing", true);
    public static final ModConfigSpec.IntValue KILL_FACING_RANGE = BUILDER.defineInRange("loliPickaxeKillFacingRange", 20, 0, 100);
    public static final ModConfigSpec.DoubleValue KILL_FACING_SLOPE = BUILDER.defineInRange("loliPickaxeKillFacingSlope", 0.5D, 0.0D, 10.0D);
    public static final ModConfigSpec.DoubleValue LOLI_SPEED = BUILDER.defineInRange("loliSpeed", 0.3D, 0.0D, 10.0D);
    public static final ModConfigSpec.BooleanValue LOLI_ATTACK = BUILDER.define("loliAttack", true);
    public static final ModConfigSpec.BooleanValue LOLI_TELEPORT = BUILDER.define("loliTeleport", true);
    public static final ModConfigSpec.DoubleValue CARD_DROP_PROBABILITY = BUILDER.defineInRange("loliCardDropProbability", 0.01D, 0.0D, 1.0D);
    public static final ModConfigSpec.DoubleValue ALBUM_DROP_PROBABILITY = BUILDER.defineInRange("loliCardAlbumDropProbability", 0.001D, 0.0D, 1.0D);
    public static final ModConfigSpec.DoubleValue RECORD_DROP_PROBABILITY = BUILDER.defineInRange("loliRecordDropProbability", 0.01D, 0.0D, 1.0D);
    public static final ModConfigSpec.DoubleValue ENTITY_SOUL_DROP_PROBABILITY = BUILDER.defineInRange("entitySoulDropProbability", 0.1D, 0.0D, 1.0D);
    public static final ModConfigSpec.BooleanValue ENABLE_BUFF_ATTACK_TNT = BUILDER.define("loliEnableBuffAttackTNT", true);
    public static final ModConfigSpec.DoubleValue CARD_SCALE = BUILDER.defineInRange("loliCardScale", 1.0D, 0.1D, 10.0D);
    public static final ModConfigSpec.BooleanValue CARD_RENDER_FRAME = BUILDER.define("loliCardRenderFrame", true);
    public static final ModConfigSpec.ConfigValue<String> LOLI_MODEL_ID = BUILDER.define("loliModelId", "lolipickaxe:loli");

    static {
        SPEC = BUILDER.build();
    }

    private Config() {
    }

    /* ------------------------------------------------------------------ */
    /* Per-item overrides (old ConfigLoader.getXxx(stack, "flag") helpers) */
    /* ------------------------------------------------------------------ */

    public static boolean getBoolean(ItemStack stack, String flag) {
        CompoundTag tag = LoliData.get(stack);
        if (tag.contains(flag)) {
            return tag.getBooleanOr(flag, false);
        }
        return switch (flag) {
            case "loliPickaxeMandatoryDrop" -> MANDATORY_DROP.get();
            case "loliPickaxeStopOnLiquid" -> STOP_ON_LIQUID.get();
            case "loliPickaxeCancelStackLimit" -> CANCEL_STACK_LIMIT.get();
            case "loliPickaxeAutoAccept" -> AUTO_ACCEPT.get();
            case "loliPickaxeThorns" -> THORNS.get();
            case "loliPickaxeKillRangeEntity" -> KILL_RANGE_ENTITY.get();
            case "loliPickaxeAutoKillRangeEntity" -> AUTO_KILL_RANGE_ENTITY.get();
            case "loliPickaxeCompulsoryRemove" -> COMPULSORY_REMOVE.get();
            case "loliPickaxeValidToAmityEntity" -> VALID_TO_AMITY_ENTITY.get();
            case "loliPickaxeValidToAllEntity" -> VALID_TO_ALL_ENTITY.get();
            case "loliPickaxeClearInventory" -> CLEAR_INVENTORY.get();
            case "loliPickaxeDropItems" -> DROP_ITEMS.get();
            case "loliPickaxeKickPlayer" -> KICK_PLAYER.get();
            case "loliPickaxeReincarnation" -> REINCARNATION.get();
            case "loliPickaxeBeyondRedemption" -> BEYOND_REDEMPTION.get();
            case "loliPickaxeBlueScreenAttack" -> BLUE_SCREEN_ATTACK.get();
            case "loliPickaxeExitAttack" -> EXIT_ATTACK.get();
            case "loliPickaxeFailRespondAttack" -> FAIL_RESPOND_ATTACK.get();
            case "loliPickaxeKillFacing" -> KILL_FACING.get();
            default -> false;
        };
    }

    public static int getInt(ItemStack stack, String flag) {
        CompoundTag tag = LoliData.get(stack);
        if (tag.contains(flag)) {
            return tag.getIntOr(flag, 0);
        }
        return switch (flag) {
            case "loliPickaxeKillRange" -> Math.min(KILL_RANGE.get(), MAX_KILL_RANGE.get());
            case "loliPickaxeAutoKillRange" -> Math.min(AUTO_KILL_RANGE.get(), MAX_AUTO_KILL_RANGE.get());
            case "loliPickaxeKillFacingRange" -> KILL_FACING_RANGE.get();
            default -> 0;
        };
    }

    public static double getDouble(ItemStack stack, String flag) {
        CompoundTag tag = LoliData.get(stack);
        if (tag.contains(flag)) {
            return tag.getDoubleOr(flag, 0.0D);
        }
        return switch (flag) {
            case "loliPickaxeKillFacingSlope" -> KILL_FACING_SLOPE.get();
            default -> 0.0D;
        };
    }

    public static String getString(ItemStack stack, String flag) {
        CompoundTag tag = LoliData.get(stack);
        if (tag.contains(flag)) {
            return tag.getStringOr(flag, "");
        }
        return switch (flag) {
            case "loliPickaxeKickMessage" -> KICK_MESSAGE.get();
            default -> "";
        };
    }

    public static void set(ItemStack stack, String flag, boolean value) {
        LoliData.setBoolean(stack, flag, value);
    }

    public static void set(ItemStack stack, String flag, int value) {
        LoliData.setInt(stack, flag, value);
    }

    public static void set(ItemStack stack, String flag, double value) {
        LoliData.setDouble(stack, flag, value);
    }

    /* ------------------------------------------------------------------ */
    /* Flag table used by the /loli command and the (future) config GUI    */
    /* ------------------------------------------------------------------ */

    public static final java.util.Map<String, ModConfigSpec.ConfigValue<?>> FLAGS = new java.util.LinkedHashMap<>();

    static {
        FLAGS.put("loliPickaxeMaxRange", MAX_RANGE);
        FLAGS.put("loliPickaxeMandatoryDrop", MANDATORY_DROP);
        FLAGS.put("loliPickaxeStopOnLiquid", STOP_ON_LIQUID);
        FLAGS.put("loliPickaxeMaxPage", MAX_PAGE);
        FLAGS.put("loliPickaxeCancelStackLimit", CANCEL_STACK_LIMIT);
        FLAGS.put("loliPickaxeSlotStackLimit", SLOT_STACK_LIMIT);
        FLAGS.put("loliPickaxeAutoAccept", AUTO_ACCEPT);
        FLAGS.put("loliPickaxeThorns", THORNS);
        FLAGS.put("loliPickaxeKillRangeEntity", KILL_RANGE_ENTITY);
        FLAGS.put("loliPickaxeKillRange", KILL_RANGE);
        FLAGS.put("loliPickaxeMaxKillRange", MAX_KILL_RANGE);
        FLAGS.put("loliPickaxeAutoKillRangeEntity", AUTO_KILL_RANGE_ENTITY);
        FLAGS.put("loliPickaxeAutoKillRange", AUTO_KILL_RANGE);
        FLAGS.put("loliPickaxeMaxAutoKillRange", MAX_AUTO_KILL_RANGE);
        FLAGS.put("loliPickaxeDuration", DURATION);
        FLAGS.put("loliPickaxeDropProtectTime", DROP_PROTECT_TIME);
        FLAGS.put("loliPickaxeCompulsoryRemove", COMPULSORY_REMOVE);
        FLAGS.put("loliPickaxeValidToAmityEntity", VALID_TO_AMITY_ENTITY);
        FLAGS.put("loliPickaxeValidToAllEntity", VALID_TO_ALL_ENTITY);
        FLAGS.put("loliPickaxeClearInventory", CLEAR_INVENTORY);
        FLAGS.put("loliPickaxeDropItems", DROP_ITEMS);
        FLAGS.put("loliPickaxeKickPlayer", KICK_PLAYER);
        FLAGS.put("loliPickaxeKickMessage", KICK_MESSAGE);
        FLAGS.put("loliPickaxeReincarnation", REINCARNATION);
        FLAGS.put("loliPickaxeBeyondRedemption", BEYOND_REDEMPTION);
        FLAGS.put("loliPickaxeFindOwner", FIND_OWNER);
        FLAGS.put("loliPickaxeFindOwnerRange", FIND_OWNER_RANGE);
        FLAGS.put("loliPickaxeBlueScreenAttack", BLUE_SCREEN_ATTACK);
        FLAGS.put("loliPickaxeExitAttack", EXIT_ATTACK);
        FLAGS.put("loliPickaxeFailRespondAttack", FAIL_RESPOND_ATTACK);
        FLAGS.put("loliPickaxeKillFacing", KILL_FACING);
        FLAGS.put("loliPickaxeKillFacingRange", KILL_FACING_RANGE);
        FLAGS.put("loliPickaxeKillFacingSlope", KILL_FACING_SLOPE);
        FLAGS.put("loliSpeed", LOLI_SPEED);
        FLAGS.put("loliAttack", LOLI_ATTACK);
        FLAGS.put("loliTeleport", LOLI_TELEPORT);
        FLAGS.put("loliCardDropProbability", CARD_DROP_PROBABILITY);
        FLAGS.put("loliCardAlbumDropProbability", ALBUM_DROP_PROBABILITY);
        FLAGS.put("loliRecordDropProbability", RECORD_DROP_PROBABILITY);
        FLAGS.put("entitySoulDropProbability", ENTITY_SOUL_DROP_PROBABILITY);
        FLAGS.put("loliEnableBuffAttackTNT", ENABLE_BUFF_ATTACK_TNT);
        FLAGS.put("loliCardScale", CARD_SCALE);
        FLAGS.put("loliCardRenderFrame", CARD_RENDER_FRAME);
        FLAGS.put("loliModelId", LOLI_MODEL_ID);
    }

    public static String flagValue(String flag) {
        ModConfigSpec.ConfigValue<?> value = FLAGS.get(flag);
        if (value == null) {
            return null;
        }
        Object raw = value.get();
        return String.valueOf(raw);
    }

    /** @return null on success, otherwise an error message. */
    public static String setFlag(String flag, String raw) {
        ModConfigSpec.ConfigValue<?> value = FLAGS.get(flag);
        if (value == null) {
            return "unknown flag: " + flag;
        }
        try {
            if (value instanceof ModConfigSpec.BooleanValue bool) {
                bool.set(Boolean.parseBoolean(raw));
            } else if (value instanceof ModConfigSpec.IntValue integer) {
                integer.set(Integer.parseInt(raw));
            } else if (value instanceof ModConfigSpec.DoubleValue decimal) {
                decimal.set(Double.parseDouble(raw));
            } else if (value instanceof ModConfigSpec.ConfigValue<?> config) {
                setStringValue(config, raw);
            }
            return null;
        } catch (Exception e) {
            return "bad value: " + raw;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setStringValue(ModConfigSpec.ConfigValue<?> value, String raw) {
        ((ModConfigSpec.ConfigValue) value).set(raw);
    }
}
