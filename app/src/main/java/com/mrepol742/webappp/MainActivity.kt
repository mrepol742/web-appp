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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mrepol742.webappp.client.SecureChromeClient
import com.mrepol742.webappp.ui.theme.WebApppTheme
import com.mrepol742.webappp.utils.DynamicShortcut

class MainActivity : ComponentActivity() {
    private val allowedDomain = BuildConfig.APPLICATION_URL
    private var currentUrl: String = "https://$allowedDomain"
    private val shortcuts: List<Pair<String, String>> =
        BuildConfig.SHORTCUTS
            .split(";")
            .map {
                val (path, label) = it.split("|", limit = 2)
                path to label
            }
    private val webViewState = mutableStateOf<WebView?>(null)
    private val secureWebChromeClientState = mutableStateOf<SecureChromeClient?>(null)
    private val isLoading = mutableStateOf<Boolean?>(null)

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
                Box(modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()) {
                    WebViewScreen(
                        allowedDomain = allowedDomain,
                        initialUrl = currentUrl,
                        webViewState = webViewState,
                        secureWebChromeClientState = secureWebChromeClientState,
                        fileChooserLauncher = fileChooserLauncher,
                        locationPermissionLauncher = locationPermissionLauncher,
                        permissionsLauncher = permissionsLauncher,
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Loading overlay
                    if (isLoading.value == true) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(45.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )
                        }
                    }
                }
            }
        }

        DynamicShortcut(this, allowedDomain, shortcuts).createDynamicShortcut()
    }

    override fun onResume() {
        super.onResume()
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val urlFromData = intent?.data?.toString()
        val urlFromExtra = intent?.getStringExtra("url")
        val url = urlFromData ?: urlFromExtra

        if (!url.isNullOrEmpty()) {
            currentUrl = url
            webViewState.value?.loadUrl(currentUrl)

            intent?.replaceExtras(Bundle())
            intent?.setAction(null)
            intent?.setData(null)
        }
    }

}