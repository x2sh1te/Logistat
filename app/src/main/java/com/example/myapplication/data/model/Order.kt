package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Int,
    val date: Long, // Храним дату как Long (Unix timestamp)
    val clientId: Int,
    val managerText: String,
    val routeText: String,
    val amount: Double,
    val paymentType: String, // "Наличные" / "Безнал"
    val paymentStatus: String // "Оплачено" / "Не оплачено" / "Частично"
)
