# 每日便签

桌面每日任务便签 Android App（**v1.0.0**）。

> 需求文档见上级目录 [`vibe-coding`](../vibe-coding/README.md)。

## 学习文档

想读懂代码？从 [`docs/README.md`](docs/README.md) 开始，按顺序阅读 01～05。

## 功能

- App 内添加 / 编辑 / 删除任务（最多 15 条）
- App 与桌面小组件均可打钩，状态同步
- 每日定时重置（默认 08:00，可修改）
- 开机 / 打开 App 时补重置
- 手动「立即重置」
- 桌面小组件**透明背景**便签样式

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
releases/daily-note-v0.1.0-debug.apk
```

原始输出路径：`app\build\outputs\apk\debug\app-debug.apk`

### 手动安装（无需 Android Studio）

1. 打开 `releases/` 目录，找到最新 APK
2. 传到手机并点击安装（USB 连接时可直接复制到手机存储）

```bat
adb install -r releases\daily-note-v0.1.0-debug.apk
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
└── receiver/      # 定时重置、开机广播
```

## 技术栈

Kotlin · Room · App Widget · AlarmManager · minSdk 26 · targetSdk 34

## 发布

Git 仓库在 `C:\Android` 根目录。发布 GitHub Release 见 [`scripts/publish-github.ps1`](../scripts/publish-github.ps1)。
