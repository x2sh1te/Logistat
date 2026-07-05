package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.screens.AddEditOrderScreen
import com.example.myapplication.ui.screens.MainListScreen
import com.example.myapplication.ui.screens.ProfileSelectionScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.MainViewModel
import com.example.myapplication.viewmodel.MainViewModelFactory
import com.example.myapplication.viewmodel.OrderViewModel
import com.example.myapplication.viewmodel.OrderViewModelFactory
import com.example.myapplication.viewmodel.ProfileViewModel
import com.example.myapplication.viewmodel.ProfileViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Получаем доступ к нашему приложению и репозиторию
        val app = application as CargoApplication
        val repository = app.repository
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        repository = repository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    repository: com.example.myapplication.data.Repository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(repository)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.ProfileSelection.route,
        modifier = modifier
    ) {
        composable(Screen.ProfileSelection.route) {
            ProfileSelectionScreen(
                viewModel = profileViewModel,
                onProfileSelected = { userId ->
                    // Когда профиль выбран, идем на главный экран
                    navController.navigate(Screen.MainList.createRoute(userId)) {
                        popUpTo(Screen.ProfileSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.MainList.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: -1
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(repository, userId)
            )
            val userName by mainViewModel.userName.collectAsState()

            MainListScreen(
                viewModel = mainViewModel,
                userName = userName,
                onAddOrder = {
                    navController.navigate(Screen.AddEditOrder.createRoute())
                },
                onChangeProfile = {
                    navController.navigate(Screen.ProfileSelection.route) {
                        popUpTo(Screen.MainList.route) { inclusive = true }
                    }
                },
                onEditOrder = { orderId ->
                    navController.navigate(Screen.AddEditOrder.createRoute(orderId))
                }
            )
        }

        composable(Screen.AddEditOrder.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")?.toLong() ?: -1L
            // Нам нужен userId водителя. Можем взять его из profileViewModel или передать в аргументах.
            // Для простоты передадим через аргумент в будущем или возьмем из текущего состояния.
            val userId = profileViewModel.currentUserId 

            val orderViewModel: OrderViewModel = viewModel(
                factory = OrderViewModelFactory(repository, orderId, userId)
            )

            AddEditOrderScreen(
                viewModel = orderViewModel,
                orderId = orderId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
