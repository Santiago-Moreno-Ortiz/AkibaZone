package com.example.akibazone.presentation.player

import android.annotation.SuppressLint
import android.content.Context

/** A capability for the app-owned raw demo resource, never an arbitrary incoming URI. */
class DemoVideo private constructor(val uri: String) {
    companion object {
        // Optional development asset: do not reference R.raw.demo_video until it exists.
        @SuppressLint("DiscouragedApi")
        fun resolve(context: Context): DemoVideo? {
            val id = context.resources.getIdentifier("demo_video", "raw", context.packageName)
            return fromResource(context.packageName, id)
        }

        internal fun fromResource(packageName: String, resourceId: Int): DemoVideo? {
            if (resourceId <= 0 || !packageName.matches(Regex("[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)+"))) return null
            return DemoVideo("android.resource://$packageName/$resourceId")
        }
    }
}
