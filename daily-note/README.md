# 每日便签

桌面每日任务便签 Android App（**v1.4**）。

> 需求文档见上级目录 [`vibe-coding`](../vibe-coding/README.md)。

## 学习文档

想读懂代码？从 [`docs/README.md`](docs/README.md) 开始，按顺序阅读 01～07。

## 功能

- App 内添加 / 编辑 / 删除任务（最多 15 条）
- App 与桌面小组件均可打钩，状态同步
- 每日定时重置（默认 08:00，可修改）
- 开机 / 打开 App 时补重置
- 手动「立即重置」
- 两种桌面便签：**2×3**（大）、**2×2**（小）
- **毛玻璃**便签样式，设置页可调强度
- **便签字体大小** 4 档可调（默认最小）
- **自定义**未完成 / 已完成文字颜色
- **在线更新**（v1.3）：打开 App 检查 GitHub Release，App 内下载并安装

## 如何运行

### Android Studio（推荐）

1. **File → Open** → 选择本目录 `daily-note`
2. 等待 Gradle 同步
3. 连接手机或启动模拟器，点击 **Run ▶**

### 命令行打包

```bat
cd daily-note
gradlew.bat assembleDebug
```

构建完成后，安装包会自动复制到：

```
releases/daily-note-v{version}-debug.apk
```

原始输出路径：`app\build\outputs\apk\debug\app-debug.apk`

### 手动安装（无需 Android Studio）

1. 打开 `releases/` 目录，找到最新 APK
2. 传到手机并点击安装（USB 连接时可直接复制到手机存储）

```bat
adb install -r releases\daily-note-v1.3-debug.apk
```

## 添加桌面小组件

长按桌面 → 小组件 → **每日便签** → **每日任务** → 拖到桌面

## 代码结构

```
app/src/main/java/com/vibecoding/dailytasks/
├── MainActivity.kt
├── DailyTasksApp.kt
├── ResetScheduler.kt
├── data/          # Room 数据库
├── ui/            # 列表适配器
├── widget/        # 桌面小组件
├── update/        # GitHub 在线更新（v1.3）
└── receiver/      # 定时重置、开机广播
```

## 技术栈

Kotlin · Room · App Widget · AlarmManager · minSdk 26 · targetSdk 34

## 发布

Git 仓库在 `C:\Android` 根目录。发布 GitHub Release 见 [`scripts/publish-github.ps1`](../scripts/publish-github.ps1)。
