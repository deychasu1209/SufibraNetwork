package com.sufibra.network.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sufibra.network.ui.theme.AzulPrincipalOscuro
import com.sufibra.network.ui.theme.DarkBackground
import com.sufibra.network.ui.theme.Turquesa
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val userRepository = remember { UserRepository() }
    val colorScheme = MaterialTheme.colorScheme
    val gradientColors = if (colorScheme.background == DarkBackground) {
        listOf(AzulPrincipalOscuro, AzulPrincipal)
    } else {
        listOf(AzulPrincipal, Turquesa)
    }

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
                    colors = gradientColors
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
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
        }
    }
}


