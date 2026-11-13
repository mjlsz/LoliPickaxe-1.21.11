# 1.12.2 → 1.21.11 逐项对照与移植清单

原模组共 **105 个类 / 约 19,300 行**（Vineflower 反编译，`../decompiled`）。
下表按包列出处理状态：✅ 已移植　⚠️ 简化移植　❌ 未移植。

## common

| 1.12.2 类 | 1.21.11 对应 | 状态 | 备注 |
|---|---|---|---|
| `LoliPickaxe` | `com.anotherstar.LoliPickaxe` | ✅ | 入口；`@Mod` 构造器注册一切 |
| `CommonProxy` / `ClientProxy` | 无 | ✅ | 1.21 不再需要 proxy，客户端逻辑用 `FMLEnvironment.dist` 守卫 |
| `ConfigLoader`（1133 行） | `config/Config` + `LoliConfigScreen` + `/loli` | ✅ | 55 个 flag 保留 43 个；`ModConfigSpec` + 逐物品 NBT 覆盖 + GUI + `/loli` 已接 |
| `ConfigField` 注解 | 无 | ⚠️ | 改为显式 `ModConfigSpec` 定义 |
| `LoliPickaxeUtil` | `util/LoliUtil` | ⚠️ | 秒杀、范围秒杀、扇形秒杀、范围采掘已移植；"伊邪那美/灵魂超度"等特殊惩罚未接 |
| `LoliPickaxeEvent` | `common/CommonEvents` | ⚠️ | `LivingUpdateEvent` → `PlayerTickEvent.Post`；`EntityJoinWorldEvent` 去重/寻主未接 |
| `DestroyBedrockEvent`（基岩破坏） | — | ❌ | 依赖 coremod 的 `PlayerControllerMP` 改写 |
| `SmallLoliBlockDropEvent` / `SmallLoliFlyEvent` | `LoliDropHandler` | ✅ | 时运、自动熔炼、稀有掉落已实现 |
| `PlayerJoinEvent` | — | ❌ | 原版用于同步配置，配置同步包未做 |
| `LoliDropEvent` / `LoliTickEvent` | — | ❌ | 掉落概率、延迟清理 |
| `ItemLoader` | `registry/ModItems` | ✅ | 21 个物品 + 唱片（唱片改为单个 `loli_record`） |
| `BlockLoader` | `registry/ModBlocks` | ✅ | 3 个 TNT |
| `EntityLoader` | `registry/ModEntities` | ✅ | 2 个实体 |
| `RecipeLoader` + `SuperpositionRecipe` | `common/AddonCraftingHandler` | ✅ | 14 个 shapeless 配方 + 合成事件校验 9 个同级升级 |
| `EnchantmentLoader` / `EnchantmentAutoFurnace` | — | ❌ | 1.21 附魔是数据驱动（datapack），自动熔炼改用事件实现更简单 |
| `LoliGUIHandler` | `registry/ModMenus` | ✅ | `IMenuTypeExtension` + `MenuType`，储藏室可开 |
| `ContainerLoliPickaxe`（21KB）/ `ContainerBlaceListLoliPickaxe` | `common/gui/LoliStorageMenu` | ⚠️ | 储藏室（6×9 分页）已移植；黑名单容器未做 |
| `InventoryLoliBase` / `InventoryLoliPickaxe` / `InventorySmallLoliPickaxe` | `common/gui/LoliStorage` | ✅ | NBT `Inventory` 持久化，`ItemStack.CODEC` 序列化 |
| `ConfigCommand` / `LoliBuffAttackCommand` | `command/LoliCommand` | ⚠️ | `/loli` 已注册；`LoliBuffAttackCommand` 未移植 |
| `BlockBuffAttackTNT` | `BlockLoliTNT` | ⚠️ | 假蓝屏/假崩溃改为真实爆炸 + 秒杀；TNT 实体改为自定义 `EntityLoliBuffAttackTNT` |
| `EntityLoli` | `entity/EntityLoli` | ⚠️ | 一击必杀 AI、免伤、重生已移植；游泳/跨维度细节简化 |
| `EntityAILoli*`（5 个） | `EntityLoli.LoliAttackGoal` | ⚠️ | 目标选择简化为"最近的玩家" |

## client

| 1.12.2 类 | 1.21.11 对应 | 状态 | 备注 |
|---|---|---|---|
| `ModelLoli` | `client/model/LoliModel` | ✅ | 逐块搬运（128×128） |
| `RenderLoli` | `client/render/LoliRenderer` | ✅ | `MobRenderer` |
| `ModelPaperLoli` / `RenderRemiliaLoli` / `RenderLoliItem` / `LayerMaidHeldItem` | — | ❌ | 纸片萝莉/蕾米莉亚皮肤 |
| `RenderLoliCardFrame`（16KB, TESR+OBJ） | — | ❌ | 1.21 无内置 OBJ 加载器 |
| `WavefrontObject` 等 OBJ 解析（8 个类） | — | ❌ | 需自写 loader 或改盒子模型 |
| `GUIContainerLoliPickaxe` | `client/gui/LoliStorageScreen` | ✅ | 6 行容器 + 翻页按钮 |
| `GUILoliConfig` / `GUILoliEnchantment` / `GUILoliPotion` / `GUILoliCard*` | — | ❌ | 其余 Screen |
| `KeyLoader` | — | ❌ | 4 个 GUI 快捷键 |
| `LoliCardUtil` / `LoliCardOnlineUtil` / 事件 | — | ❌ | 卡片渲染/在线卡片（HTTP 下载） |

## network / core

