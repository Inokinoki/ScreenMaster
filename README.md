# Screen Master

A powerful Android application designed to facilitate launching applications on specific displays in multi-screen environments.

## Overview

ScreenMaster enables users to easily manage and launch applications across multiple displays connected to an Android device. Perfect for devices with external monitors, presentation setups, or any multi-screen configuration.

## Features

- **Display Discovery**: Automatically detects and lists all available displays/screens
- **Detailed Display Information**: View resolution, refresh rate, density, and display ID for each screen
- **Display State Monitoring**: Real-time monitoring of display power state (ON/OFF/DOZE/etc.) with color-coded indicators
- **Screen Timeout Information**: Shows the system-wide screen timeout setting in the app bar
- **App Launcher**: Browse all installed launchable applications
- **Multi-Screen Support**: Launch any app on any connected display
- **Launch Confirmation**: Toast notification confirms app launch with display name and ID
- **Modern UI**: Built with Jetpack Compose and Material Design 3
- **Intuitive Navigation**: Simple two-step process to launch apps on specific screens

## Screenshots

### Screen List View
- Displays all available screens with detailed information
- Default display is marked with a star icon
- Shows resolution, refresh rate, and density for each screen

### App List View
- Lists all launchable applications
- Shows app icons, names, and package names
- Tap to launch on the selected display

## Requirements

- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 15 (API 36)
- **Compile SDK**: Android 15 (API 36)
- **Kotlin**: 2.0.21
- **Android Gradle Plugin**: 8.11.2

## Permissions

The app requires the following permission:

- `QUERY_ALL_PACKAGES`: Required to discover and list all installed launchable applications

## Display Power Control Limitation

**Important:** Android does not provide public APIs to control display power states. The `DisplayManager` class can only:
- Monitor display state changes
- Get display properties
- Control brightness
- Create virtual displays

It **cannot** turn displays on or off programmatically. To control display power, you would need:
1. Root access and shell commands
2. System-level app with platform signature
3. Device-specific manufacturer APIs (if available)

This app **monitors** display states but does **not** control them, as this functionality is not available through Android's public API.

## How to Use

### Launching Apps on Displays

1. **Launch ScreenMaster**: Open the app to see the list of available displays
2. **Select a Display**: Tap on any display card to choose it as the target screen
3. **Choose an App**: Browse the list of installed apps and tap one to launch
4. **Confirmation**: A toast message confirms the app launch with the display name and ID

### Monitoring Display State

1. **View Display State**: Each display shows its current power state with color coding
   - Blue text indicates the display is active (ON, DOZE, VR, etc.)
   - Red text indicates the display is inactive (OFF)
2. **Supported States**: ON, OFF, DOZE, DOZE_SUSPEND, VR, ON_SUSPEND, UNKNOWN
3. **Screen Timeout**: The system-wide screen timeout setting is displayed in the app bar
   - Shows how long before displays turn off due to inactivity
   - Note: This is a system-wide setting, not per-display

## Technical Details

### Architecture

- **UI Layer**: Jetpack Compose with Material Design 3
- **Navigation**: Jetpack Navigation Compose
- **Display Management**: Android DisplayManager API
- **Package Management**: Android PackageManager API

### Project Structure

```
app/src/main/java/cc/inoki/screenmaster/
├── MainActivity.kt
├── helper/
│   ├── AppHelper.kt          # Manages app discovery and launching
│   └── DisplayHelper.kt      # Manages display detection
├── model/
│   ├── AppInfo.kt            # Data model for applications
│   └── DisplayInfo.kt        # Data model for displays
├── navigation/
│   └── AppNavigation.kt      # Navigation configuration
├── ui/
│   ├── screen/
│   │   ├── AppListScreen.kt  # App selection screen
│   │   └── ScreenListScreen.kt # Display selection screen
│   └── theme/                # Material Design 3 theme
```

### Key Components

#### DisplayHelper
Provides access to display information using Android's DisplayManager and Settings:
- `getAllDisplays()`: Returns list of all available displays with their current state
- `getDisplay(displayId)`: Retrieves specific display by ID
- `isDisplayOn(displayId)`: Checks if a display is currently powered on
- `getDisplayStateString(displayId)`: Returns detailed state string (ON/OFF/DOZE/etc.)
- `getScreenTimeout()`: Returns the system-wide screen timeout in milliseconds
- `getScreenTimeoutFormatted()`: Returns formatted screen timeout (e.g., "30s", "2m", "5m 30s")

#### AppHelper
Manages application discovery and launching:
- `getLaunchableApps()`: Returns all apps with launcher activities
- `launchApp(packageName, displayId)`: Launches app on specified display

## Building the Project

### Using Android Studio

1. Clone or download the project
2. Open Android Studio
3. Select "Open an existing project"
4. Navigate to the ScreenMaster directory
5. Wait for Gradle sync to complete
6. Click "Run" or press Shift+F10

### Using Command Line

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build and run
./gradlew installDebug
adb shell am start -n cc.inoki.screenmaster/.MainActivity
```

## Dependencies

- AndroidX Core KTX
- AndroidX Lifecycle Runtime KTX
- AndroidX Activity Compose
- Jetpack Compose BOM
- Jetpack Compose UI
- Material Design 3
- Navigation Compose

See `gradle/libs.versions.toml` for specific versions.

## Multi-Display Support

The app uses Android's native multi-display APIs:
- `DisplayManager` to enumerate displays
- `ActivityOptions.setLaunchDisplayId()` to target specific displays
- `FLAG_ACTIVITY_LAUNCH_ADJACENT` for multi-window scenarios

### Supported Devices

This app works best on:
- Android devices with external display support
- Tablets with secondary displays
- Devices in desktop mode (Samsung DeX, etc.)
- Android TV boxes with multiple HDMI outputs
- Foldable devices with multiple screens

## Development

### Adding New Features

The app is structured for easy extension:
- Add new screens in `ui/screen/`
- Add new routes in `navigation/AppNavigation.kt`
- Create helper classes in `helper/` for new functionality
- Define data models in `model/`

### Code Style

The project follows the official Kotlin coding conventions and uses:
- Kotlin 2.0.21
- Jetpack Compose for UI
- Material Design 3 guidelines

## Troubleshooting

### Apps Don't Launch on External Display

- Ensure the external display is properly connected
- Some apps may not support secondary displays
- Check that the display is active in system settings

### No External Displays Shown

- Verify external displays are connected and recognized by Android
- Some devices may require developer options enabled
- Check cable connections and display compatibility

### Permission Issues

- The app requests `QUERY_ALL_PACKAGES` which may require user consent on some devices
- This permission is necessary to list all installed apps

### Display State Monitoring

- The app can monitor display states (ON/OFF/DOZE/etc.) using Android's public Display API
- Display power control is NOT available through public Android APIs
- State information is read-only and refreshes when you navigate back to the screen list

## License

This project is provided as-is for educational and personal use.

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## Version History

### 1.1.0 (Current)
- Added display state monitoring (ON/OFF/DOZE/etc. detection)
- Added detailed display state information
- Added screen timeout display in the app bar
- Added toast notifications for app launches with display ID
- Improved display card UI with state indicators
- Documented Android API limitations for display power control

### 1.0.0
- Initial release
- Display detection and listing
- App discovery and launching
- Multi-display support
- Material Design 3 UI
