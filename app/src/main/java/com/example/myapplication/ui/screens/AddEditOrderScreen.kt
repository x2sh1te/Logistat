package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditOrderScreen(
    viewModel: OrderViewModel,
    orderId: Long,
    onBack: () -> Unit
) {
    val date by viewModel.date.collectAsState()
    val clientName by viewModel.clientName.collectAsState()
    val managerText by viewModel.managerText.collectAsState()
    val routeText by viewModel.routeText.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val paymentType by viewModel.paymentType.collectAsState()
    val paymentStatus by viewModel.paymentStatus.collectAsState()

    val clipboardManager = LocalClipboardManager.current
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (orderId == -1L) "Новый заказ" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Дата
            OutlinedTextField(
                value = dateFormatter.format(Date(date)),
                onValueChange = {},
                label = { Text("Дата") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату")
                    }
                }
            )

            // Заказчик
            OutlinedTextField(
                value = clientName,
                onValueChange = { viewModel.clientName.value = it },
                label = { Text("Заказчик") },
                modifier = Modifier.fillMaxWidth()
            )

            // Менеджер
            OutlinedTextField(
                value = managerText,
                onValueChange = { viewModel.managerText.value = it },
                label = { Text("Менеджер") },
                modifier = Modifier.fillMaxWidth()
            )

            // Маршрут
            OutlinedTextField(
                value = routeText,
                onValueChange = { viewModel.routeText.value = it },
                label = { Text("Маршрут (Погрузка -> Выгрузка)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { clipboardManager.setText(AnnotatedString(routeText)) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Копировать")
                    }
                }
            )

            // Сумма
            OutlinedTextField(
                value = amount,
                onValueChange = { viewModel.amount.value = it },
                label = { Text("Сумма (₽)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Тип оплаты
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Тип оплаты: ", modifier = Modifier.weight(1f))
                FilterChip(
                    selected = paymentType == "Наличные",
                    onClick = { viewModel.paymentType.value = "Наличные" },
                    label = { Text("Наличные") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = paymentType == "Безнал",
                    onClick = { viewModel.paymentType.value = "Безнал" },
                    label = { Text("Безнал") }
                )
            }

            // Статус оплаты
            var expanded by remember { mutableStateOf(false) }
            val statuses = listOf("Оплачено", "Не оплачено", "Частично")
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = paymentStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Статус оплаты") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    statuses.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                viewModel.paymentStatus.value = status
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.saveOrder(onBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Сохранить", fontSize = 18.sp)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.date.value = datePickerState.selectedDateMillis ?: date
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
