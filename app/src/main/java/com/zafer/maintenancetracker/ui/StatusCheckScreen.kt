package com.zafer.maintenancetracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusCheckScreen(
    deviceId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val statusOptions = listOf("Çalışıyor", "Arızalı", "Eksik")
    var selectedStatus by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }


    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var uploadedImageUrl by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    // 1. Kamera Başlatıcı
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedImage = bitmap
            isUploading = true


            coroutineScope.launch {
                val url = uploadToCloudinary(bitmap)
                isUploading = false
                if (url != null) {
                    uploadedImageUrl = url
                    Toast.makeText(context, "Fotoğraf Yüklendi!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Yükleme Başarısız!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Kamera izni
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // İzin verildiyse kamerayı aç
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Fotoğraf çekmek için kamera izni gerekli!", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Durum Kontrolü") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Cihaz Durumunu Seçin:", style = MaterialTheme.typography.titleMedium)

            statusOptions.forEach { status ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (selectedStatus == status),
                        onClick = { selectedStatus = status }
                    )
                    Text(text = status, modifier = Modifier.padding(start = 8.dp))
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Personel Notu / Arıza Açıklaması") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )


            if (selectedStatus == "Arızalı") {
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(null)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kamerayı Aç ve Fotoğraf Çek")
                }


                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    Text("Buluta yükleniyor, lütfen bekleyin...", modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (capturedImage != null) {
                    Image(
                        bitmap = capturedImage!!.asImageBitmap(),
                        contentDescription = "Arıza Fotoğrafı",
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

                    val logData = hashMapOf(
                        "deviceId" to deviceId,
                        "personnelEmail" to (currentUser?.email ?: "Bilinmiyor"),
                        "status" to selectedStatus,
                        "note" to note,
                        "photoUrl" to uploadedImageUrl,
                        "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )


                    db.collection("MaintenanceLogs")
                        .add(logData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Rapor başarıyla kaydedildi!", Toast.LENGTH_LONG).show()
                            onNavigateBack()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                },
                enabled = selectedStatus.isNotEmpty() && (!isUploading),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Süreci Tamamla ve Kaydet")
            }
        }
    }
}


suspend fun uploadToCloudinary(bitmap: Bitmap): String? {
    return withContext(Dispatchers.IO) {
        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            val byteArray = stream.toByteArray()

            val cloudName = "ddbitwu19"
            val uploadPreset = "bakim_preset"

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_preset", uploadPreset)
                .addFormDataPart(
                    "file", "ariza_foto.jpg",
                    byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    return@withContext jsonObject.getString("secure_url")
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}