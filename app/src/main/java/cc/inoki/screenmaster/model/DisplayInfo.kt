package cc.inoki.screenmaster.model

data class DisplayInfo(
    val id: Int,
    val name: String,
    val width: Int,
    val height: Int,
    val densityDpi: Int,
    val refreshRate: Float,
    val isDefault: Boolean,
    val isOn: Boolean = true,
    val deviceManufacturer: String = "",
    val deviceModel: String = "",
    val displayType: String = "",
    val hdrCapabilities: String = "",
    val rotation: String = ""
)