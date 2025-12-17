package cc.inoki.screenmaster.helper

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import cc.inoki.screenmaster.model.DisplayInfo

class DisplayHelper(private val context: Context) {

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

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
                isDefault = display.displayId == Display.DEFAULT_DISPLAY
            )
        }
    }

    fun getDisplay(displayId: Int): Display? {
        return displayManager.displays.find { it.displayId == displayId }
    }
}