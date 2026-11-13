# 容器拓展（Vessel Expansion）

[Paragliders](https://github.com/Tictim/Paraglider) 的附属模组，为 Minecraft 1.20.1 / Forge 增加 15 种可永久提升属性的「容器」，并接入原模组的女神雕像与恶魔雕像交易系统。

源码仓库：<https://github.com/PierceShi2021/Paraglider-Vessels> ｜ 问题反馈：<https://github.com/PierceShi2021/Paraglider-Vessels/issues>

## 功能

- **15 种容器**，每种有独立的使用上限与每级属性加成（配置可调，默认上限 20）
  - 常规：力量、迅捷、御之、幸运、敏之、坚毅
  - Apotheosis 联动：暴击、暴伤、甲穿、经验、治愈
  - TaCZ 联动：枪手、精准、稳定、强袭
- **雕像交易**：女神雕像 4 个试炼通过证换 1 个容器；恶魔雕像卖出 / 买入可切换任意容器（一次切换净耗 1 绿宝石），精华与原模组共享
- **排斥容器兼容**：卸载并返还全部新旧容器
- **联动开关**：联动容器仅在对应模组齐备时注册，缺模组则物品、配方、创造标签全部不出现
- 使用时有粒子与音效；容器数据随玩家存档持久化，死亡 / 换维不丢失

## 运行环境

| 项目 | 版本 |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.4.16+ |
| Java | 17 |
| Paragliders | 20.1.3（必需） |
| Apotheosis + Apothic Attributes | 可选，启用神化联动容器 |
| TaCZ + TaczAttributeAdd | 可选，启用 TACZ 联动容器 |

## 构建

第三方模组的二进制不在本仓库中，构建前请先准备一次：

1. 从 CurseForge / Modrinth 下载 `Paraglider-forge-20.1.3.jar`。
2. 重命名为 `paraglider-1.0.jar`，放到 `libs/`（详见 `libs/README.md`）。
3. 执行：

```bash
./gradlew build          # Windows: gradlew.bat build
```

产物位于 `build/libs/paraglider_vessels-1.0.0.jar`。

## 安装

将构建产物与 Paragliders 一起放入 `mods/` 即可。本模组 jar 不含 Paragliders 的代码或二进制。

## 配置

SERVER 类型配置文件 `paraglider_vessels-server.toml`，共 15 段，每段包含 `maxVessels`（上限）与每级加成字段（可为负值，例如稳定容器降低后坐力）。整合包可通过 `defaultconfigs/` 提供默认值；已有存档请修改 `saves/<世界>/serverconfig/` 下的同名文件。

属性按 `min(已使用数量, 上限) × 每级加成` 计算；调低上限不会删除已获得的容器计数。

## 许可证

本模组以 **GNU General Public License v3.0** 发布，详见 `LICENSE`。贴图为 Paragliders 素材的改色衍生，部分实现参考了其源码，署名与修改说明见 `NOTICE.md`。

Minecraft、Forge 以及所联动模组名称与商标归各自所有者所有，本项目不主张任何权利。
