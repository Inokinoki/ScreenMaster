package cc.inoki.screenmaster.helper

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.PowerManager
import android.provider.Settings
import android.view.Display
import cc.inoki.screenmaster.model.DisplayInfo

class DisplayHelper(private val context: Context) {

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    fun getAllDisplays(): List<DisplayInfo> {
        val displays = displayManager.displays
        return displays.mapIndexed { index, display ->
            DisplayInfo(
                id = display.displayId,
                name = display.name ?: "Display ${index + 1}",
                width = display.mode.physicalWidth,
                height = display.mode.physicalHeight,
                densityDpi = context.resources.displayMetrics.densityDpi,
                refreshRate = display.refreshRate,
                isDefault = display.displayId == Display.DEFAULT_DISPLAY,
                isOn = isDisplayOn(display.displayId)
            )
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