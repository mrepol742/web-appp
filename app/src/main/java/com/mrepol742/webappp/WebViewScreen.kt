package com.mrepol742.webappp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mrepol742.webappp.client.SecureChromeClient
import com.mrepol742.webappp.client.SecureWebViewClient
import com.mrepol742.webappp.utils.DownloadListener
import kotlinx.coroutines.delay

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    allowedDomain: String,
    initialUrl: String,
    webViewState: MutableState<WebView?>,
    secureWebChromeClientState: MutableState<SecureChromeClient?>,
    fileChooserLauncher: ActivityResultLauncher<Intent>,
    locationPermissionLauncher: ActivityResultLauncher<String>,
    permissionsLauncher: ActivityResultLauncher<Array<String>>,
    modifier: Modifier = Modifier
) {

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                val appName = context.getString(R.string.app_name)
                val appVersion = BuildConfig.VERSION_NAME

                /*
                 * Adjust this base on your web app preferences
                 * but if you use HTTPS its highly unlikely
                 * your going to need this so ill turn them off
                 */
                settings.allowFileAccess = false
                settings.allowContentAccess = false

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadsImagesAutomatically = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                settings.mediaPlaybackRequiresUserGesture = false
                settings.setGeolocationEnabled(true)

                settings.builtInZoomControls = false
                settings.setSupportZoom(false)
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    settings.saveFormData = false
                }

                setOnLongClickListener { true }
                isLongClickable = false
                isHapticFeedbackEnabled = false

                isVerticalScrollBarEnabled = true
                isHorizontalScrollBarEnabled = false

                scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
                overScrollMode = WebView.OVER_SCROLL_NEVER

                val currentUA = settings.userAgentString
                val safeAppName = appName.replace("\\s+".toRegex(), "")
                settings.userAgentString = "$currentUA $safeAppName/$appVersion"
                val newUA = settings.userAgentString

                Log.d("WebView", "User Agent: $newUA")

                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)

                webViewClient = SecureWebViewClient(context, allowedDomain)

                val chromeClient = SecureChromeClient(
                    context as Activity,
                    fileChooserLauncher,
                    locationPermissionLauncher,
                    permissionsLauncher
                )
                webChromeClient = chromeClient
                secureWebChromeClientState.value = chromeClient
                setDownloadListener(DownloadListener(context))

                loadUrl(initialUrl)
                webViewState.value = this
            }
        },

        update = { webView ->
            webView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    )

    var canGoBack by remember { mutableStateOf(false) }

    LaunchedEffect(webViewState.value) {
        val webView = webViewState.value ?: return@LaunchedEffect
        while (true) {
            canGoBack = webView.canGoBack()
            delay(100) // Poll every 100ms
        }
    }

    BackHandler(enabled = canGoBack) {
        webViewState.value?.goBack()
    }
}
