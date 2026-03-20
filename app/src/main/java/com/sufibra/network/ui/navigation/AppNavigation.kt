package com.sufibra.network.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sufibra.network.ui.screens.dashboard.AdminDashboardScreen
import com.sufibra.network.ui.screens.dashboard.TechnicianDashboardScreen
import com.sufibra.network.ui.screens.clients.ClientsListScreen
import com.sufibra.network.ui.screens.clients.CreateClientScreen
import com.sufibra.network.ui.screens.clients.EditClientScreen
import com.sufibra.network.ui.screens.events.CreateAveriaScreen
import com.sufibra.network.ui.screens.events.CreateInstallationScreen
import com.sufibra.network.ui.screens.events.EditEventScreen
import com.sufibra.network.ui.screens.events.EventDetailScreen
import com.sufibra.network.ui.screens.events.EventsListScreen
import com.sufibra.network.ui.screens.events.FinalizeEventScreen
import com.sufibra.network.ui.screens.events.TechnicianAvailableEventsScreen
import com.sufibra.network.ui.screens.events.TechnicianCurrentJobScreen
import com.sufibra.network.ui.screens.events.TechnicianEventDetailScreen
import com.sufibra.network.ui.screens.events.TechnicianMyJobsScreen
import com.sufibra.network.ui.screens.login.LoginScreen
import com.sufibra.network.ui.screens.splash.SplashScreen
import com.sufibra.network.ui.screens.users.CreateUserScreen
import com.sufibra.network.ui.screens.users.EditUserScreen
import com.sufibra.network.ui.screens.users.UsersListScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(navController)
        }

        composable(Screen.TechnicianDashboard.route) {
            TechnicianDashboardScreen(navController)
        }

        composable(Screen.CreateUser.route) {
            CreateUserScreen(navController)
        }

        composable(Screen.UsersList.route) {
            UsersListScreen(navController)
        }

        composable(Screen.ClientsList.route) {
            ClientsListScreen(navController)
        }

        composable(Screen.CreateClient.route) {
            CreateClientScreen(navController)
        }

        composable(
            route = Screen.EditUser.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getString("userId") ?: ""

            EditUserScreen(
                navController = navController,
                userId = userId
            )
        }

        composable(
            route = Screen.EditClient.route,
            arguments = listOf(navArgument("clientId") { type = NavType.StringType })
        ) { backStackEntry ->

            val clientId = backStackEntry.arguments?.getString("clientId") ?: ""

            EditClientScreen(
                navController = navController,
                clientId = clientId
            )
        }

        composable(Screen.EventsList.route) {
            EventsListScreen(navController)
        }

        composable(Screen.CreateAveria.route) {
            CreateAveriaScreen(navController)
        }

        composable(Screen.CreateInstallation.route) {
            CreateInstallationScreen(navController)
        }

        composable(Screen.TechnicianAvailableEvents.route) {
            TechnicianAvailableEventsScreen(navController)
        }

        composable(
            route = Screen.TechnicianEventDetail.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""

            TechnicianEventDetailScreen(
                navController = navController,
                eventId = eventId
            )
        }

        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""

            EventDetailScreen(
                navController = navController,
                eventId = eventId
            )
        }

        composable(
            route = Screen.EditEvent.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""

            EditEventScreen(
                navController = navController,
                eventId = eventId
            )
        }

        composable(Screen.TechnicianCurrentJob.route) {
            TechnicianCurrentJobScreen(navController)
        }

        composable(Screen.TechnicianMyJobs.route) {
            TechnicianMyJobsScreen(navController)
        }

        composable(
            route = Screen.FinalizeEvent.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""

            FinalizeEventScreen(
                navController = navController,
                eventId = eventId
            )
        }
    }
}
