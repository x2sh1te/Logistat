package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Repository
import com.example.myapplication.data.model.Client
import com.example.myapplication.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(
    private val repository: Repository,
    private val orderId: Long,
    private val userId: Int
) : ViewModel() {

    // Состояние формы
    val date = MutableStateFlow(System.currentTimeMillis())
    val clientName = MutableStateFlow("")
    val managerText = MutableStateFlow("")
    val routeText = MutableStateFlow("")
    val amount = MutableStateFlow("")
    val paymentType = MutableStateFlow("Наличные")
    val paymentStatus = MutableStateFlow("Не оплачено")

    // Список всех заказчиков для подсказок
    val allClients: StateFlow<List<Client>> = repository.allClients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        if (orderId != -1L) {
            viewModelScope.launch {
                repository.getOrderById(orderId)?.let { order ->
                    date.value = order.date
                    managerText.value = order.managerText
                    routeText.value = order.routeText
                    amount.value = order.amount.toString()
                    paymentType.value = order.paymentType
                    paymentStatus.value = order.paymentStatus
                    
                    // Находим имя клиента
                    allClients.value.find { it.id == order.clientId }?.let {
                        clientName.value = it.name
                    }
                }
            }
        }
    }

    fun saveOrder(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 1. Сначала разбираемся с клиентом
            var client = repository.getClientByName(clientName.value)
            if (client == null && clientName.value.isNotBlank()) {
                val newId = repository.insertClient(Client(name = clientName.value))
                client = Client(id = newId.toInt(), name = clientName.value)
            }

            val clientId = client?.id ?: 0
            val order = Order(
                id = if (orderId == -1L) 0 else orderId,
                userId = userId,
                date = date.value,
                clientId = clientId,
                managerText = managerText.value,
                routeText = routeText.value,
                amount = amount.value.toDoubleOrNull() ?: 0.0,
                paymentType = paymentType.value,
                paymentStatus = paymentStatus.value
            )

            if (orderId == -1L) {
                repository.insertOrder(order)
            } else {
                repository.updateOrder(order)
            }
            onSuccess()
        }
    }
}

class OrderViewModelFactory(
    private val repository: Repository,
    private val orderId: Long,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(repository, orderId, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
