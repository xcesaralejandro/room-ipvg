package com.example.db

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.db.entities.UserEntity
import com.example.db.ui.theme.DbTheme
import com.example.db.viewModel.UserViewModel
import com.example.db.viewModel.UserViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Obtener el UserDao desde la base de datos
        val userDao = AppDatabase.getDatabase(this).userDao()
        // Crear una instancia del ViewModel desde un factory
        val factory = UserViewModelFactory(userDao)
        val viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        setContent {
            DbTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)){
                        UserScreen(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun UserScreen(viewModel: UserViewModel) {
    // Obtiene la lista de usuarios y con collectAsState convierte el flujo (Flow)
    // en un estado observable por jetpack compose. Esta combinación permite que la
    // interfaz grafica se actualice automaticamente cuando cambien los datos que está
    // manejando Flow.
    val users by viewModel.users.collectAsState(initial = emptyList())
    var name by remember { mutableStateOf("") }
    // Utilizado como variable temporal
    // para saber el usuario seleccionado
    var selectedUser by remember { mutableStateOf<UserEntity?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (name.isNotBlank()) {
                    if (selectedUser != null) {
                        // Si hay un usuario seleccionado lo actualizamos. (!!.) esa aserción
                        // asegura que el objeto no es null y lo copiamos reemplazando propiedades
                        // para crear una nueva instancia del objeto con propiedades modificadas
                        viewModel.updateUser(selectedUser!!.copy(name = name))
                        selectedUser = null // Restablecer el usuario seleccionado
                    } else {
                        // Si no hay un usuario seleccionado, lo insertamos
                        viewModel.insertUser(UserEntity(name = name))
                    }
                    name = "" // Limpiamos el campo
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (selectedUser != null) "Actualizar" else "Agregar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(users) { user ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${user.id} - ${user.name}")
                    Row {
                        Button(onClick = {
                            // Al hacer clic en editar, establecemos el usuario seleccionado y llenamos el campo de texto
                            name = user.name
                            selectedUser = user
                        }) {
                            Text("Editar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            // Al hacer clic en eliminar, eliminamos al usuario XD
                            viewModel.deleteUser(user)
                        }) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
}
