# NOTICE / 版权与来源说明

本项目是 **LoliPickaxe**（作者 **Is_GK**）从 Minecraft 1.12.2 (Forge) 到
Minecraft 1.21.11 (NeoForge) 的社区移植。

## 上游作品

- 原始模组：`LoliPickaxe-1.2.12-1.12.2.jar`
- 原作者：**Is_GK**
- 原模组内的贴图、模型、音效、语言文本等美术与音频资源**版权归原作者所有**，
  本仓库沿用这些资源是为了保持移植后的观感一致。
- 原模组作者曾说明模组已在 GitHub 开源；**但上游具体的开源许可证需要在发布前确认**。

## 本仓库新增的内容

- 1.21.11 / NeoForge 21.11 的 Java 移植代码（`src/main/java`）
- 1.21 资源格式转换脚本与转换后的 JSON（模型定义、方块状态、配方、语言文件）
- 本地构建与校验脚本（`build_all.py`、`validate_assets.py`）
- 移植文档（`README.md`、`PORTING.md`、`NEXT_STEPS.md`）

## 发布前必须确认

1. **许可证**：在上游许可证明确之前，不建议直接套用 MIT/Apache 等许可证发布。
   可选做法：
   - 联系原作者取得移植/再分发授权；
   - 或先以「非商业、保留署名、禁止再分发资源」的方式发布，并明确标注资源归属；
   - 或只发布**代码与转换脚本**，资源由使用者自行从原模组提取。
2. **署名**：README、模组描述、Release 说明中都应显著标注原作者 Is_GK 与本移植项目。
3. **资源再分发**：若上游不允许再分发美术资源，需从仓库中移除 `assets/lolipickaxe/textures`、
   `assets/lolipickaxe/models/entity`、`assets/lolipickaxe/sounds` 等原始资源，改为构建时注入。
