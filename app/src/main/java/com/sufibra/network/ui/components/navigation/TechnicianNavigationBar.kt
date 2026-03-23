package com.sufibra.network.ui.components.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.ui.navigation.Screen

@Composable
fun TechnicianNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val colorScheme = MaterialTheme.colorScheme
    val profileModuleSelected = currentRoute == Screen.Profile.route ||
        currentRoute == Screen.EditProfile.route ||
        currentRoute == Screen.ChangePassword.route
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = colorScheme.primary,
        selectedTextColor = colorScheme.primary,
        unselectedIconColor = colorScheme.onSurfaceVariant,
        unselectedTextColor = colorScheme.onSurfaceVariant,
        indicatorColor = colorScheme.secondaryContainer
    )

    NavigationBar(
        containerColor = colorScheme.surface,
        contentColor = colorScheme.onSurfaceVariant
    ) {

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
            colors = itemColors
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
            colors = itemColors
        )

        NavigationBarItem(
            selected = currentRoute == Screen.TechnicianMyJobs.route,
            onClick = {
                navController.navigate(Screen.TechnicianMyJobs.route)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trabajos),
                    contentDescription = "Mis trabajos"
                )
            },
            label = { Text("Mis trabajos") },
            colors = itemColors
        )

        NavigationBarItem(
            selected = profileModuleSelected,
            onClick = {
                navController.navigate(Screen.Profile.route)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_perfil),
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Perfil") },
            colors = itemColors
        )
    }
}
