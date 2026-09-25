package com.example.nole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

// NUEVA RAMA
// -------------------------
// TERCERA LINEA
@Preview
@Composable
fun AppAndroidPreview() {
    App()
}





// NUEVO CAMBIO
// NUEVO CAMBIO
// MERGE PABLO