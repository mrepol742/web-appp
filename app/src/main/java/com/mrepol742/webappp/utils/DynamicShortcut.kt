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

            val shortcuts = listOf(
                "projects" to "My Projects",
                "gaming" to "The Games I Played",
                "contact-me" to "Contact Me",
            )

            val dynamicShortcuts = shortcuts.map { (idSuffix, label) ->
                ShortcutInfo.Builder(context, "shortcut_$idSuffix")
                    .setShortLabel(label)
                    .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher_round))
                    .setIntent(
                        Intent(context, MainActivity::class.java).apply {
                            action = Intent.ACTION_VIEW
                            putExtra("url", "https://$allowedDomain/$idSuffix")
                        }
                    )
                    .build()
            }

            shortcutManager?.dynamicShortcuts = dynamicShortcuts
        }
    }
}