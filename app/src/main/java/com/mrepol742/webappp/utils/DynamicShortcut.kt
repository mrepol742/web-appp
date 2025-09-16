package com.mrepol742.webappp.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.mrepol742.webappp.MainActivity
import com.mrepol742.webappp.R

class DynamicShortcut(private val context: Context, private val allowedDomain: String) {

    fun createDynamicShortcut() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)

            val projectShortcut = ShortcutInfo.Builder(context, "shortcut_projects")
                .setShortLabel("Projects")
                .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher_round))
                .setIntent(
                    Intent(context, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        putExtra("url", "https://$allowedDomain/projects")
                    }
                )
                .build()

            val gamingShortcut = ShortcutInfo.Builder(context, "shortcut_gaming")
                .setShortLabel("Gaming")
                .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher_round))
                .setIntent(
                    Intent(context, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        putExtra("url", "https://$allowedDomain/gaming")
                    }
                )
                .build()

            val contactMe = ShortcutInfo.Builder(context, "shortcut_contact_me")
                .setShortLabel("Contact")
                .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher_round))
                .setIntent(
                    Intent(context, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        putExtra("url", "https://$allowedDomain/contact-me")
                    }
                )
                .build()

            shortcutManager?.dynamicShortcuts = listOf(projectShortcut, gamingShortcut, contactMe)
        }
    }
}