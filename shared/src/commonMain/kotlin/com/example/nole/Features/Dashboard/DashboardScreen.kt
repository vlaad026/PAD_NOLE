package com.example.nole.Features.Dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource

// Importa los recursos de tu proyecto KMP
import nole.shared.generated.resources.Res
import nole.shared.generated.resources.*

val NolePink = Color(0xFFFF1493)

@Composable
fun DashboardScreen(onNavigateToLogin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeContentPadding()
    ) {
        // --- BARRA SUPERIOR CON BOTÓN DE LOGIN/REGISTRO ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onNavigateToLogin,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NolePink
                ),
                border = BorderStroke(1.5.dp, NolePink)
            ) {
                Text(
                    text = "Regístrate o inicia sesión",
                    style = MaterialTheme.typography.labelLarge,
                    color = NolePink,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- CONTENIDO DEL DASHBOARD ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.nole_negro),
                    contentDescription = "Logo NOLE",
                    modifier = Modifier.size(180.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bienvenido a NOLE",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}