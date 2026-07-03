# 安装包

本目录存放可直接安装到手机的 APK。

| 文件 | 说明 |
|------|------|
| `daily-note-v1.0.0-debug.apk` | **v1.0.0** 当前版本 |

GitHub Release：[Releases 页面](https://github.com/USER/daily-note/releases)（部署后替换链接）

## 手动安装

1. 下载 APK 传到手机，或 USB 复制到手机存储
2. 点击 APK 安装；若提示「未知来源」按系统指引允许

```bat
adb install -r releases\daily-note-v1.0.0-debug.apk
```

## 构建新版

```bat
cd daily-note
gradlew.bat assembleDebug
```

构建完成后 APK 自动复制到本目录。
