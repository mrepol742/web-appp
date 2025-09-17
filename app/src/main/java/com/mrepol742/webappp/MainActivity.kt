package com.mrepol742.webappp

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.mrepol742.webappp.client.SecureChromeClient
import com.mrepol742.webappp.ui.theme.WebApppTheme
import com.mrepol742.webappp.utils.DynamicShortcut


class MainActivity : ComponentActivity() {
    private val allowedDomain = "www.melvinjonesrepol.com"
    private var currentUrl: String = "https://$allowedDomain"
    private val shortcuts = listOf(
        "/projects" to "My Projects",
        "/gaming" to "The Games I Played",
        "https://go.melvinjonesrepol.com" to "ShortLink",
        "https://ai.melvinjonesrepol.com" to "Melvin AI",
        "/contact-me" to "Contact Me",
    )
    private val webViewState = mutableStateOf<WebView?>(null)
    private val secureWebChromeClientState = mutableStateOf<SecureChromeClient?>(null)

    private val fileChooserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            secureWebChromeClientState.value?.handleFileChosen(result.resultCode, result.data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WebApppTheme {
                Scaffold(modifier = Modifier.fillMaxSize().systemBarsPadding()) { innerPadding ->
                    WebViewScreen(
                        allowedDomain = allowedDomain,
                        initialUrl = currentUrl,
                        webViewState = webViewState,
                        secureWebChromeClientState = secureWebChromeClientState,
                        fileChooserLauncher= fileChooserLauncher,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        DynamicShortcut(this, allowedDomain, shortcuts).createDynamicShortcut()
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