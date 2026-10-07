package com.example.nole.Features.Login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource

// Importa los recursos de tu proyecto KMP
import nole.shared.generated.resources.Res
import nole.shared.generated.resources.*

val NolePink = Color(0xFFFF1493)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = LoginViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isSuccess) {
        onLoginSuccess()
    }

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NolePink,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = NolePink,
        unfocusedLabelColor = Color.LightGray,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = NolePink
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- AQUÍ VA EL LOGO ---
        // Cambia 'compose_multiplatform' o el nombre que tenga la imagen de tu logo dentro de 'commonMain/composeResources/drawable'
        // --- LOGO DE LA APP ---
        Image(
            painter = painterResource(Res.drawable.logo_nole), // Nombre de tu archivo PNG
            contentDescription = "Logo NOLE",
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = if (state.isRegisterMode) "Crear una cuenta" else "Iniciar Sesión",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = { Text("Correo Electrónico") },
            singleLine = true,
            colors = customTextFieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = { Text("Contraseña") },
            singleLine = true,
            colors = customTextFieldColors,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        if (state.isRegisterMode) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                label = { Text("Confirmar Contraseña") },
                singleLine = true,
                colors = customTextFieldColors,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
        }

        state.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onSubmitClick() },
            colors = ButtonDefaults.buttonColors(containerColor = NolePink, contentColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(if (state.isRegisterMode) "Registrarse" else "Entrar", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { viewModel.onToggleMode() }) {
            Text(
                text = if (state.isRegisterMode) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate aquí",
                color = Color.LightGray
            )
        }
    }
}