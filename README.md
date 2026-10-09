# 🎨 ColorByte

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-7B1FA2.svg)](https://m3.material.io)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)](https://developer.android.com/about/dashboards)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-37-blue.svg)](https://developer.android.com/about/versions/16)

**ColorByte** is a modern, fast, and feature-rich Android app designed for developers, UI/UX designers, and digital artists. It offers real-time bi-directional conversion between HEX and RGB color spaces, color breakdown calculations, palette management, and deep UI customization—all built natively with Jetpack Compose and Material Design 3.

---

## ✨ Features

- **⚡ Instant Bi-Directional Conversion**: Convert HEX codes to RGB values and vice versa in real time with instant feedback and input validation.
- **🎛️ Interactive RGB Controls**: Tune red, green, and blue values via precise numeric inputs or live-updating RGB sliders.
- **📊 Comprehensive Color Breakdown**:
  - Individual channel breakdowns for Red, Green, and Blue.
  - CSS-ready color formatted output (`rgb(r, g, b)`).
  - WCAG relative luminance calculation with automatic high-contrast text color selection (dark or light text).
- **💾 Color Palette & History Management**:
  - Save colors with custom titles and notes.
  - Mark favorite colors for fast access.
  - Search, filter, and sort your saved color library.
  - Export and import color history as JSON for backup and sharing.
- **⚙️ Customization & Preferences**:
  - Light, Dark, or System Theme modes.
  - Dynamic Color (Material You) palette integration.
  - Customizable HEX output (Uppercase vs. Lowercase, `#` prefix toggle).
  - Haptic feedback and history item limit options.
- **📱 Clean & Responsive UI**: Built 100% with Jetpack Compose, featuring smooth animations and an edge-to-edge layout.

---

## 🛠️ Tech Stack & Architecture

ColorByte follows **Clean Architecture** principles and recommended Android architecture patterns:

- **UI Layer**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 components, Navigation Compose, and custom state management via `StateFlow` and `ViewModel`.
- **Data Layer**: [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) for persistent user settings and color history storage.
- **Domain Layer**: Pure Kotlin business logic (`ColorByteConverter`) for RGB/HEX conversions, validation, luminance calculations, and color contrast logic.
- **Language**: 100% [Kotlin](https://kotlinlang.org/) with Coroutines & Flow.

```
com.example.colorbyte/
├── data/
│   ├── model/          # Data models (AppSettings, SavedColor, RgbColor)
│   └── repository/     # Data repository & DataStore handling
├── domain/
│   └── ColorByteConverter.kt  # Business logic & conversion math
├── ui/
│   ├── components/     # Reusable Compose UI components
│   ├── navigation/     # App navigation routes
│   ├── screens/        # Screen level composables (Converter, History, Settings, About)
│   ├── theme/          # Material 3 typography, colors, and themes
│   ├── util/           # Haptic helpers and UI utilities
│   └── viewmodel/      # ColorByteViewModel & Factory
└── MainActivity.kt
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Ladybug or newer.
- **JDK**: Java 11 or higher.
- **Android Device / Emulator**: Running Android 7.0 (API level 24) or higher.

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/ColorByte.git
   cd ColorByte
   ```

2. **Open in Android Studio**:
   - Select **File > Open** and choose the `ColorByte` directory.
   - Allow Gradle to sync project dependencies.

3. **Run on Device or Emulator**:
   - Connect an Android device or launch an emulator.
   - Click **Run (Shift + F10)** in Android Studio.

---

## 🧪 Testing

Run unit tests directly via Gradle:
```bash
./gradlew test
```

Unit tests cover:
- RGB to HEX conversion (casing, prefix, edge values 0–255).
- HEX to RGB normalization and validation.
- Relative luminance calculation and WCAG contrast rules.
- JSON serialization and deserialization for saved colors.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Abdullah Ateeq**
- GitHub: [@abdullah-ateeq](https://github.com/abdullah-ateeq)
