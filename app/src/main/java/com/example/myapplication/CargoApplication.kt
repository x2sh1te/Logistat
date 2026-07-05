package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CargoApplication : Application() {
    // Используем SupervisorJob, чтобы ошибки в одной задаче не отменяли всё остальное
    val applicationScope = CoroutineScope(SupervisorJob())

    // Ленивая инициализация базы данных и репозитория
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { Repository(database.userDao(), database.clientDao(), database.orderDao()) }
}
