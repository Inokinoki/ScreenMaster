package cc.inoki.screenmaster.helper

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.Display
import android.view.Surface
import cc.inoki.screenmaster.model.DisplayInfo

class DisplayHelper(private val context: Context) {

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    fun getAllDisplays(): List<DisplayInfo> {
        val displays = displayManager.displays
        return displays.mapIndexed { index, display ->
            DisplayInfo(
                id = display.displayId,
                name = "[" + getDisplayType(display) + "] " + (display.name ?: "Display ${index + 1}"),
                width = display.mode.physicalWidth,
                height = display.mode.physicalHeight,
                densityDpi = context.resources.displayMetrics.densityDpi,
                refreshRate = display.refreshRate,
                isDefault = display.displayId == Display.DEFAULT_DISPLAY,
                isOn = isDisplayOn(display.displayId),
                deviceManufacturer = Build.MANUFACTURER,
                deviceModel = Build.MODEL,
                displayType = getDisplayType(display),
                hdrCapabilities = getHdrCapabilities(display),
                rotation = getRotation(display)
            )
        }
    }

    private fun getDisplayType(display: Display): String {
        return when {
            display.displayId == Display.DEFAULT_DISPLAY -> "Built-in"
            else -> "External"
        }
    }

    private fun getHdrCapabilities(display: Display): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val hdrCapabilities = display.hdrCapabilities
                if (hdrCapabilities != null && hdrCapabilities.supportedHdrTypes.isNotEmpty()) {
                    val types = hdrCapabilities.supportedHdrTypes.map { type ->
                        when (type) {
                            Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION -> "Dolby Vision"
                            Display.HdrCapabilities.HDR_TYPE_HDR10 -> "HDR10"
                            Display.HdrCapabilities.HDR_TYPE_HLG -> "HLG"
                            Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS -> "HDR10+"
                            else -> "HDR"
                        }
                    }
                    types.joinToString(", ")
                } else {
                    "None"
                }
            } else {
                "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }

    private fun getRotation(display: Display): String {
        return when (display.rotation) {
            Surface.ROTATION_0 -> "0°"
            Surface.ROTATION_90 -> "90°"
            Surface.ROTATION_180 -> "180°"
            Surface.ROTATION_270 -> "270°"
            else -> "Unknown"
        }
    }

    fun getDisplay(displayId: Int): Display? {
        return displayManager.displays.find { it.displayId == displayId }
    }

    fun isDisplayOn(displayId: Int): Boolean {
        val display = getDisplay(displayId)
        return display?.state == Display.STATE_ON
    }

    // Note: Android does not provide public APIs to control display power states.
    // DisplayManager can only monitor state changes, not control them.
    // To control display power, you would need:
    // 1. Root access and shell commands
    // 2. System-level app with platform signature
    // 3. Device-specific manufacturer APIs (if available)

    fun getDisplayStateString(displayId: Int): String {
        val display = getDisplay(displayId)
        return when (display?.state) {
            Display.STATE_ON -> "ON"
            Display.STATE_OFF -> "OFF"
            Display.STATE_DOZE -> "DOZE"
            Display.STATE_DOZE_SUSPEND -> "DOZE_SUSPEND"
            Display.STATE_VR -> "VR"
            Display.STATE_ON_SUSPEND -> "ON_SUSPEND"
            else -> "UNKNOWN"
        }
    }

    fun getScreenTimeout(): Long {
        // Note: SCREEN_OFF_TIMEOUT is a system-wide setting, not per-display
        // Returns timeout in milliseconds, or -1 if not set
        return try {
            Settings.System.getLong(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT
            )
        } catch (e: Settings.SettingNotFoundException) {
            -1L
        }
    }

    fun getScreenTimeoutFormatted(): String {
        val timeoutMs = getScreenTimeout()
        if (timeoutMs < 0) {
            return "Not set"
        }
        val seconds = timeoutMs / 1000
        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 3600 -> {
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                if (remainingSeconds == 0L) "${minutes}m" else "${minutes}m ${remainingSeconds}s"
            }
            else -> {
                val hours = seconds / 3600
                val remainingMinutes = (seconds % 3600) / 60
                if (remainingMinutes == 0L) "${hours}h" else "${hours}h ${remainingMinutes}m"
            }
        }
    }
}