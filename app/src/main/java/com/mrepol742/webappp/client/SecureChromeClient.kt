package com.mrepol742.webappp.client

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout

class SecureChromeClient(
    private val activity: Activity
) : WebChromeClient() {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val fileChooserRequestCode = 1001
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var fullScreenContainer: FrameLayout? = null

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == fileChooserRequestCode) {
            val result: Array<Uri>? = if (resultCode == Activity.RESULT_OK && data != null) {
                data.data?.let { arrayOf(it) }
            } else null

            filePathCallback?.onReceiveValue(result)
            filePathCallback = null
        }
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        this.filePathCallback?.onReceiveValue(null)
        this.filePathCallback = filePathCallback

        val intent = fileChooserParams.createIntent()
        return try {
            activity.startActivityForResult(intent, fileChooserRequestCode)
            true
        } catch (e: Exception) {
            this.filePathCallback = null
            false
        }
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        // If a view already exists, hide it first
        if (customView != null) {
            callback?.onCustomViewHidden()
            return
        }

        customView = view
        customViewCallback = callback

        // Create a fullscreen container
        fullScreenContainer = FrameLayout(activity).apply {
            setBackgroundColor(android.graphics.Color.BLACK)
            addView(
                view,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

        // Add to decorView
        activity.window.decorView.findViewById<ViewGroup>(android.R.id.content).addView(
            fullScreenContainer,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        // Hide system UI
        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }

    override fun onHideCustomView() {
        fullScreenContainer?.removeAllViews()
        activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
            .removeView(fullScreenContainer)

        fullScreenContainer = null
        customView = null
        customViewCallback?.onCustomViewHidden()

        // Restore system UI
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }


override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        AlertDialog.Builder(activity)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm() }
            .setOnDismissListener { result?.confirm() }
            .create()
            .show()
        return true
    }

    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        AlertDialog.Builder(activity)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm() }
            .setNegativeButton(android.R.string.cancel) { _, _ -> result?.cancel() }
            .create()
            .show()
        return true
    }

    override fun onJsPrompt(
        view: WebView?,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        val input = android.widget.EditText(activity).apply { setText(defaultValue) }

        AlertDialog.Builder(activity)
            .setMessage(message)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                result?.confirm(input.text.toString())
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> result?.cancel() }
            .create()
            .show()
        return true
    }
}
