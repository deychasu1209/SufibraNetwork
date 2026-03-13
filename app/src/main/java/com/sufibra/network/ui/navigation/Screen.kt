package com.sufibra.network.ui.navigation

sealed class Screen(val route: String) {

    object Splash : Screen("splash")
    object Login : Screen("login")
    object AdminDashboard : Screen("admin_dashboard")
    object TechnicianDashboard : Screen("technician_dashboard")

    object CreateUser : Screen("create_user")

    object UsersList : Screen("users_list")

    object EditUser : Screen("edit_user/{userId}") {
        fun createRoute(userId: String) = "edit_user/$userId"
    }

    object EventsList : Screen("events_list")
    object CreateInstallation : Screen("create_installation")
    object CreateAveria : Screen("create_averia")

    object TechnicianAvailableEvents : Screen("technician_available_events")

    object TechnicianEventDetail : Screen("technician_event_detail/{eventId}") {
        fun createRoute(eventId: String) = "technician_event_detail/$eventId"
    }

    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }

    object TechnicianCurrentJob : Screen("technician_current_job")
    object TechnicianMyJobs : Screen("technician_my_jobs")

    object FinalizeEvent : Screen("finalize_event/{eventId}") {
        fun createRoute(eventId: String) = "finalize_event/$eventId"
    }
}
