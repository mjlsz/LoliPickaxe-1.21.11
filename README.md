# LoliPickaxe 1.12.2 → 1.21.11 移植

[![checks](https://github.com/mjlsz/LoliPickaxe-1.21.11/actions/workflows/build.yml/badge.svg)](https://github.com/mjlsz/LoliPickaxe-1.21.11/actions/workflows/build.yml)
[![license](https://img.shields.io/badge/license-MIT%20(code)%20%2B%20assets%20reserved-blue)](LICENSE)
[![minecraft](https://img.shields.io/badge/Minecraft-1.21.11-green)](https://minecraft.net)
[![neoforge](https://img.shields.io/badge/NeoForge-21.11.48-orange)](https://neoforged.net)

> 仓库：<https://github.com/mjlsz/LoliPickaxe-1.21.11>

> 原模组：`LoliPickaxe-1.2.12-1.12.2.jar`（作者 Is_GK，Forge 1.12.2，含 ASM coremod）
> 目标：Minecraft **1.21.11** / **NeoForge 21.11.48**，Java 21

---

## 0. 交付状态：已通过真机验证 ✅

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

## 1. 先说三件必须知道的事

### 1.1 Forge 没有 1.21.11

MinecraftForge 的版本线在 **1.21.1（51.0.33）** 就停了：

```
maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml
  <latest>1.20.1-47.4.29</latest>
  1.21.x 最后版本：1.21-51.0.33
```

1.21.11 的 Forge 系加载器只有 **NeoForge 21.11.x**（NeoForge 是 Forge 的分叉，
注册表 / 事件 / 数据组件这些概念一脉相承）。因此本工程按 NeoForge 21.11 移植。

### 1.2 编译问题已绕过（本地可用 javac 编译）

`maven.neoforged.net` 当前整体不可达（TCP 超时），国内 BMCLAPI 镜像也只缓存了
`net.neoforged:neoforge`，没有 NeoForm 运行时：

```
* What went wrong:
Execution failed for task ':createMinecraftArtifacts'.
> Could not find net.neoforged:neoform-runtime:2.0.31.
  Searched in:
    - https://mirrors4.qlu.edu.cn/bmclapi/...
    - https://maven.parchmentmc.org/...
    - https://repo.maven.apache.org/maven2/...
```

Gradle 这条路暂时走不通，但编译本身已经不依赖它了（见 1.2.1）。
等官方仓库恢复后也可以直接 `gradle build`。

已做的验证：

| 验证项 | 结果 |
|---|---|
| 反编译 1.12.2 jar（Vineflower 1.12） | 105 个 .java，约 19,300 行，可读 |
| 资源转换（贴图/模型/语言/配方/音效） | 全部转换完成 |
| **1.21.11 真机 API 编译** | **0 error / 0 warning，40 个 class** |
| 模组 jar 打包 | `lolipickaxe-2.0.0.jar`，195 个条目 |
| Gradle + NeoForm 路线 | 仍卡在 `neoform-runtime` 下载 |


### 1.2.1 本地编译管线（本轮新增）

NeoForm 拿不到，于是改用「官方命名 Minecraft jar」直接编译：

1. 从 Mojang 下载 1.21.11 client.jar + client.txt（ProGuard 映射）
2. mapping-io 把 ProGuard 映射转 Tiny v2
3. 	iny-remapper（自写 OutputConsumer，绕过沙箱的 Files.isWritable 误判）把混淆 jar 重映射成官方命名
4. javac 直接对着 mc/named + 
eoforge-21.11.48-universal.jar + 
et.neoforged:bus + 少量 FML 桩类编译
5. uild_jar.py 打包 classes + assets + data + 展开后的 
eoforge.mods.toml

产物：`build/libs/lolipickaxe-2.0.0.jar`（40 个类，195 个条目）。

> 说明：NeoForge 1.20.5+ 运行时使用 Mojang 官方命名，mod 不再需要 reobf，
> 所以这个 jar 在命名空间上是对的；但**尚未在真实客户端里跑过**（需要能下载
> NeoForge 安装器依赖，目前 maven.neoforged.net 仍不可达）。
### 1.3 这不是"改个版本号"，是重写

1.12.2 → 1.21.11 跨了 **9 年、14 个大版本**。原模组 19k 行里，
注册表、NBT、GUI、网络、渲染、配方、Coremod 全部换了 API。逐项对照见
[`PORTING.md`](PORTING.md)。

---

## 2. 已完成

### 2.1 工程与构建

- `build.gradle` / `settings.gradle` / `gradle.properties`：ModDevGradle 2.0.148 +
  NeoForge 21.11.48 + Java 21，仓库指向 BMCLAPI 镜像（并自动改写 NeoForged 仓库 URL）。
- `neoforge.mods.toml` 模板（mod id `lolipickaxe`，版本 `2.0.0`）。
- `src/main/resources/assets/lolipickaxe/`：贴图、模型、方块状态、语言、音效、配方全部就位。

### 2.2 注册内容

| 类型 | 内容 |
|---|---|
| 物品 | `loli_pickaxe`、`small_loli_pickaxe`、14 种升级材料、`loli_dispersal`、`bug_entity_clear`、`loli_card`、`loli_card_album`、`loli_card_online`、`loli_record` |
| 方块 | `loli_blue_screen_tnt`、`loli_exit_tnt`、`loli_fail_respond_tnt`（含 BlockItem） |
| 实体 | `lolipickaxe:loli`（PathfinderMob）、`lolipickaxe:loli_buff_attack_tnt` |
| 音效 | `lolisuccess`、`lolirecord` |
| 创造模式标签页 | 氪金萝莉 / 氪金萝莉-合成材料（升级材料按等级展开） |

### 2.3 玩法逻辑

- **氪金萝莉**：左键秒杀（玩家/生物/非生物）、潜行右键范围秒杀、右键循环采掘范围、
  主人绑定、强制掉落/清空背包/掉线打击/反伤/友军判定等开关（读物品 NBT 覆盖全局配置）。
- **普通萝莉**：14 项升级的等级数值（`getTransformValue` / `getDoubleTransformValue` 原样搬）、
  范围采掘、范围攻击、飞行、闪避/减伤、背包页数、自动熔炼。
- **事件**：`AttackEntityEvent`、`LivingIncomingDamageEvent`、`PlayerTickEvent`、
  `BlockEvent.BreakEvent`、`PlayerInteractEvent.LeftClickEmpty` 全部换成 NeoForge 事件。
- **网络**：`lolipickaxe:kill_facing`（CustomPacketPayload + StreamCodec）。
- **配置**：`ModConfigSpec`，键名与原版一致（`loliPickaxeXxx`），并保留"物品 NBT 覆盖"语义。
- **客户端**：萝莉实体模型（128×128 盒子模型，逐块搬运）、渲染器、模型层注册。

### 2.4 资源

- `.lang` → `en_us.json` / `zh_cn.json`（99 / 107 条）
- 27 个物品模型 + 1.21 `items/*.json` 物品定义
- 方块状态 / 方块模型 / OBJ 资源 / 卡片图片 / 音效原样保留
- 18 个配方转换为 1.21 格式（去掉 `data`，`result.item` → `result.id`）

### 2.5 本轮新增（第 2 轮）

- **储藏室 GUI**：`LoliStorage`（NBT 持久化，`ItemStack.CODEC` 序列化）+ `LoliStorageMenu`
  （6×9 分页、`DataSlot` 同步页码、shift 点击快速移动）+ `LoliStorageScreen`（翻页按钮）。
  手持普通萝莉按 Shift+右键打开。
- **9 合 1 升级配方**：14 个 `crafting_shapeless` 配方 + `AddonCraftingHandler`，
  在 `ItemCraftedEvent` 里读取合成矩阵，校验 9 个同级升级并写入 `level+1`；
  等级不一致或已满级时清空产物。
- 新增网络包 `lolipickaxe:change_page`。

### 2.6 本轮新增（第 5 轮）

- **时运 / 自动熔炼**：LoliDropHandler 监听 BlockDropsEvent，按 LoliFortuneLevel
  追加掉落，LoliAutoFurnace 满级时用 RecipeType.SMELTING 把掉落物烧一遍（失败则保留原物）。
- **稀有掉落**：LivingDropsEvent 里按配置概率掉生物灵魂 / 卡片 / 唱片（需玩家手持氪金萝莉击杀）。
- **/loli 命令**：新增 43 个配置项的读写（/loli <flag> [value]，权限等级 GAMEMASTERS），
  顺带适配 1.21.11 新的 PermissionSet API。
- **语言文件修正**：1.12 的 item.xxx.name 全部映射为 1.21 的
  item.lolipickaxe.<id> / lock.lolipickaxe.<id> / entity.lolipickaxe.<id>，
  并补了 GUI/提示文本（zh 137 条、en 125 条）。
### 2.7 本轮新增（第 6 轮）

- **储藏室对两把镐都可用**：抽出 `ILoliStorageHolder`，氪金萝莉按 `loliPickaxeMaxPage` 开页，
  普通萝莉按背包升级等级开页；菜单/存档逻辑完全复用。
- **快捷键打开储藏室**：`V`（`key.lolipickaxe.storage`），客户端发包 → 服务端
  `IPlayerExtension#openMenu` 带 slot 参数；1.21.11 的 `KeyMapping.Category`
  需要注册 `Identifier`，已适配。
- 新增 `lolipickaxe:open_storage` 载荷；语言文件补快捷键与分类名（zh 139 / en 127）。
### 2.8 本轮新增（第 7 轮）

- **配置 GUI**（`C` 键）：按 `Config.FLAGS` 自动列出全部布尔开关，两列分页，
  每个按钮显示 `ON/OFF`，点击即写入手持物品 NBT 并同步服务端
- **`lolipickaxe:set_flag` 载荷**：服务端校验手持物品后写入对应 flag
- **一键构建脚本** `build_all.py`：去 BOM → 编译 FML 桩 → javac → 组装 jar，
  避免 PowerShell 写文件带 BOM 的老问题
### 2.9 本轮新增（第 8 轮）：资源自检

- 新增 `validate_assets.py`：校验 items 定义 → 模型 → 贴图 → parent 链、
  配方产物是否有物品定义、中英文键是否对齐
- 修掉的问题：
  - `small_loli_pickaxe` 没有物品定义（补 items 定义，复用镐子模型）
  - 删掉两个无贴图的废弃模型（`loli_pickaxe_stick` / `_end`）
  - 补齐英文缺失键（方块/实体/升级等级/GUI 等 10 项），en 145 条
- 现在校验结果：**200 个条目，0 problem**
### 2.10 本轮新增（第 9 轮）

- **附魔书附魔**：手持氪金萝莉、副手拿附魔书右键，把书上的附魔按等级叠加到镐子上
  （`EnchantmentHelper.updateEnchantments` + `ItemEnchantments.Mutable#upgrade`），
  书被消耗；1.12.2 是用 GUI 做的，这里用更直接的交互替代
- 语言补 `loliPickaxe.enchant`
### 2.11 本轮新增（第 10 轮）

- **药水效果**：手持氪金萝莉、副手拿药水右键把效果存进 NBT（`LoliPotion` 列表，同 id 取最高等级）；
  之后每 tick 以 410 tick 时长持续施加（对应原版 `ItemLoliPickaxe` 的 `LoliPotion` 逻辑与药水 GUI 的下游效果）
- 语言补 `loliPickaxe.potion`
### 2.12 本轮新增（第 11 轮）

- **卡片入册**：手持卡片、副手拿卡片册右键，卡片 id 写入册子 NBT（上限 54 张，重复卡不叠加）
- 语言补 `gui.loliCardAlbum.add`
- 回归：`javac` 0 error、资源校验 0 problem、`jdeps` 无缺失依赖
### 2.6 仍未完成（继续）

- 配置/附魔/药水 GUI、`/loli` 命令、配置同步
- 卡片册/网络卡片渲染（OBJ）
- Coremod → Mixin
- 自动熔炼/时运逻辑、掉落概率

---

## 3. 未完成 / 已简化（需要继续）

| 项 | 状态 | 说明 |
|---|---|---|
| 储藏室 GUI（`ContainerLoliPickaxe`、翻页、黑名单） | ❌ | 1.21 要 `MenuType` + `AbstractContainerMenu` + `Screen` + 数据同步，工作量大 |
| 配置/附魔/药水 GUI + 快捷键 | ❌ | 原版 4 个 GUI 键；`RegisterMenuScreensEvent` 已可用 |
| 卡片册/网络卡片渲染（OBJ + TESR） | ❌ | 1.21 移除内置 OBJ 加载器，需要自写加载器或改盒子模型 |
| 9 合 1 升级配方（`SuperpositionRecipe`） | ❌ | 需要自定义 `RecipeSerializer` + 组件匹配 |
| Coremod（`LoliPickaxeTransformer`，约 30 处 ASM） | ❌ | 1.21 用 Mixin；涉及改原版类，风险高，暂不移植 |
| 唱片机播放 / 卡片掉落概率 / 死亡惩罚 | ⚠️ | 配置项已留，逻辑待接 |
| 实体 AI（游泳、跨维度重生） | ⚠️ | 已简化 |

如果只想要"能玩"的最小闭环，优先补：储藏室 GUI → 升级配方 → 卡片渲染。

---

## 4. 目录

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

## 5. 关键 API 对照（详见 PORTING.md）

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

## 6. 构建

网络恢复（或使用能访问 `maven.neoforged.net` 的代理）后：

```powershell
$env:JAVA_HOME = "F:\ds\lolipickaxe-port\tools\jdk21\jdk-21.0.13+9"
cd F:\ds\lolipickaxe-port\LoliPickaxe
F:\ds\lolipickaxe-port\tools\gradle-9.2.1\bin\gradle.bat build
```

产物：`build/libs/lolipickaxe-2.0.0.jar`

若仍走镜像，确认 `settings.gradle` 中 BMCLAPI 仓库排在第一位；
一旦官方源恢复，可删除 `build.gradle` 末尾的 URL 改写块。









