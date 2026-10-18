# ANB Launcher — Android source starter

## v0.3.10 prototype features
- Declares both Android Home and regular launcher intent filters.
- Queries launchable activities using a narrow `MAIN` + `LAUNCHER` intent signature.
- Displays installed apps using their real labels and icons, with live name filtering.
- Uses a responsive 3–5 column app grid based on available screen width.
- Opens selected app activities; long-press an app tile to open Android App info.
- Refreshes clock/date labels while the launcher is in the foreground and pauses updates in the background.
- Adds a shortcut to Android's Home settings so the user can choose a default launcher.
- Quick Controls and most Appearance/Wallpaper settings remain demo-only and do not change device hardware/system settings.

## Build and validation
Open this folder in Android Studio, sync Gradle, install Android SDK Platform 35 and compatible build tools, then build and run on an emulator first.

No Gradle sync, APK build, emulator test, or physical-device test has been performed for this archive. Some device manufacturers may have additional package-visibility or launcher behavior differences. The project is source code, not a ROM or flashable package.
