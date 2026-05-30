package com.zafer.maintenancetracker.model

data class MaintenanceLog(
    val id: String = "",             // Raporun kendi id'si
    val deviceId: String = "",       // Hangi cihaza ait olduğu (Inventory id'si)
    val deviceName: String = "",     // Kolaylık olsun diye cihazın adı
    val personnelEmail: String = "",  // Raporu gönderen personelin emaili
    val status: String = "",         // "Çalışıyor", "Arızalı", "Eksik"
    val description: String = "",    // Personelin girdiği not/açıklama
    val imageUrl: String = "",       // Fotoğraf çekildiyse Firebase Storage linki
    val timestamp: Long = 0L         // Kronolojik sıralama için işlem tarihi (Milisaniye)
)