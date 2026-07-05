package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.StatsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onBack: () -> Unit
) {
    val users by viewModel.allUsers.collectAsState()
    val selectedUserId by viewModel.selectedUserId.collectAsState()
    val stats by viewModel.statsSummary.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.exportToCsv { } }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Экспорт в CSV")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Выбор периода
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("С: ${dateFormatter.format(Date(startDate))}", modifier = Modifier.weight(1f))
                Text("По: ${dateFormatter.format(Date(endDate))}", modifier = Modifier.weight(1f))
            }

            // Вкладки выбора водителя
            val tabs = listOf("Общий") + users.map { it.name }
            val tabIds = listOf(-1) + users.map { it.id }
            val selectedTabIndex = tabIds.indexOf(selectedUserId)

            ScrollableTabRow(
                selectedTabIndex = if (selectedTabIndex == -1) 0 else selectedTabIndex,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { viewModel.selectedUserId.value = tabIds[index] },
                        text = { Text(title) }
                    )
                }
            }

            // Карточки со статистикой
            StatCard("Общая выручка", "${stats.totalRevenue} ₽", MaterialTheme.colorScheme.primaryContainer)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Наличные", "${stats.cashRevenue} ₽", Color(0xFFE3F2FD), modifier = Modifier.weight(1f))
                StatCard("Безнал", "${stats.nonCashRevenue} ₽", Color(0xFFF3E5F5), modifier = Modifier.weight(1f))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Оплачено", "${stats.paidAmount} ₽", Color(0xFFE8F5E9), modifier = Modifier.weight(1f))
                StatCard("Долги", "${stats.debtAmount} ₽", Color(0xFFFFEBEE), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.exportToCsv { } },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Экспорт в Excel (CSV)")
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = title, fontSize = 14.sp, color = Color.DarkGray)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
