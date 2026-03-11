package com.sufibra.network.ui.components.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.sufibra.network.R
import com.sufibra.network.ui.navigation.Screen
import com.sufibra.network.ui.theme.AzulPrincipal

@Composable
fun AdminNavigationBar(navController: NavController) {

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = AzulPrincipal.copy(alpha = 0.10f)
    ) {

        NavigationBarItem(
            selected = currentRoute == Screen.AdminDashboard.route,
            onClick = {
                navController.navigate(Screen.AdminDashboard.route)
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
            selected = currentRoute == Screen.EventsList.route,
            onClick = {
                navController.navigate(Screen.EventsList.route)
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
            selected = currentRoute == Screen.UsersList.route,
            onClick = {
                navController.navigate(Screen.UsersList.route)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_usuarios),
                    contentDescription = "Usuarios"
                )
            },
            label = { Text("Usuarios") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrincipal,
                selectedTextColor = AzulPrincipal,
                indicatorColor = AzulPrincipal.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = false,// cambiar aqui
            // selected = currentRoute == Screen.AdminClients.route, // cambiar nombre
            onClick = {},
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clientes),
                    contentDescription = "Clientes"
                )
            },
            label = { Text("Clientes") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AzulPrincipal,
                selectedTextColor = AzulPrincipal,
                indicatorColor = AzulPrincipal.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = false,// cambiar aqui
            // selected = currentRoute == Screen.AdminProfile.route, // cambiar nombre
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