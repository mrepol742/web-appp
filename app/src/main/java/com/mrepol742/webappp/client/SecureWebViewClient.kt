package com.mrepol742.webappp.client;

import android.content.Context
import android.content.Intent
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class SecureWebViewClient(
        private val context: Context,
        private val allowedDomain: String
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
    ): Boolean {
        val uri = request?.url ?: return false
        val host = uri.host ?: return false

        Log.d("WebView", "Loading host: $host")

        val allowedDomainRegex = Regex("(^|\\.)${Regex.escape(allowedDomain)}$")

        return if (allowedDomainRegex.matches(host)) {
            false
        } else {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
            true
        }
    }
}
