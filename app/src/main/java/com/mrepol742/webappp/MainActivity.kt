package com.mrepol742.webappp

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebView
import android.widget.Toast
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
    // private val allowedDomain = "melvinjonesrepol.com"

    // Testing for GEO Location
    // private val allowedDomain = "browserleaks.com/geo"

    // Testing for Camera
    // private val allowedDomain = "webrtc.github.io/samples/src/content/getusermedia/gum"

    // Testing for Microphone
    private val allowedDomain = "webrtc.github.io/samples/src/content/getusermedia/audio"

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

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
             if (!isGranted) {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private var pendingPermissionRequest: PermissionRequest? = null
    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            pendingPermissionRequest?.let { request ->
                val grantedResources = mutableListOf<String>()
                if (results[Manifest.permission.CAMERA] == true &&
                    request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
                ) {
                    grantedResources.add(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
                }
                if (results[Manifest.permission.RECORD_AUDIO] == true &&
                    request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                ) {
                    grantedResources.add(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                }

                if (grantedResources.isNotEmpty()) {
                    request.grant(grantedResources.toTypedArray())
                } else {
                    Toast.makeText(
                        this,
                        "Camera and/or microphone permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                    request.deny()
                }
                pendingPermissionRequest = null
            }
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
                        fileChooserLauncher = fileChooserLauncher,
                        locationPermissionLauncher = locationPermissionLauncher,
                        permissionsLauncher =permissionsLauncher,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        DynamicShortcut(this, allowedDomain, shortcuts).createDynamicShortcut()
    }

    override fun onResume() {
        super.onResume()

        val urlFromData = intent?.data?.toString()
        val urlFromExtra = intent?.getStringExtra("url")
        val url = urlFromData ?: urlFromExtra

        if (!url.isNullOrEmpty()) {
            currentUrl = url
            webViewState.value?.loadUrl(currentUrl)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}