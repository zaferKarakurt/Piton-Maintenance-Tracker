package com.zafer.maintenancetracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

// Cihaz verilerini tutacak basit bir model sınıfı
data class Device(
    val id: String = "",
    val name: String = "",
    val serialNo: String = "",
    val type: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonnelScreen(navController: NavController) {
    // Firestore'dan çekeceğimiz cihaz listesini tutan State
    var deviceList by remember { mutableStateOf<List<Device>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Ekran ilk açıldığında Firestore'dan verileri çekiyoruz
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Inventory")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { document ->
                    Device(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        serialNo = document.getString("serialNo") ?: "",
                        type = document.getString("type") ?: ""
                    )
                }
                deviceList = list
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saha Personel Paneli") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Veriler yüklenirken dönecek olan çubuk
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (deviceList.isEmpty()) {
                Text(
                    text = "Kayıtlı cihaz bulunamadı.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Cihazların listelendiği yer
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Arıza/Durum Kontrolü İçin Cihaz Seçin:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(deviceList) { device ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Tıklanan cihazın ID'sini de yanımıza alarak yeni ekrana gidiyoruz
                                    navController.navigate("status_check_screen/${device.id}")
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = device.name, style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Tip: ${device.type}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Seri No: ${device.serialNo}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}