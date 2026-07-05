package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.Order
import com.example.myapplication.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainListScreen(
    viewModel: MainViewModel,
    userName: String,
    onAddOrder: () -> Unit,
    onChangeProfile: () -> Unit,
    onEditOrder: (Long) -> Unit,
    onShowStats: () -> Unit
) {
    val orders by viewModel.filteredOrders.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Водитель: $userName") },
                actions = {
                    IconButton(onClick = onShowStats) {
                        Icon(Icons.Default.BarChart, contentDescription = "Статистика")
                    }
                    IconButton(onClick = onChangeProfile) {
                        Icon(Icons.Default.Logout, contentDescription = "Сменить профиль")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddOrder) {
                Icon(Icons.Default.Add, contentDescription = "Добавить заказ")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Поле поиска
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Поиск по заказчику или маршруту") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Заказов пока нет", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Чтобы FAB не перекрывал
                ) {
                    items(orders, key = { it.id }) { order ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    scope.launch { viewModel.deleteOrder(order) }
                                    true
                                } else if (value == SwipeToDismissBoxValue.StartToEnd) {
                                    onEditOrder(order.id)
                                    false
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> Color.Blue
                                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                                    else -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .background(color, MaterialTheme.shapes.medium),
                                    contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                                ) {
                                    Text(
                                        if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) "Редактировать" else "Удалить",
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        ) {
                            OrderCard(
                                order = order,
                                onClick = { onEditOrder(order.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val dateString = dateFormatter.format(Date(order.date))

    val statusColor = when (order.paymentStatus) {
        "Оплачено" -> Color(0xFF4CAF50) // Зеленый
        "Частично" -> Color(0xFFFF9800) // Оранжевый
        else -> Color(0xFFF44336) // Красный
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Индикатор статуса
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(statusColor, shape = MaterialTheme.shapes.small)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.routeText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1
                )
                Text(
                    text = "Менеджер: ${order.managerText}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${order.amount} ₽",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = dateString,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
