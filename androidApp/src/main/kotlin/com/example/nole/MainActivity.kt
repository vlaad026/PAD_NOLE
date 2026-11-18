package com.example.nole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.nole.di.AppContainer
import com.example.nole.di.DefaultAppContainer

class MainActivity : ComponentActivity() {
    private val appContainer: AppContainer = DefaultAppContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(appContainer)
        }
    }
}

// PADEOOOOOOO chavales

//Primer commit en login

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

// MERGE 2