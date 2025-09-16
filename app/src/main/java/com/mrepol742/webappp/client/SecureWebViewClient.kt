package com.mrepol742.webappp.client

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

class SecureWebViewClient(
    private val context: Context,
    private val allowedDomain: String
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        // hide footer for non dynamic pages
        val js = """
            javascript:(function() {
                var footers = document.getElementsByTagName('footer');
                for (var i=0; i<footers.length; i++) {
                    footers[i].style.display='none';
                }
       
                var footerById = document.getElementById('footer');
                if (footerById) {
                    footerById.style.display='none';
                } 
                })();
            """.trimIndent()

        view?.evaluateJavascript(js, null)
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url == null) return false
        return handleUri(url.toUri())
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val uri = request?.url ?: return false
        return handleUri(uri)
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)

        if (request?.isForMainFrame == true) {
            when (errorResponse?.statusCode) {
                400 -> view?.loadUrl("file:///android_asset/http_error/error_400.html")
                401, 403 -> view?.loadUrl("file:///android_asset/http_error/error_403.html")
                404 -> view?.loadUrl("file:///android_asset/http_error/error_404.html")
                500 -> view?.loadUrl("file:///android_asset/http_error/error_500.html")
                502, 504 -> view?.loadUrl("file:///android_asset/http_error/error_504.html")
                503 -> view?.loadUrl("file:///android_asset/http_error/error_503.html")
            }
        }
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
