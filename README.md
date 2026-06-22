# Mile Converter

A minimal Android app built with Kotlin and Jetpack Compose for fast Google Play publication.

## Features

- Convert between miles, kilometers, and nautical miles
- Choose any of the three units as the input unit
- Simple About screen with a link to `https://github.com/krzysztofcal`
- Single-activity app with no backend, analytics, ads, or extra permissions

## Requirements

- Android Studio Ladybug or newer recommended
- JDK 17
- Android SDK 35

## Build and run

```bash
./gradlew assembleDebug
```

Run tests:

```bash
./gradlew test
```

Build a release Android App Bundle for Google Play:

```bash
./gradlew bundleRelease
```

Signed release builds use `KEYSTORE_FILE`, `KEY_ALIAS`, `KEY_PASSWORD`, and `STORE_PASSWORD` from environment variables or Gradle properties.

Open the project in Android Studio and run the `app` configuration on a device or emulator.
