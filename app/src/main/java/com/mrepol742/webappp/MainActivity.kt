package com.mrepol742.webappp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.mrepol742.webappp.ui.theme.WebApppTheme


class MainActivity : ComponentActivity() {
    private val allowedDomain = "www.melvinjonesrepol.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebApppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewScreen(
                        allowedDomain = allowedDomain,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}