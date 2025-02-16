# Lumeo AudioBook Player

## Overview

Lumeo AudioBook Player is an Android application designed for seamless audiobook playback. The app provides features to enhance the listening experience, including:

- Playback controls (play, pause, skip)
- Sleep timer functionality
- Custom notification controls
- Adaptive UI with modern design

## Project Structure

```
/build.gradle          - Project-level Gradle configuration
/app/                  - Main application module
  /build.gradle        - App-level Gradle configuration
  /src/                - Application source code
    /main/             - Main source set
      /java/           - Java/Kotlin source files
        /com/example/audiobookplayer/
          AudioPlaybackService.kt - Background audio service
          MainActivity.kt         - Main entry point
      /res/            - Application resources
        /layout/       - UI layout files
        /drawable/     - Image and vector assets
        /values/       - Strings, styles, and colors
/google-services.json  - Firebase configuration
```

## Features

- **Audio Playback**: Robust service-based audio playback system
- **Notification Controls**: Media controls in notification shade
- **Custom UI Elements**: Styled components for consistent look and feel
- **Adaptive Design**: Optimized for various screen sizes and orientations

## Requirements

- Android 5.0+ (API level 21)
- Android Studio (for development)
- Gradle build system

## Setup

1. Clone the repository:
   ```bash
   git clone git@github.com:alex-whitehouse/Lumeo-AudioBookPlayer.git
   ```

2. Open the project in Android Studio

3. Build and run:
   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch
3. Submit a pull request with detailed changes

## License

This project is licensed under the MIT License - see the LICENSE file for details.
