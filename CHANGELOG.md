# Changelog

## [1.7] - 2026-07-08

### 新增

- **桌面便签立即重置**：大便签底部「立即重置」、小便签「重置」，无需打开 App

## [1.6.1] - 2026-07-07

### 修复

- **启动闪退**：`setAlarmClock` 在部分 vivo 机型抛异常，已捕获并降级；Application 启动时延迟注册闹钟

## [1.6] - 2026-07-07

### 修复

- **设置页闪退**：MaterialSwitch 与主题不兼容，改为 SwitchMaterial
- **到点未自动重置**：改用系统闹钟级 `setAlarmClock`，提高国产 ROM 准时触发率

### 改进

- 设置页补充定时重置权限说明（自启动 / 省电 / 闹钟权限）

## [1.5] - 2026-07-06

### 新增

- **自动更新开关**：设置页可关闭「打开 App 时自动检查更新」
- **手动检查更新**：设置页「检查更新」按钮，失败时提示开代理

## [1.4] - 2026-07-06

### 新增

- **桌面便签字体大小**：设置页 4 档调节（默认最小，与 v1.2 字号一致）

## [1.3] - 2026-07-06

### 新增

- **在线更新**：打开 App 时检查 GitHub Release
- App 内下载 APK，完成后调系统安装器
- 用户可「稍后」跳过；下载失败提示开启代理

## [1.2] - 2026-07-06

### 新增

- **两种桌面便签尺寸**：便签 2×3（大）、便签 2×2（小）
- **毛玻璃样式**：圆角半透明背景，设置页可调强度
- **自定义文字颜色**：未完成 / 已完成任务颜色可分别设置（12 色预设）

### 改进

- 小组件默认毛玻璃强度 75%
- 设置页 UI 重组

## [1.1] - 2026-07-03

- 设置页：便签背景透明度
- 去掉便签顶部标题
- 发布脚本支持 v2rayN 代理

## [1.0] - 2026-07-03

首个正式版本。

[1.7]: https://github.com/cheng0225/daily-note/releases/tag/v1.7
[1.6.1]: https://github.com/cheng0225/daily-note/releases/tag/v1.6.1
[1.6]: https://github.com/cheng0225/daily-note/releases/tag/v1.6
[1.5]: https://github.com/cheng0225/daily-note/releases/tag/v1.5
[1.4]: https://github.com/cheng0225/daily-note/releases/tag/v1.4
[1.3]: https://github.com/cheng0225/daily-note/releases/tag/v1.3
[1.2]: https://github.com/cheng0225/daily-note/releases/tag/v1.2
[1.1]: https://github.com/cheng0225/daily-note/releases/tag/v1.1
[1.0]: https://github.com/cheng0225/daily-note/releases/tag/v1.0
