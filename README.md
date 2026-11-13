# LoliPickaxe 1.12.2 → 1.21.11 移植

[![checks](https://github.com/mjlsz/LoliPickaxe-1.21.11/actions/workflows/build.yml/badge.svg)](https://github.com/mjlsz/LoliPickaxe-1.21.11/actions/workflows/build.yml)
[![license](https://img.shields.io/badge/license-MIT%20(code)%20%2B%20assets%20reserved-blue)](LICENSE)
[![minecraft](https://img.shields.io/badge/Minecraft-1.21.11-green)](https://minecraft.net)
[![neoforge](https://img.shields.io/badge/NeoForge-21.11.48-orange)](https://neoforged.net)

> 仓库：<https://github.com/mjlsz/LoliPickaxe-1.21.11>

> 原模组：`LoliPickaxe-1.2.12-1.12.2.jar`（作者 Is_GK，Forge 1.12.2，含 ASM coremod）
> 目标：Minecraft **1.21.11** / **NeoForge 21.11.48**，Java 21

---

## 0. 状态：已通过真机验证 

2026-11-13 在 NeoForge 21.11.48 / Minecraft 1.21.11 客户端中实测：模组正常加载，
创造页、物品贴图、方块、合成、GUI 全部可用。

最终产物：`build/libs/lolipickaxe-2.0.0.jar`
- 大小 27,386,858 字节
- SHA-256 `5e9a40c4dd1545842bf91a37ff6d7002e7ef036a6854eca3f456b691608448d4`

真机阶段修掉的四个问题（编译期发现不了）：

| 报错 | 原因 | 修复 |
|---|---|---|
| `ResolutionException: Module lolipickaxe contains package net.minecraft.client.gui.screens` | 为绕过包私有访问，把辅助类放进了 MC 的包 → JPMS 拆分包 | 删除该类，改为直接注册菜单屏幕 |
| `NoSuchFieldError: FMLEnvironment.dist` | 编译用 FML 桩的字段与真实 FancyModLoader 不一致 | 改用 `Class.forName("net.minecraft.client.Minecraft")` 探测客户端 |
| 全部贴图粉黑格 | 沿用 1.12 的 `textures/items`、`textures/blocks` | 改为 1.21 的 `textures/item`、`textures/block`，模型引用同步 |
| 方块/掉落表失效 | 6 个 JSON 带 UTF-8 BOM | 全部清除（构建脚本已防） |

## 0.1 安装

1. 安装 **NeoForge 21.11.48** 的 Minecraft **1.21.11** 客户端（版本必须匹配）
2. 把 `lolipickaxe-2.0.0.jar` 放进 `mods/`
3. 启动即可；首次进入前建议备份存档

服务端同样可用（GUI 与按键仅客户端生效）。

## 0.2 仓库结构

```
LoliPickaxe/
├── src/main/java/com/anotherstar/    # 移植代码（物品/方块/实体/GUI/网络/配方）
├── src/main/resources/               # 1.21 资源与数据包（assets / data）
├── src/main/templates/               # neoforge.mods.toml 模板
├── build.gradle / settings.gradle    # NeoForge 21.11 + ModDevGradle
├── build_all.py                      # 本地无 Gradle 构建（javac + 打包）
├── validate_assets.py                # 资源一致性自检
├── README.md / PORTING.md / NEXT_STEPS.md / NOTICE.md
```

## 0.3 致谢

- 原作者 **Is_GK**：LoliPickaxe 1.12.2
- 本移植：代码移植、资源格式转换、构建与验证工具
- 资源归属与再分发注意事项见 [NOTICE.md](NOTICE.md)


## 1. 已完成

### 1.1 工程与构建

- `build.gradle` / `settings.gradle` / `gradle.properties`：ModDevGradle 2.0.148 +
  NeoForge 21.11.48 + Java 21，仓库指向 BMCLAPI 镜像（并自动改写 NeoForged 仓库 URL）。
- `neoforge.mods.toml` 模板（mod id `lolipickaxe`，版本 `2.0.0`）。
- `src/main/resources/assets/lolipickaxe/`：贴图、模型、方块状态、语言、音效、配方全部就位。

### 1.2 注册内容

| 类型 | 内容 |
|---|---|
| 物品 | `loli_pickaxe`、`small_loli_pickaxe`、14 种升级材料、`loli_dispersal`、`bug_entity_clear`、`loli_card`、`loli_card_album`、`loli_card_online`、`loli_record` |
| 方块 | `loli_blue_screen_tnt`、`loli_exit_tnt`、`loli_fail_respond_tnt`（含 BlockItem） |
| 实体 | `lolipickaxe:loli`（PathfinderMob）、`lolipickaxe:loli_buff_attack_tnt` |
| 音效 | `lolisuccess`、`lolirecord` |
| 创造模式标签页 | 氪金萝莉 / 氪金萝莉-合成材料（升级材料按等级展开） |

### 1.3 玩法逻辑

- **氪金萝莉**：左键秒杀（玩家/生物/非生物）、潜行右键范围秒杀、右键循环采掘范围、
  主人绑定、强制掉落/清空背包/掉线打击/反伤/友军判定等开关（读物品 NBT 覆盖全局配置）。
- **普通萝莉**：14 项升级的等级数值（`getTransformValue` / `getDoubleTransformValue` 原样搬）、
  范围采掘、范围攻击、飞行、闪避/减伤、背包页数、自动熔炼。
- **事件**：`AttackEntityEvent`、`LivingIncomingDamageEvent`、`PlayerTickEvent`、
  `BlockEvent.BreakEvent`、`PlayerInteractEvent.LeftClickEmpty` 全部换成 NeoForge 事件。
- **网络**：`lolipickaxe:kill_facing`（CustomPacketPayload + StreamCodec）。
- **配置**：`ModConfigSpec`，键名与原版一致（`loliPickaxeXxx`），并保留"物品 NBT 覆盖"语义。
- **客户端**：萝莉实体模型（128×128 盒子模型，逐块搬运）、渲染器、模型层注册。

### 1.4 资源

- `.lang` → `en_us.json` / `zh_cn.json`（99 / 107 条）
- 27 个物品模型 + 1.21 `items/*.json` 物品定义
- 方块状态 / 方块模型 / OBJ 资源 / 卡片图片 / 音效原样保留
- 18 个配方转换为 1.21 格式（去掉 `data`，`result.item` → `result.id`）


## 2. 目录

```
LoliPickaxe/
├── build.gradle / settings.gradle / gradle.properties
├── src/main/templates/META-INF/neoforge.mods.toml
├── src/main/java/com/anotherstar/
│   ├── LoliPickaxe.java                 # @Mod 入口
│   ├── common/
│   │   ├── CommonEvents.java            # 事件（攻击/受伤/tick/破坏方块）
│   │   ├── block/BlockLoliTNT.java
│   │   ├── config/Config.java           # ModConfigSpec + 物品 NBT 覆盖
│   │   ├── entity/EntityLoli.java
│   │   ├── entity/EntityLoliBuffAttackTNT.java
│   │   └── item/…                       # 全部物品
│   ├── client/…                         # 模型 / 渲染器 / 左键发包
│   ├── network/…                        # payload + 注册
│   ├── registry/…                       # 物品/方块/实体/菜单/配方/音效/标签页
│   └── util/…                           # LoliData(NBT) / LoliUtil(秒杀/范围采掘)
└── src/main/resources/assets/lolipickaxe/…
```

反编译原始码与参考资源在上一层：

```
F:\ds\lolipickaxe-port\decompiled\      # 105 个反编译源文件
F:\ds\lolipickaxe-port\original_assets\ # 原 jar 的资源
F:\ds\lolipickaxe-port\refs\neoforge-src\ # NeoForge 21.11.48 源码（API 对照）
```

---

## 3. 关键 API 对照（详见 PORTING.md）

| 1.12.2 | 1.21.11 |
|---|---|
| `@Mod(modid=…)+@EventHandler` | `@Mod` + 构造器 `(IEventBus, ModContainer)` |
| `GameRegistry.register` / `Register<Item>` | `DeferredRegister.Items` + `registerItem` |
| `ItemStack` metadata (`getItemDamage`) | `DataComponents.CUSTOM_DATA`（`CustomData`） |
| `NBTTagCompound` 可变 | `LoliData` 读改写封装 |
| `IForgeRegistry` / `ResourceLocation` | `Registry` / **`Identifier`**（1.21.9 改名） |
| `IGuiHandler` | `MenuType` + `AbstractContainerMenu` + `Screen` |
| `SimpleNetworkWrapper` | `CustomPacketPayload` + `StreamCodec` + `PayloadRegistrar` |
| `ModelLoader.setCustomModelResourceLocation` | `assets/<ns>/items/*.json` 物品定义 |
| `CreativeTabs` | `CreativeModeTab` + `DeferredRegister` |
| `Configuration` | `ModConfigSpec` |
| ASM `IClassTransformer` | Mixin |

---










