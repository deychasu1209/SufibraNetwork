package com.sufibra.network.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sufibra.network.R
import com.sufibra.network.data.repository.UserRepository
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.theme.AzulPrincipal
import com.sufibra.network.ui.theme.Turquesa
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val userRepository = remember { UserRepository() }

    LaunchedEffect(Unit) {

        delay(10)

        val currentUser = auth.currentUser

        if (currentUser == null) {

            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }

        } else {

            val result = userRepository.getUserByUid(currentUser.uid)

            result.onSuccess { user ->

                if (user.estado) {

                    if (user.rol == "ADMIN") {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else if (user.rol == "TECHNICIAN") {
                        navController.navigate(Screen.TechnicianDashboard.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }

                } else {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            result.onFailure {
                auth.signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AzulPrincipal, Turquesa)
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo_blanco),
                contentDescription = "Logo Sufibra",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sufibra Network",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "INTERNET, HOGAR Y NEGOCIO",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}