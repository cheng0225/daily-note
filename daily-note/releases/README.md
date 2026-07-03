# 安装包

| 文件 | 说明 |
|------|------|
| `daily-note-v1.1-debug.apk` | **v1.1** 当前版本 |

## v1.1 更新

- 设置页：调节便签背景透明度
- 去掉便签顶部「今日任务」标题

```bat
adb install -r releases\daily-note-v1.1-debug.apk
```

## 构建

```bat
cd daily-note
gradlew.bat assembleDebug
```
