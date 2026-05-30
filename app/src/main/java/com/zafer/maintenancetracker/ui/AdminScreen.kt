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


data class MaintenanceLog(
    val id: String = "",
    val deviceId: String = "",
    val personnelEmail: String = "",
    val status: String = "",
    val note: String = "",
    val photoUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    val context = LocalContext.current
    var logs by remember { mutableStateOf<List<MaintenanceLog>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }


    var showAddUserDialog by remember { mutableStateOf(false) }
    var newUserEmail by remember { mutableStateOf("") }
    var newUserPassword by remember { mutableStateOf("") }
    val roleOptions = listOf("personnel", "admin")
    var selectedRole by remember { mutableStateOf(roleOptions[0]) }


    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("MaintenanceLogs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    isLoading = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val logList = snapshot.documents.map { doc ->
                        MaintenanceLog(
                            id = doc.id,
                            deviceId = doc.getString("deviceId") ?: "Bilinmiyor",
                            personnelEmail = doc.getString("personnelEmail") ?: "Bilinmiyor",
                            status = doc.getString("status") ?: "Bilinmiyor",
                            note = doc.getString("note") ?: "Not yok",
                            photoUrl = doc.getString("photoUrl") ?: ""
                        )
                    }
                    logs = logList
                    isLoading = false
                }
            }
    }

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
                // İKON YERİNE DÜZ METİN KULLANIYORUZ, HATA VERMEYECEK
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
                                    Text(text = "Cihaz ID: ${log.deviceId}", style = MaterialTheme.typography.titleMedium)
                                    Text(text = log.status, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Raporlayan: ${log.personnelEmail}", style = MaterialTheme.typography.bodySmall)

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Not: ${log.note}", style = MaterialTheme.typography.bodyLarge)

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

        // KULLANICI EKLEME PENCERESİ
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

                                        // Firestore'a kaydet
                                        val userMap = hashMapOf(
                                            "email" to newUserEmail,
                                            "role" to selectedRole
                                        )
                                        db.collection("Users").document(uid).set(userMap)
                                            .addOnSuccessListener {
                                                showAddUserDialog = false
                                                Toast.makeText(context, "Kullanıcı eklendi! Güvenlik için giriş ekranına yönlendiriliyorsunuz.", Toast.LENGTH_LONG).show()
                                                // Auth değiştiği için login ekranına atıyoruz
                                                navController.navigate("login_screen") {
                                                    popUpTo(0) // Geçmişi temizle
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