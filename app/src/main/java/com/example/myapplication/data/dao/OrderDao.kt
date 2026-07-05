package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId AND date >= :minDate ORDER BY date DESC")
    fun getOrdersForUser(userId: Int, minDate: Long): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): Order?

    @Query("SELECT * FROM orders WHERE date BETWEEN :startDate AND :endDate")
    fun getOrdersForPeriod(startDate: Long, endDate: Long): Flow<List<Order>>
}
