# 编译依赖放置说明

本仓库**不分发**任何第三方模组二进制（Paragliders 为 GPLv3，且二进制不应随源码仓库分发）。

首次构建前请手动准备一次：

1. 从 CurseForge 或 Modrinth 下载 `Paraglider-forge-20.1.3.jar`。
2. 重命名为 **`paraglider-1.0.jar`**，放到本目录。

`build.gradle` 通过以下方式引用它：

```groovy
flatDir { dirs 'libs' }
implementation fg.deobf("blank:paraglider:1.0")
```

`libs/*.jar` 已写入 `.gitignore`，不会被提交。

最终构建出的 `paraglider_vessels-1.0.0.jar` **不含** Paragliders 的代码；玩家只需在 `mods/` 中自行安装 Paragliders 即可运行。
