package com.zafer.maintenancetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zafer.maintenancetracker.ui.AdminScreen
import com.zafer.maintenancetracker.ui.LoginScreen
import com.zafer.maintenancetracker.ui.PersonnelScreen
import com.zafer.maintenancetracker.ui.StatusCheckScreen
import com.zafer.maintenancetracker.ui.theme.MaintenanceTrackerTheme
import com.zafer.maintenancetracker.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaintenanceTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val loginViewModel: LoginViewModel = viewModel()

                    // Trafik polisimizi oluşturuyoruz (Sayfa geçişleri için)
                    val navController = rememberNavController()

                    // Hangi ekranların olduğunu NavHost ile tanımlıyoruz
                    NavHost(
                        navController = navController,
                        startDestination = "login_screen" // Uygulama ilk bu ekrandan başlar
                    ) {
                        // 1. Giriş Ekranı
                        composable("login_screen") {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onNavigate = { route ->
                                    navController.navigate(route)
                                }
                            )
                        }

                        // 2. Admin Ekranı
                        composable("admin_screen") {
                            AdminScreen(navController = navController)
                        }

                        // 3. Personel Ekranı (navController'ı içeri gönderdik ki sayfa değiştirebilsin)
                        composable("personnel_screen") {
                            PersonnelScreen(navController = navController)
                        }

                        // 4. Durum Kontrol Ekranı (Yeni Ekranımız)
                        composable("status_check_screen/{deviceId}") { backStackEntry ->
                            // Tıklanan cihazın ID'sini yakalıyoruz
                            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
                            StatusCheckScreen(
                                deviceId = deviceId,
                                onNavigateBack = { navController.popBackStack() } // İşimizi bitirince geri dönme komutu
                            )
                        }
                    }
                }
            }
        }
    }
}