package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Client
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<Client>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertClient(client: Client): Long

    @Query("SELECT * FROM clients WHERE name = :name")
    suspend fun getClientByName(name: String): Client?
}
