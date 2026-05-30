package com.zafer.maintenancetracker.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Giriş
    fun loginUser(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isEmpty() || pass.isEmpty()) {
            onResult(false, "Lütfen e-posta ve şifre girin.")
            return
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    checkUserRole(userId, onResult)
                }
            }
            .addOnFailureListener {
                onResult(false, "Giriş başarısız: ${it.localizedMessage}")
            }
    }

    private fun checkUserRole(uid: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role")
                    onResult(true, role)
                } else {
                    onResult(false, "Kullanıcı rolü bulunamadı.")
                }
            }
            .addOnFailureListener {
                onResult(false, "Rol kontrolü başarısız oldu.")
            }
    }
}