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
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.sufibra.network.R
import com.sufibra.network.ui.navigation.Screen

@Composable
fun AdminNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val colorScheme = MaterialTheme.colorScheme
    val clientsModuleSelected = currentRoute == Screen.ClientsList.route ||
        currentRoute == Screen.CreateClient.route ||
        currentRoute == Screen.EditClient.route
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
        fun navigateTo(route: String) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
            }
        }

        NavigationBarItem(
            selected = currentRoute == Screen.AdminDashboard.route,
            onClick = {
                navigateTo(Screen.AdminDashboard.route)
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
            selected = currentRoute == Screen.EventsList.route,
            onClick = {
                navigateTo(Screen.EventsList.route)
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
            selected = currentRoute == Screen.UsersList.route,
            onClick = {
                navigateTo(Screen.UsersList.route)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_usuarios),
                    contentDescription = "Usuarios"
                )
            },
            label = { Text("Usuarios") },
            colors = itemColors
        )

        NavigationBarItem(
            selected = clientsModuleSelected,
            onClick = {
                navigateTo(Screen.ClientsList.route)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clientes),
                    contentDescription = "Clientes"
                )
            },
            label = { Text("Clientes") },
            colors = itemColors
        )

        NavigationBarItem(
            selected = profileModuleSelected,
            onClick = {
                navigateTo(Screen.Profile.route)
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
