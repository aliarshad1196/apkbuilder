TV Home Lite - minimal Android TV launcher

Target device:
  Android 4.0.4 / API 15
  ARMv7 (armeabi-v7a)

Features:
  - Android HOME activity
  - Lists installed apps that expose a launcher activity
  - Large focusable buttons navigable by D-pad / remote
  - Settings shortcut
  - No third-party libraries, widgets, animations, or background services

Build requirements:
  - JDK 8
  - Android SDK Platform 25
  - Android Build Tools 25.0.3
  - Gradle 3.x compatible with Android Gradle Plugin 2.3.3

Build:
  Open this folder in a compatible Android Studio installation, or run:
      gradle assembleDebug

APK output:
  app/build/outputs/apk/app-debug.apk

Install from Windows PowerShell (from your platform-tools folder):
  .\adb.exe install "FULL_PATH_TO_app-debug.apk"

Then choose TV Home Lite when Android asks which Home app to use.
Do not disable or remove the factory launcher until this one has been tested.
