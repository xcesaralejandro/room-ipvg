package com.example.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.db.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userEntity: UserEntity)

    @Update
    suspend fun update(userEntity: UserEntity)

    @Delete
    suspend fun delete(userEntity: UserEntity)

    // Flow en Kotlin actúa como un observable que permanece activo y puede emitir cambios
    // en cualquier momento. Cuando hay un cambio en los datos (como una actualización desde una
    // API o un cambio en la base de datos), Flow notifica automáticamente a los observadores.
    // La reactividad permite que la UI se recomponga o actualice para reflejar estos cambios sin
    // que tengas que manejar manualmente cada actualización. Esto lo hace muy efectivo para
    // gestionar datos dinámicos en aplicaciones.

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
}