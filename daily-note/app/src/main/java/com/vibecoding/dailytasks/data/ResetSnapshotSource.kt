package com.vibecoding.dailytasks.data

enum class ResetSnapshotSource(val value: String) {
    MANUAL("manual"),
    SCHEDULED("scheduled"),
    WIDGET("widget"),
    CATCHUP("catchup"),
}