| 1.12.2 类 | 1.21.11 对应 | 状态 | 备注 |
|---|---|---|---|
| `NetworkHandler` + 17 个 Packet | `network/ModNetwork` + `KillFacingPayload` | ⚠️ | 只移植了 `kill_facing`；其余（配置同步、槽位、开容器、死亡特效）待补 |
| `LoliPickaxeCore` / `LoliPickaxeTransformer`（33KB ASM） | — | ❌ | 1.21 应改为 Mixin；原功能包括改 `EntityPlayerMP`、`NetHandlerPlayServer`、`Minecraft` 崩溃处理等 |
| `LoliPickaxeContainer`（core） | — | ❌ | coremod 侧容器 |

## 已完成的关键 API 迁移

```java
// 1.12.2
@Mod(modid = "lolipickaxe", name = "LoliPickaxe Mod", version = "1.2.12")
public class LoliPickaxe { @EventHandler public void preInit(FMLPreInitializationEvent e) {...} }

// 1.21.11
@Mod(LoliPickaxe.MODID)
public class LoliPickaxe {
    public LoliPickaxe(IEventBus modBus, ModContainer container) { ... }
}
```

```java
// 1.12.2: metadata
stack.getItemDamage(); stack.setItemDamage(i);

// 1.21.11: custom data component
LoliData.getInt(stack, "level", 0);
LoliData.setInt(stack, "level", i);
```

```java
// 1.12.2: 注册事件
@SubscribeEvent public void registerItem(RegistryEvent.Register<Item> e) { e.getRegistry().register(...); }

// 1.21.11: DeferredRegister
public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
public static final DeferredItem<ItemAddon> COAL_ADDON =
        ITEMS.registerItem("loli_coal_addon", p -> new ItemAddon(p, "loli_coal_addon", 10, false), new Item.Properties());
```

```java
// 1.12.2: 网络
NetworkHandler.INSTANCE.sendMessageToServer(new LoliKillFacingPacket());

// 1.21.11: CustomPacketPayload
ClientPacketDistributor.sendToServer(KillFacingPayload.INSTANCE);
```

## 后续优先级建议

1. **储藏室 GUI**：`MenuType` + `AbstractContainerMenu` + `Screen` + `ContainerData`（翻页）
2. **升级配方**：`RecipeSerializer` + `Recipe<CraftingInput>`，9 个同级升级 → 下一级
3. **配置同步与 `/loli` 命令**：`ConfigurationTask` 或登录时发 payload
4. **卡片渲染**：把 OBJ 转成 `ModelPart` 或自写 OBJ 加载器
5. **Mixin 替代 coremod**：仅移植"防崩溃/伤害来源"必要部分



## 编译期实测出来的 1.21.11 API 变化（本轮）

| 旧写法（1.21.1 记忆 / 文档） | 1.21.11 实际 | 处理 |
|---|---|---|
| `MobRenderer<T, M>` | `MobRenderer<T, S extends LivingEntityRenderState, M extends EntityModel<? super S>>` | 新增 `LoliRenderState`，实现 `createRenderState()` |
| `EntityModel<EntityLoli>` + `setupAnim(entity, ...)` | `EntityModel<S extends EntityRenderState>` + `setupAnim(S)` | 动画参数改从 render state 取 |
| `renderToBuffer(...)` 可覆写 | `Model#renderToBuffer` 是 `final` | 删掉覆写，交给 root 渲染 |
| `Entity#addAdditionalSaveData(CompoundTag)` | `ValueOutput` / `ValueInput` | TNT 实体改用 `putInt/getIntOr` |
| `Entity#hurt` 抽象 | 新增抽象 `hurtServer(ServerLevel, DamageSource, float)` | TNT 实体实现返回 false |
| `Entity#moveTo` / `canChangeDimensions()` | `snapTo(...)`；维度方法已删 | 萝莉重生改用 `snapTo` |
| `CreativeModeTab.builder()` | `builder(CreativeModeTab.Row, int)`（标签页改成网格） | 两个页签分别 `(TOP,0)` / `(TOP,1)` |
| `ServerPlayer#openMenu(provider, bufWriter)` | 只剩 `openMenu(MenuProvider)` | 走 `IPlayerExtension#openMenu(provider, Consumer<RegistryFriendlyByteBuf>)` |
| `Item#inventoryTick(stack, Level, entity, int, boolean)` | `inventoryTick(stack, ServerLevel, entity, EquipmentSlot)` | 改签名（且只在服务端调用） |
| `Item#getDescriptionId(ItemStack)` | 只有无参 `getDescriptionId()` | 改名调用 |
| `Level#playSound(..., Holder<SoundEvent>, ...)` | 需要 `SoundEvent` 本体 | `SoundEvents.GENERIC_EXPLODE.value()` |
| `CustomData#contains` | 没有，只有 `copyTag()` | 先 `copyTag()` 再查 |
| `GuiGraphics#blit(Identifier, x,y,u,v,w,h)` | 需要 `RenderPipeline` | `blit(RenderPipelines.GUI_TEXTURED, tex, x, y, u, v, w, h, tw, th)` |
| `MenuScreens.ScreenConstructor` 可直接用 | 包级私有（NeoForge 用 AT 打开） | 在 `net.minecraft.client.gui.screens` 包内加 `LoliMenuScreens` 转发 |

### 编译管线（无 NeoForm 时的替代）

```
client.jar + client.txt
   → mapping-io (ProGuard → Tiny v2)
   → tiny-remapper (obf → official)
   → mc/named/*.class
   → javac -cp "mc/named;neoforge-universal.jar;bus.jar;stubs;mc/libs/*"
   → build2/*.class
   → build_jar.py → build/libs/lolipickaxe-2.0.0.jar
```

结果：**0 error，40 个 class，195 个 jar 条目**。

