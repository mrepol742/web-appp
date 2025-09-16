package com.mrepol742.webappp.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.webkit.DownloadListener
import android.webkit.URLUtil
import androidx.core.net.toUri

class DownloadListener(private val context: Context) : DownloadListener {

    override fun onDownloadStart(
        url: String?,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
        contentLength: Long
    ) {
        if (url == null) return

        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)

        val request = DownloadManager.Request(url.toUri()).apply {
            setTitle(fileName)
            setDescription("Downloading file...")
            allowScanningByMediaScanner()
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }
}
