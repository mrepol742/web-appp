package com.mrepol742.webappp

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mrepol742.webappp.client.SecureChromeClient
import com.mrepol742.webappp.client.SecureWebViewClient
import com.mrepol742.webappp.utils.DownloadListener

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(allowedDomain: String, initialUrl: String, webViewState: MutableState<WebView?>, modifier: Modifier = Modifier) {

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                val appName = context.getString(R.string.app_name)
                val appVersion = BuildConfig.VERSION_NAME

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadsImagesAutomatically = true
                settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                settings.mediaPlaybackRequiresUserGesture = false

                settings.builtInZoomControls = false
                settings.setSupportZoom(false)
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true

                setOnLongClickListener { true }
                isLongClickable = false
                isHapticFeedbackEnabled = false

                val currentUA = settings.userAgentString
                val safeAppName = appName.replace("\\s+".toRegex(), "")
                settings.userAgentString = "$currentUA $safeAppName/$appVersion"
                val newUA = settings.userAgentString

                Log.d("WebView", "User Agent: $newUA")

                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)

                webViewClient = SecureWebViewClient(context, allowedDomain)
                webChromeClient = SecureChromeClient(context as Activity)
                setDownloadListener(DownloadListener(context))

                loadUrl(initialUrl)
                webViewState.value = this
            }
        }
    )
}
