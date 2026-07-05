package com.example.myapplication.data

import com.example.myapplication.data.dao.ClientDao
import com.example.myapplication.data.dao.OrderDao
import com.example.myapplication.data.dao.UserDao
import com.example.myapplication.data.model.Client
import com.example.myapplication.data.model.Order
import com.example.myapplication.data.model.User
import kotlinx.coroutines.flow.Flow

class Repository(
    private val userDao: UserDao,
    private val clientDao: ClientDao,
    private val orderDao: OrderDao
) {
    // Users
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    suspend fun getUserById(id: Int) = userDao.getUserById(id)

    // Clients
    val allClients: Flow<List<Client>> = clientDao.getAllClients()
    suspend fun insertClient(client: Client) = clientDao.insertClient(client)
    suspend fun getClientByName(name: String) = clientDao.getClientByName(name)

    // Orders
    fun getOrdersForUser(userId: Int, minDate: Long): Flow<List<Order>> = 
        orderDao.getOrdersForUser(userId, minDate)
    
    suspend fun insertOrder(order: Order) = orderDao.insertOrder(order)
    suspend fun updateOrder(order: Order) = orderDao.updateOrder(order)
    suspend fun deleteOrder(order: Order) = orderDao.deleteOrder(order)
    suspend fun getOrderById(id: Long) = orderDao.getOrderById(id)
    
    fun getOrdersForPeriod(start: Long, end: Long) = orderDao.getOrdersForPeriod(start, end)
}
