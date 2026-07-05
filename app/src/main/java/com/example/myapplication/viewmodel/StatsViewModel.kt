package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Repository
import com.example.myapplication.data.model.Order
import com.example.myapplication.data.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StatsSummary(
    val totalRevenue: Double = 0.0,
    val cashRevenue: Double = 0.0,
    val nonCashRevenue: Double = 0.0,
    val paidAmount: Double = 0.0,
    val debtAmount: Double = 0.0
)

class StatsViewModel(private val repository: Repository) : ViewModel() {

    val startDate = MutableStateFlow(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)) // Месяц назад
    val endDate = MutableStateFlow(System.currentTimeMillis())
    
    // -1 означает "Общий"
    val selectedUserId = MutableStateFlow(-1)

    val allUsers: StateFlow<List<User>> = repository.allUsers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Поток заказов для выбранного периода
    private val _orders = combine(startDate, endDate) { start, end ->
        repository.getOrdersForPeriod(start, end)
    }.flatMapLatest { it }

    // Итоговая статистика
    val statsSummary: StateFlow<StatsSummary> = combine(_orders, selectedUserId) { orders, userId ->
        val filtered = if (userId == -1) orders else orders.filter { it.userId == userId }
        
        var total = 0.0
        var cash = 0.0
        var nonCash = 0.0
        var paid = 0.0
        var debt = 0.0

        filtered.forEach { order ->
            total += order.amount
            if (order.paymentType == "Наличные") cash += order.amount else nonCash += order.amount
            if (order.paymentStatus == "Оплачено") paid += order.amount else if (order.paymentStatus == "Не оплачено") debt += order.amount
            // Для "Частично" логика может быть сложнее, но пока упростим
        }

        StatsSummary(total, cash, nonCash, paid, debt)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsSummary())

    fun exportToCsv(onFileReady: (String) -> Unit) {
        viewModelScope.launch {
            // Здесь будет логика генерации CSV
            // Для MVP просто заглушка, которую мы наполним в следующем шаге
        }
    }
}

class StatsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
