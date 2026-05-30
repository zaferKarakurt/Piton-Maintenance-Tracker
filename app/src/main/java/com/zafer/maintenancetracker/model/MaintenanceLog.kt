package com.zafer.maintenancetracker.model

data class MaintenanceLog(
    val id: String = "",
    val deviceId: String = "",
    val deviceName: String = "",
    val personnelEmail: String = "",
    val status: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L
)