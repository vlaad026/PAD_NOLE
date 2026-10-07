import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(onNavigateToLogin: () -> Unit) {

    val rosaPrincipal = Color(0xFFE91E63)
    val rosaFondo = Color(0xFFFCE4EC)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = rosaFondo)
            .safeContentPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    println("Botón de registro/login pulsado")
                    onNavigateToLogin()
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = rosaPrincipal
                ),
                border = BorderStroke(1.5.dp, rosaPrincipal)
            ) {

                Text(
                    text = "Regístrate o inicia sesión",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
        }
    }
}