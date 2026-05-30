package com.zafer.maintenancetracker.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class MaintenanceLog(
    val id: String = "",
    val deviceId: String = "",
    val deviceName: String = "",
    val personnelEmail: String = "",
    val status: String = "",
    val note: String = "",
    val photoUrl: String = "",
    val timestamp: Timestamp? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    val context = LocalContext.current
    var logs by remember { mutableStateOf<List<MaintenanceLog>>(emptyList()) }
    var deviceMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    var showAddUserDialog by remember { mutableStateOf(false) }
    var newUserEmail by remember { mutableStateOf("") }
    var newUserPassword by remember { mutableStateOf("") }
    val roleOptions = listOf("personnel", "admin")
    var selectedRole by remember { mutableStateOf(roleOptions[0]) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()

        // Önce Inventory'den cihaz adlarını çek
        db.collection("Inventory").get().addOnSuccessListener { inventorySnapshot ->
            val map = inventorySnapshot.documents.associate { doc ->
                doc.id to (doc.getString("name") ?: "Bilinmiyor")
            }
            deviceMap = map

            // Sonra logları çek
            db.collection("MaintenanceLogs")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        isLoading = false
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val logList = snapshot.documents.map { doc ->
                            val devId = doc.getString("deviceId") ?: ""
                            MaintenanceLog(
                                id = doc.id,
                                deviceId = devId,
                                deviceName = map[devId] ?: "Bilinmiyor",
                                personnelEmail = doc.getString("personnelEmail") ?: "Bilinmiyor",
                                status = doc.getString("status") ?: "Bilinmiyor",
                                note = doc.getString("note") ?: "Not yok",
                                photoUrl = doc.getString("photoUrl") ?: "",
                                timestamp = doc.getTimestamp("timestamp")
                            )
                        }
                        logs = logList
                        isLoading = false
                    }
                }
        }
    }

    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("tr"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yönetici Kontrol Paneli") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddUserDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (logs.isEmpty()) {
                Text(
                    text = "Henüz sisteme düşen bir arıza/bakım raporu yok.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(logs) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (log.status == "Arızalı") MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.deviceName,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = log.status,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Raporlayan: ${log.personnelEmail}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tarih: ${log.timestamp?.toDate()?.let { dateFormatter.format(it) } ?: "Bilinmiyor"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Not: ${log.note}",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                if (log.photoUrl.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    AsyncImage(
                                        model = log.photoUrl,
                                        contentDescription = "Arıza Fotoğrafı",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddUserDialog) {
            AlertDialog(
                onDismissRequest = { showAddUserDialog = false },
                title = { Text("Yeni Kullanıcı Ekle") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newUserEmail,
                            onValueChange = { newUserEmail = it },
                            label = { Text("E-posta") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newUserPassword,
                            onValueChange = { newUserPassword = it },
                            label = { Text("Şifre (En az 6 hane)") },
                            singleLine = true
                        )
                        Text("Kullanıcı Rolü:", modifier = Modifier.padding(top = 8.dp))
                        roleOptions.forEach { role ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = (selectedRole == role),
                                    onClick = { selectedRole = role }
                                )
                                Text(text = if (role == "personnel") "Saha Personeli" else "Yönetici (Admin)")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newUserEmail.isNotEmpty() && newUserPassword.length >= 6) {
                                val auth = FirebaseAuth.getInstance()
                                val db = FirebaseFirestore.getInstance()
                                auth.createUserWithEmailAndPassword(newUserEmail, newUserPassword)
                                    .addOnSuccessListener { authResult ->
                                        val uid = authResult.user?.uid ?: return@addOnSuccessListener
                                        val userMap = hashMapOf(
                                            "email" to newUserEmail,
                                            "role" to selectedRole
                                        )
                                        db.collection("Users").document(uid).set(userMap)
                                            .addOnSuccessListener {
                                                showAddUserDialog = false
                                                Toast.makeText(context, "Kullanıcı eklendi! Güvenlik için giriş ekranına yönlendiriliyorsunuz.", Toast.LENGTH_LONG).show()
                                                navController.navigate("login_screen") {
                                                    popUpTo(0)
                                                }
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                Toast.makeText(context, "Geçerli bir e-posta ve şifre girin.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Kaydet")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddUserDialog = false }) {
                        Text("İptal")
                    }
                }
            )
        }
    }
}