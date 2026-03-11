package com.sufibra.network.ui.components.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.theme.AzulPrincipal

@Composable
fun TechnicianNavigationBar(navController: NavController) {

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = AzulPrincipal.copy(alpha = 0.10f)
    )  {

        NavigationBarItem(
            selected = currentRoute == Screen.TechnicianDashboard.route,
            onClick = {
                navController.navigate(Screen.TechnicianDashboard.route)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_inicio),
                    contentDescription = "Inicio"
                )
            },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrincipal,
                selectedTextColor = AzulPrincipal,
                indicatorColor = AzulPrincipal.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == Screen.TechnicianAvailableEvents.route,
            onClick = {
                navController.navigate(Screen.TechnicianAvailableEvents.route)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_eventos),
                    contentDescription = "Eventos"
                )
            },
            label = { Text("Eventos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrincipal,
                selectedTextColor = AzulPrincipal,
                indicatorColor = AzulPrincipal.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = false,// cambiar aqui
            // selected = currentRoute == Screen.TechnicianJobs.route, // cambiar
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trabajos),
                    contentDescription = "Mis trabajos"
                )
            },
            label = { Text("Mis trabajos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrincipal,
                selectedTextColor = AzulPrincipal,
                indicatorColor = AzulPrincipal.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = false,// cambiar aqui
            // selected = currentRoute == Screen.TechnicianProfile.route, // cambiar
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_perfil),
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrincipal,
                selectedTextColor = AzulPrincipal,
                indicatorColor = AzulPrincipal.copy(alpha = 0.15f)
            )
        )
    }
}