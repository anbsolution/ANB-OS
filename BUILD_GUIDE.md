# Build guide — ANB Launcher source

## Required tools
- Android Studio with Android SDK Platform 35 and Android SDK Build-Tools installed.
- JDK 17 for the Android Gradle Plugin 8.7.x build process.
- Gradle 8.9 for Android Gradle Plugin 8.7.x.

The project currently does not include Gradle Wrapper scripts/JAR. Open `android-launcher` in Android Studio and allow it to configure/sync the project, or create a wrapper from a machine with Gradle installed using `gradle wrapper --gradle-version 8.9`.

## Build from Android Studio
1. Extract the ANB OS ZIP.
2. Open the `android-launcher` directory as the project root.
3. Install Android SDK Platform 35 if prompted.
4. Use JDK 17 for Gradle.
5. Sync Gradle, then select **Build > Make Project**.
6. Run on an emulator first. Review logs before testing on a physical phone.
7. Choose ANB Launcher from Android's default Home app settings if you want to try it as your launcher.

## Important
- This is an Android launcher app, not a complete Android OS or a flashable ROM.
- Quick Controls and most settings screens are prototype-only; they do not toggle real system features.
- The app has not been compiled or device-tested in this environment. Do not assume the build succeeds until Android Studio reports success.
- Do not flash this ZIP in recovery or use it as a ROM package.
