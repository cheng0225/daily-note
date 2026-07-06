# 技术方案

> 代码实现位于 [`daily-note/`](../../daily-note/) 目录。

## 1. 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                      Android 系统                        │
├─────────────────────────────────────────────────────────┤
│  Launcher 桌面                                           │
│    └── App Widget（今日任务列表 + 点击打钩）              │
├─────────────────────────────────────────────────────────┤
│  本应用                                                  │
│    ├── MainActivity（任务编辑 + 重置时间设置 + 启动检查更新）
│    ├── TaskWidgetProvider（小组件逻辑）                   │
│    ├── TaskRepository（读写本地数据）                     │
│    ├── update/（GitHub Release 检查、下载、安装）          │
│    └── DailyResetReceiver / Worker（定时重置）            │
└─────────────────────────────────────────────────────────┘
```

## 2. 模块划分

| 模块 | 职责 |
|------|------|
| **ui/widget** | `AppWidgetProvider`、RemoteViews 布局、点击 PendingIntent |
| **ui/main** | 设置页：任务 CRUD、重置时间 |
| **data** | 任务实体、Repository、本地存储 |
| **domain** | 重置判断、状态切换（可选，小项目可合并到 data） |
| **receiver** | 每日闹钟触发、开机补重置（`BOOT_COMPLETED`） |
| **update** | GitHub API 查版本、下载 APK、FileProvider 安装 |

## 3. 关键技术点

### 3.1 桌面小组件

- 使用 `AppWidgetProvider` + `RemoteViews`
- 列表类小组件两种方案：
  - **方案 A**：`ListView` + `RemoteViewsService`（适合任务较多、可滚动）
  - **方案 B**：固定条数 `TextView` + `CheckBox`（任务 ≤ 8 条时更简单）
- 点击事件：`setOnClickPendingIntent` + 自定义 `BroadcastReceiver` 更新单条状态
- 数据变更后调用 `AppWidgetManager.updateAppWidget()`

### 3.2 本地存储

**推荐（MVP）**：Room 或 DataStore + 自定义序列化

```
TaskEntity
  - id: Long
  - title: String
  - sortOrder: Int
  - isCompleted: Boolean

AppSettings
  - resetHour: Int
  - resetMinute: Int
  - lastResetDate: String  // "2026-07-02"
```

### 3.3 每日重置

```
设置/修改重置时间
    → 取消旧 Alarm，按新时间注册下一次触发

Alarm 触发 / 开机启动
    → 若 lastResetDate < today（按本地时区）
        → 全部 isCompleted = false
        → lastResetDate = today
        → 刷新 Widget
```

- **AlarmManager.setExactAndAllowWhileIdle** 或 **WorkManager PeriodicWork**（精确到分钟用 Alarm 更合适）
- 监听 `ACTION_BOOT_COMPLETED`：开机后检查是否漏重置

### 3.4 在线更新（v1.3）

```
App 冷启动
    → GET /repos/{owner}/{repo}/releases/latest
    → 比较 tag 与 BuildConfig.VERSION_NAME
    → 有新版本且未跳过 → 弹窗
    → 下载 browser_download_url → cacheDir
    → FileProvider + ACTION_VIEW 调系统安装器
```

- 仓库：`cheng0225/daily-note`
- APK 命名：`daily-note-v{versionName}-debug.apk`
- 网络失败：启动检查静默；用户点更新后下载失败则提示开代理

### 3.5 权限

| 权限 | 用途 |
|------|------|
| `RECEIVE_BOOT_COMPLETED` | 开机补重置 |
| `SCHEDULE_EXACT_ALARM` | Android 12+ 精确定时（若 targetSdk ≥ 31） |
| `INTERNET` | GitHub Release 检查与 APK 下载 |
| `REQUEST_INSTALL_PACKAGES` | Android 8+ 安装更新包 |
| `POST_NOTIFICATIONS` | 不需要（无通知） |

## 4. 包结构（拟定）

```
com.vibecoding.dailytasks
├── data
│   ├── TaskEntity.kt
│   ├── TaskDao.kt
│   ├── AppDatabase.kt
│   └── TaskRepository.kt
├── widget
│   ├── TaskWidgetProvider.kt
│   └── TaskWidgetService.kt      // 若用 ListView 方案
├── ui
│   └── MainActivity.kt
├── receiver
│   ├── DailyResetReceiver.kt
│   └── BootReceiver.kt
├── update
│   ├── UpdateManager.kt
│   ├── GithubReleaseClient.kt
│   └── ApkInstaller.kt
└── DailyTasksApp.kt
```

## 5. 风险与对策

| 风险 | 对策 |
|------|------|
| 国产 ROM 杀后台，闹钟不准 | 开机补重置 + 打开 App 时校验日期 |
| 小组件点击无响应 | 各 ROM 测试；PendingIntent 使用唯一 requestCode |
| 精确闹钟权限被用户拒绝 | 设置页说明用途；降级为近似闹钟 + 打开 App 时校验 |
| RemoteViews 列表刷新慢 | 单条更新后局部 `notifyAppWidgetViewDataChanged` |
| GitHub 国内不可达 | 下载失败提示开代理；启动检查失败静默跳过 |

## 6. 依赖（拟定）

- AndroidX Core、AppCompat、Material
- Room（或 DataStore Preferences）
- Kotlin Coroutines（可选，小项目也可用主线程 + Repository）

---

*文档版本：v0.2 | 更新日期：2026-07-06*
