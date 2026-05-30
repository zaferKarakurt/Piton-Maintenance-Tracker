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


                    val navController = rememberNavController()


                    NavHost(
                        navController = navController,
                        startDestination = "login_screen"
                    ) {

                        composable("login_screen") {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onNavigate = { route ->
                                    navController.navigate(route)
                                }
                            )
                        }


                        composable("admin_screen") {
                            AdminScreen(navController = navController)
                        }


                        composable("personnel_screen") {
                            PersonnelScreen(navController = navController)
                        }


                        composable("status_check_screen/{deviceId}") { backStackEntry ->

                            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
                            StatusCheckScreen(
                                deviceId = deviceId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}