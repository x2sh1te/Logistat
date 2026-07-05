package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.ProfileViewModel

@Composable
fun ProfileSelectionScreen(
    viewModel: ProfileViewModel,
    onProfileSelected: (Int) -> Unit
) {
    val users by viewModel.allUsers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Кто за рулем?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        users.forEach { user ->
            Button(
                onClick = { 
                    viewModel.selectUser(user.id)
                    onProfileSelected(user.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Крупная кнопка по ТЗ
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = user.name,
                    fontSize = 20.sp
                )
            }
        }

        if (users.isEmpty()) {
            CircularProgressIndicator()
            Text("Загрузка профилей...")
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ProfileSelectionScreenPreview() {
    com.example.myapplication.ui.theme.MyApplicationTheme {
        ProfileSelectionScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return com.example.myapplication.viewmodel.ProfileViewModel(
                            com.example.myapplication.data.Repository(
                                object : com.example.myapplication.data.dao.UserDao {
                                    override fun getAllUsers() = kotlinx.coroutines.flow.flowOf(
                                        listOf(
                                            com.example.myapplication.data.model.User(1, "Иван"),
                                            com.example.myapplication.data.model.User(2, "Алексей")
                                        )
                                    )
                                    override suspend fun insertUser(user: com.example.myapplication.data.model.User) {}
                                    override suspend fun getUserById(id: Int): com.example.myapplication.data.model.User? = null
                                },
                                object : com.example.myapplication.data.dao.ClientDao {
                                    override fun getAllClients() = kotlinx.coroutines.flow.flowOf<List<com.example.myapplication.data.model.Client>>(emptyList())
                                    override suspend fun insertClient(client: com.example.myapplication.data.model.Client) = 0L
                                    override suspend fun getClientByName(name: String): com.example.myapplication.data.model.Client? = null
                                },
                                object : com.example.myapplication.data.dao.OrderDao {
                                    override fun getOrdersForUser(userId: Int, minDate: Long) = kotlinx.coroutines.flow.flowOf<List<com.example.myapplication.data.model.Order>>(emptyList())
                                    override suspend fun insertOrder(order: com.example.myapplication.data.model.Order) {}
                                    override suspend fun updateOrder(order: com.example.myapplication.data.model.Order) {}
                                    override suspend fun deleteOrder(order: com.example.myapplication.data.model.Order) {}
                                    override suspend fun getOrderById(id: Long): com.example.myapplication.data.model.Order? = null
                                    override fun getOrdersForPeriod(startDate: Long, endDate: Long) = kotlinx.coroutines.flow.flowOf<List<com.example.myapplication.data.model.Order>>(emptyList())
                                }
                            )
                        ) as T
                    }
                }
            ),
            onProfileSelected = {}
        )
    }
}
