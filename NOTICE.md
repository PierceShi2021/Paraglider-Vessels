# 版权与署名声明

本模组「容器拓展 / Vessel Expansion」（mod id: `paraglider_vessels`）以 **GNU General Public License v3.0** 发布，许可证全文见 `LICENSE`。

## 上游项目

- **Paragliders** — by Tictim，GPLv3，<https://github.com/Tictim/Paraglider>
  本模组依赖其公开 API、交易配方类型与雕像交易机制，作为独立附属模组运行。
  本仓库**不包含** Paragliders 的源代码或二进制；构建所需的 jar 需由使用者自行获取（见 `libs/README.md`），运行时也需玩家自行安装。

## 美术素材衍生说明

`src/main/resources/assets/paraglider_vessels/textures/item/` 下的 15 张 PNG 贴图，由 Paragliders 的
`heart_container.png` 与 `stamina_vessel.png` 经**色相替换**生成：保留原始轮廓、明暗层次与透明度，仅替换颜色。
依据 GPLv3，这些衍生贴图同样以 GPLv3 发布，并在此注明来源与修改方式。

## 实现参考与署名

以下实现参考了 Paragliders 的 GPLv3 源码结构，在此署名说明：

| 本模组文件 | 参考的上游实现 |
| --- | --- |
| `bargain/VesselBargain.java`（物品消耗与交易校验） | `SimpleBargain.java` |
| `mixin/AntiVesselMixin.java`（排斥容器接管） | `AntiVesselItem.java` |
| `VesselItem.java`（右键使用流程） | `VesselItem.java` |
| `VesselEffects.java`（粒子参数） | `SimpleVesselContainer.java` |
| `ModsLoadedCondition.java`（配方条件注册方式） | `ConfigConditionSerializer.java` |

其余代码（容器数据、属性重算、配置、联动判定、自定义配方与预览等）为本模组原创实现。

## 其他

- `zh_cn.json` / `en_us.json` 中本模组自己的物品名与 tooltip 为原创文本。
- Minecraft、Forge、Apotheosis、Apothic Attributes、TaCZ、TaczAttributeAdd 等名称与商标归各自所有者所有。
