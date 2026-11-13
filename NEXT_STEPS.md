# 续作指南（给下一个回合 / 换机器时看）

本文件记录**当前状态**与**下一步动作**，避免上下文丢失后重复摸索。

## 现状（已验证）

```powershell
python F:\ds\lolipickaxe-port\LoliPickaxe\build_all.py      # 编译 + 打包
python F:\ds\lolipickaxe-port\LoliPickaxe\validate_assets.py # 资源自检
```

- 44 个源文件 → 48 个 class → `build/libs/lolipickaxe-2.0.0.jar`
- 资源校验 200 条目 0 problem，`jdeps` 无缺失依赖
- Gradle/NeoForm 路线仍被 `maven.neoforged.net` 中断（TCP 超时，已持续多日）

## 编译环境（已搭好，别删）

| 路径 | 用途 |
|---|---|
| `F:\ds\lolipickaxe-port\mc\named` | 官方命名 MC 1.21.11 类（tiny-remapper 生成） |
| `F:\ds\lolipickaxe-port\mc\libs` | Mojang 107 个库 |
| `F:\ds\lolipickaxe-port\tools\neoforge-universal.jar` | NeoForge 21.11.48（mojmap） |
| `F:\ds\lolipickaxe-port\tools\bus.jar` | 事件总线 |
| `F:\ds\lolipickaxe-port\stubs` | 仅编译用的 FML 桩（运行时由 FancyModLoader 提供） |
| `F:\ds\lolipickaxe-port\tools\*.java` | 映射转换 / 重映射 / 改访问标志 / 构建脚本 |

> 注意：本地 `mc/named` 里 `MenuScreens$ScreenConstructor` 被手工改成 public（模拟 NeoForge AT）。
> 重新生成 named jar 后需重跑 `PatchAccess`。

## 下一步（按优先级）

1. **真机验证**（前提：`maven.neoforged.net` 恢复）
   - `gradle build` → `gradle runClient`
   - 进游戏检查：物品栏名称/模型、储藏室 GUI 翻页、配置 GUI、9 合 1 合成、附魔书、药水
   - 用真实报错补 API 差异（javac 子集验证不完全等于运行时）
2. **卡片渲染**
   - 原版 `RenderLoliCardFrame` 用 OBJ + TESR；1.21 已无内置 OBJ loader
   - 方案 A：把 `models/entity/loli/loli.obj` 烘成 `ModelPart`
   - 方案 B：自写 `SimpleJsonModel`/OBJ 加载器 + `BlockEntityRenderer`
3. **coremod → Mixin**
   - 原 `LoliPickaxeTransformer` 33KB、约 30 处改写（`EntityPlayerMP`、`NetHandlerPlayServer`、`Minecraft` 崩溃处理等）
   - 只建议移植"防崩溃/伤害来源"必要项，其余用 NeoForge 事件替代
4. 可选：`LoliBuffAttackCommand`、唱片机播放、卡片掉率细分

## 编译红线（改代码前必读）

- 1.21.11 用 **`Identifier`**（不是 `ResourceLocation`）
- 实体渲染是 **render state** 模式：`MobRenderer<T, S, M>` + `createRenderState()`
- 实体存档用 **`ValueInput`/`ValueOutput`**（不是 CompoundTag）
- `Item#inventoryTick(stack, ServerLevel, entity, EquipmentSlot)`
- 创造页 `CreativeModeTab.builder(Row, int)`
- `GuiGraphics.blit` 必须带 `RenderPipelines.GUI_TEXTURED` + 纹理尺寸
- 打开菜单用 `IPlayerExtension#openMenu(provider, Consumer<RegistryFriendlyByteBuf>)`
- 权限用 `Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)`（`PermissionSet` 体系）
- 别用 PowerShell `Set-Content` 写 `.java`（会带 BOM）；用 `build_all.py` 或 Python 写
