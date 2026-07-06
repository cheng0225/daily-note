# 安装包

| 文件 | 说明 |
|------|------|
| `daily-note-v1.3-debug.apk` | **v1.3** 当前版本 |
| `daily-note-v1.2-debug.apk` | v1.2（无在线更新） |

## v1.3 更新

- **在线更新**：打开 App 检查 GitHub Release，App 内下载并安装
- 连不上 GitHub 时提示开启代理后重试

## v1.2 更新

- 两种便签：**2×3**（大）、**2×2**（小）
- 毛玻璃样式 + 可调强度
- 自定义未完成 / 已完成文字颜色

```bat
adb install -r releases\daily-note-v1.3-debug.apk
```

## 在线更新说明

- 需先手动安装至少一次 **v1.3**（或更高），之后新版本可在 App 内更新
- GitHub Release 须包含对应 APK，命名：`daily-note-v{版本}-debug.apk`
- 发布脚本：[`scripts/publish-github.ps1`](../../scripts/publish-github.ps1)
