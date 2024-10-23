package com.example.db.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.db.dao.UserDao
import com.example.db.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserViewModel(private val userDao: UserDao) : ViewModel() {

    val users: Flow<List<UserEntity>> = userDao.getAllUsers()

    fun insertUser(user: UserEntity) {
        viewModelScope.launch {
            userDao.insert(user)
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            userDao.update(user)
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            userDao.delete(user)
        }
    }
}


// ¿Qué es UserViewModelFactory?
// UserViewModelFactory es una clase que implementa ViewModelProvider.Factory para crear instancias
// de UserViewModel, que requieren un UserDao como dependencia.

// ¿Por qué es necesario?
// Inyección de Dependencias: Permite pasar parámetros al constructor del ViewModel, que el sistema
// Android no puede proporcionar automáticamente.
// Encapsulamiento: Mantiene la lógica de creación del ViewModel separada y organizada.
// Control de Tipos: Asegura que se cree el ViewModel correcto, lanzando un error si se intenta crear uno incorrecto.

class UserViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
