package com.example.loginsigo.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginsigo.data.model.UserResponse
import com.example.loginsigo.ui.theme.UtmGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserResponse,
    navController: NavController
) {
    // Estados locales para los campos del formulario (Simulados por ahora)
    var nombre by remember { mutableStateOf(user.personFullName) }
    var primerApellido by remember { mutableStateOf("") }
    var segundoApellido by remember { mutableStateOf("") }
    var curp by remember { mutableStateOf("") }
    var nss by remember { mutableStateOf("") }
    var sexoSelection by remember { mutableStateOf("Hombre") } // "Hombre" o "Mujer"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = user.username.ifBlank { "Perfil" },
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Acción perfil */ }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Perfil", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UtmGreenPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Habilita el scroll vertical
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = user.personFullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }


            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = user.active,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(checkedColor = Color.Black)
                )
                Text(text = "Perfil activo", fontWeight = FontWeight.Medium)
            }

            Divider(color = Color.LightGray, thickness = 1.dp)


            ReadOnlyRow(label = "Perfil", value = user.profileName)
            ReadOnlyRow(label = "Usuario", value = user.username)
            ReadOnlyRow(label = "Contraseña", value = "CAMBIAR", isLink = true)

            Spacer(modifier = Modifier.height(8.dp))


            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información Personal",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            ProfileTextField(label = "Nombre", value = nombre, onValueChange = { nombre = it })
            ProfileTextField(label = "Primer apellido", value = primerApellido, onValueChange = { primerApellido = it })
            ProfileTextField(label = "Segundo apellido", value = segundoApellido, onValueChange = { segundoApellido = it })


            Text(text = "Fecha de nacimiento", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Día
                OutlinedTextField(
                    value = "", onValueChange = {},
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    colors = inputColors()
                )

                Box(modifier = Modifier.weight(2f)) {
                    OutlinedTextField(
                        value = "Marzo", onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                        colors = inputColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedTextField(
                    value = "", onValueChange = {},
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    colors = inputColors()
                )
            }

            Text(text = "Sexo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GenderButton(
                    text = "Hombre",
                    isSelected = sexoSelection == "Hombre",
                    onClick = { sexoSelection = "Hombre" }
                )
                GenderButton(
                    text = "Mujer",
                    isSelected = sexoSelection == "Mujer",
                    onClick = { sexoSelection = "Mujer" }
                )
            }

            Text(text = "Estado de nacimiento", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            OutlinedTextField(
                value = "Michoacán de Ocampo",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                colors = inputColors(),
                modifier = Modifier.fillMaxWidth()
            )

            ProfileTextField(label = "CURP", value = curp, onValueChange = { curp = it })
            ProfileTextField(label = "Numero de Seguridad Social", value = nss, onValueChange = { nss = it })

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = inputColors(),
            shape = RoundedCornerShape(4.dp)
        )
    }
}

@Composable
fun ReadOnlyRow(label: String, value: String, isLink: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, color = Color.Gray)
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            color = if (isLink) Color.Gray else Color.Black // "CAMBIAR" en gris oscuro
        )
    }
}

@Composable
fun GenderButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) UtmGreenPrimary else Color(0xFF4DB6AC).copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(text = text, color = Color.White)
    }
}

@Composable
fun inputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.LightGray,
    unfocusedBorderColor = Color.LightGray
)