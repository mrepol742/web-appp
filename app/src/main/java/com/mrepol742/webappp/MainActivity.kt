package com.mrepol742.webappp

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.mrepol742.webappp.ui.theme.WebApppTheme
import com.mrepol742.webappp.utils.DynamicShortcut


class MainActivity : ComponentActivity() {
    private val allowedDomain = "www.melvinjonesrepol.com"
    private var currentUrl: String = "https://$allowedDomain"
    private val webViewState = mutableStateOf<WebView?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WebApppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewScreen(
                        allowedDomain = allowedDomain,
                        initialUrl = currentUrl,
                        webViewState = webViewState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        DynamicShortcut(this, allowedDomain).createDynamicShortcut()
    }

    override fun onResume() {
        super.onResume()

        val urlFromIntent = when (intent?.action) {
            Intent.ACTION_VIEW -> intent.getStringExtra("url")
            else -> null
        }

        if (!urlFromIntent.isNullOrEmpty()) {
            currentUrl = urlFromIntent
            webViewState.value?.loadUrl(currentUrl)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}