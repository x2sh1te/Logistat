package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Repository
import com.example.myapplication.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: Repository,
    private val userId: Int
) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    init {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            _userName.value = user?.name ?: "Неизвестно"
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Порог 365 дней
    private val minDate = System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000)

    // Список заказов из базы для конкретного пользователя
    private val _orders = repository.getOrdersForUser(userId, minDate)

    // Объединяем список заказов и поисковый запрос для фильтрации
    val filteredOrders: StateFlow<List<Order>> = combine(_orders, _searchQuery) { orders, query ->
        if (query.isEmpty()) {
            orders
        } else {
            orders.filter { 
                it.routeText.contains(query, ignoreCase = true) || 
                it.managerText.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    suspend fun deleteOrder(order: Order) {
        repository.deleteOrder(order)
    }
}

class MainViewModelFactory(
    private val repository: Repository,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
