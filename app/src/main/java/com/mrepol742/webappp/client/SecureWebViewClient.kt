package com.mrepol742.webappp.client

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

class SecureWebViewClient(
    private val context: Context,
    private val allowedDomain: String
) : WebViewClient() {

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url == null) return false
        return handleUri(url.toUri())
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val uri = request?.url ?: return false
        return handleUri(uri)
    }

    private fun handleUri(uri: Uri): Boolean {
        val scheme = uri.scheme?.lowercase()

        return when (scheme) {
            "tel", "mailto", "sms", "geo" -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("WebView", "No app found for scheme: $scheme", e)
                }
                true
            }
            "http", "https" -> {
                val host = uri.host ?: return false
                val allowedDomainRegex = Regex("(^|\\.)${Regex.escape(allowedDomain)}$")
                if (allowedDomainRegex.matches(host)) {
                    false // load in WebView
                } else {
                    openInCustomTab(uri)
                    true
                }
            }
            else -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("WebView", "No app found to handle URI: $uri", e)
                }
                true
            }
        }
    }

    private fun openInCustomTab(uri: Uri) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()

        customTabsIntent.launchUrl(context, uri)
    }
}
