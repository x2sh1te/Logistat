package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object ProfileSelection : Screen("profile_selection")
    object MainList : Screen("main_list/{userId}") {
        fun createRoute(userId: Int) = "main_list/$userId"
    }
    object AddEditOrder : Screen("add_edit_order/{orderId}") {
        fun createRoute(orderId: Long = -1L) = "add_edit_order/$orderId"
    }
    object Stats : Screen("stats")
}
