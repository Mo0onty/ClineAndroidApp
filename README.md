# Cline Android

[![Build Status](https://github.com/your-org/cline-android/actions/workflows/build.yml/badge.svg)](https://github.com/your-org/cline-android/actions/workflows/build.yml)
[![Tests](https://github.com/your-org/cline-android/actions/workflows/test.yml/badge.svg)](https://github.com/your-org/cline-android/actions/workflows/test.yml)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)

**Cline Android** is a powerful, production-ready AI coding agent app built with **Material Design 3 Expressive** principles and **Kotlin + Jetpack Compose**.

## 🚀 Features

### Core Capabilities
- ✅ **AI Coding Agent**: Full Cline agent integration with local runtime
- ✅ **Terminal Emulation**: Built-in terminal with proot-based Ubuntu environment
- ✅ **Chat Interface**: Modern chat UI with AI assistant
- ✅ **Plugin System**: Extensible plugin architecture
- ✅ **Backup & Restore**: Complete state backup and recovery
- ✅ **Offline-First**: No remote downloads required - all assets bundled

### Technical Features
- ✅ **No Root Required**: Uses proot for user-space chroot
- ✅ **No Termux Dependency**: Self-contained runtime
- ✅ **API Key Encryption**: Android Keystore (AES-GCM) for security
- ✅ **Dynamic Color**: Material Design 3 with Cline AI green palette
- ✅ **Multi-Flavor**: Standard (API 30+) and Low (API 23+) builds
- ✅ **arm64-v8a**: Optimized for modern Android devices

### Design System
- ✅ **Material Design 3 Expressive**: Full MD3 implementation
- ✅ **Custom Components**: Buttons, Cards, Inputs, Dialogs, Navigation
- ✅ **Spring Animations**: Smooth, expressive animations
- ✅ **Dark Theme**: Optimized for OLED displays
- ✅ **Cline AI Green Palette**: Official brand colors

## 📱 Screenshots

| Welcome | Chat | Terminal | Settings |
|---------|------|----------|----------|
| ![Welcome](docs/images/welcome.png) | ![Chat](docs/images/chat.png) | ![Terminal](docs/images/terminal.png) | ![Settings](docs/images/settings.png) |

## 🎨 Design System

### Color Palette (Cline AI Official)

| Color | Hex | Usage |
|-------|-----|-------|
| Primary | `#00D4AA` | Main brand color, buttons, accents |
| Secondary | `#00B8A3` | Secondary actions, highlights |
| Tertiary | `#00E5B8` | Accents, borders |
| Surface | `#1A1C1E` | Primary surfaces |
| Background | `#121416` | App background |
| On Surface | `#E0E3E6` | Text on surfaces |
| Error | `#FF5555` | Error states, warnings |

### Typography

| Style | Size | Weight | Usage |
|-------|------|--------|-------|
| Display Large | 57sp | Normal | Main titles |
| Headline Large | 32sp | Normal | Section headers |
| Title Large | 22sp | Bold | Card titles |
| Body Large | 16sp | Normal | Body text |
| Label Large | 14sp | Medium | Labels, captions |

## 🛠️ Prerequisites

### Development Environment
- **JDK**: 17 (Temurin recommended)
- **Android SDK**: API 37 (compile/target)
- **Android NDK**: Version 26
- **Python**: 3.9+
- **Build Tools**: Gradle 9.3.1, Android Gradle Plugin 9.1.1

### Required Tools
```bash
# Install JDK 17
sudo apt install openjdk-17-jdk  # Ubuntu/Debian

# Install Android SDK and NDK
# Download from: https://developer.android.com/studio

# Install Python 3.9+
sudo apt install python3 python3-pip  # Ubuntu/Debian
```

## 📥 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/your-org/cline-android.git
cd cline-android
```

### 2. Set Up Android SDK
Ensure you have:
- Android SDK with API 37
- Android NDK 26
- Build Tools 34.0.0+

Set environment variables:
```bash
export ANDROID_SDK_ROOT=/path/to/android/sdk
export ANDROID_NDK_ROOT=/path/to/android/ndk
export JAVA_HOME=/path/to/jdk-17
```

### 3. Create Local Properties
Create `local.properties` file:
```properties
sdk.dir=/path/to/android/sdk
ndk.dir=/path/to/android/ndk
```

## 🏗️ Building

### Quick Build
```bash
# Make gradlew executable
chmod +x gradlew

# Build debug APK (standard flavor)
./gradlew :app:assembleStandardDebug

# Build release APK
./gradlew :app:assembleStandardRelease
```

### Using Build Script
```bash
# Show help
./build.sh

# Build all assets and APK
./build.sh all standard debug

# Build specific target
./build.sh rootfs          # Build Ubuntu rootfs
./build.sh runtime         # Build Cline runtime
./build.sh assets          # Prepare assets
./build.sh apk standard debug  # Build debug APK
./build.sh apk low release    # Build low-flavor release APK
```

### Build Flavors

| Flavor | Min SDK | Description |
|--------|---------|-------------|
| standard | 30 | Full features, modern Android |
| low | 23 | Compatibility mode, legacy devices |

## 🧪 Testing

### Run Unit Tests
```bash
# All unit tests
./gradlew :app:testStandardDebugUnitTest

# Specific test class
./gradlew :app:testStandardDebugUnitTest --tests "com.cline.app.*"
```

### Run Instrumentation Tests
```bash
# Connect a device or start an emulator
./gradlew :app:connectedStandardDebugAndroidTest
```

### Run Lint
```bash
./gradlew :app:lintStandardDebug
```

## 📦 Project Structure

```
cline-android/
├── .github/
│   └── workflows/              # CI/CD workflows
│       ├── build.yml           # Build pipeline
│       └── test.yml            # Test pipeline
│
├── app/
│   ├── src/main/
│   │   ├── java/com/cline/app/
│   │   │   ├── ClineApp.kt         # Application class
│   │   │   │
│   │   │   ├── core/               # Core logic
│   │   │   │   ├── ClineController.kt    # Main controller
│   │   │   │   ├── ConfigStore.kt        # Configuration
│   │   │   │   ├── ContainerRuntime.kt   # Container management
│   │   │   │   ├── ProotBootstrap.kt     # proot management
│   │   │   │   ├── RuntimeHostPorts.kt   # Port management
│   │   │   │   ├── TerminalProcess.kt    # Terminal process
│   │   │   │   └── WebProcessManager.kt  # Web UI process
│   │   │   │
│   │   │   ├── data/               # Data layer
│   │   │   │   ├── KeyVault.kt           # API key encryption
│   │   │   │   └── repositories/        # Data repositories
│   │   │   │       ├── BackupRepository.kt
│   │   │   │       └── PluginRepository.kt
│   │   │   │
│   │   │   ├── design/              # Design system
│   │   │   │   ├── tokens/            # Design tokens
│   │   │   │   │   └── DesignTokens.kt
│   │   │   │   ├── theme/             # Theme system
│   │   │   │   │   └── Theme.kt
│   │   │   │   ├── components/        # Reusable components
│   │   │   │   │   ├── Buttons.kt
│   │   │   │   │   ├── Cards.kt
│   │   │   │   │   ├── Dialogs.kt
│   │   │   │   │   ├── Inputs.kt
│   │   │   │   │   └── Navigation.kt
│   │   │   │   └── animations/        # Animation utilities
│   │   │   │       └── Animations.kt
│   │   │   │
│   │   │   ├── bridge/              # Bridge layer
│   │   │   │   ├── AdbBridge.kt
│   │   │   │   ├── AppBridge.kt
│   │   │   │   └── LocalNetworkAccess.kt
│   │   │   │
│   │   │   ├── recovery/            # Recovery layer
│   │   │   │   ├── RecoveryController.kt
│   │   │   │   ├── RecoveryRuntime.kt
│   │   │   │   └── RecoveryService.kt
│   │   │   │
│   │   │   ├── ui/                  # UI layer
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── activities/        # Activities
│   │   │   │   │   ├── BackupActivity.kt
│   │   │   │   │   ├── ChatActivity.kt
│   │   │   │   │   ├── ChatDetailActivity.kt
│   │   │   │   │   ├── ExtractActivity.kt
│   │   │   │   │   ├── PluginDetailActivity.kt
│   │   │   │   │   ├── PluginInstallActivity.kt
│   │   │   │   │   ├── PluginsActivity.kt
│   │   │   │   │   ├── RecoveryActivity.kt
│   │   │   │   │   ├── SettingsActivity.kt
│   │   │   │   │   └── TerminalActivity.kt
│   │   │   │   │
│   │   │   │   ├── navigation/        # Navigation
│   │   │   │   │   └── Navigation.kt
│   │   │   │   │
│   │   │   │   ├── screens/            # Compose screens
│   │   │   │   │   ├── BackupScreen.kt
│   │   │   │   │   ├── ChatScreen.kt
│   │   │   │   │   ├── MainScreen.kt
│   │   │   │   │   ├── PluginsScreen.kt
│   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   ├── TerminalScreen.kt
│   │   │   │   │   └── WelcomeScreen.kt
│   │   │   │   │
│   │   │   │   ├── adapters/          # Legacy adapters
│   │   │   │   │   └── PluginListAdapter.kt
│   │   │   │   │
│   │   │   │   └── fragments/          # Legacy fragments
│   │   │   │       ├── ChatFragment.kt
│   │   │   │       ├── PluginsFragment.kt
│   │   │   │       └── TerminalFragment.kt
│   │   │   │
│   │   │   └── util/                 # Utilities
│   │   │       ├── Constants.kt
│   │   │       ├── ForegroundActivity.kt
│   │   │       ├── ShellExecutor.kt
│   │   │       └── SystemLanguage.kt
│   │   │
│   │   ├── res/                     # Resources
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── dimens.xml
│   │   │   │   ├── strings.xml
│   │   │   │   ├── styles.xml
│   │   │   │   └── themes.xml
│   │   │   ├── drawable/
│   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   ├── ic_launcher_foreground.xml
│   │   │   │   ├── ic_launcher_round.xml
│   │   │   │   ├── shape_rounded_corner_large.xml
│   │   │   │   ├── shape_rounded_corner_medium.xml
│   │   │   │   └── shape_rounded_corner_small.xml
│   │   │   ├── layout/
│   │   │   │   └── item_plugin.xml
│   │   │   └── mipmap-*/
│   │   │       ├── ic_launcher.png
│   │   │       └── ic_launcher_round.png
│   │   │
│   │   └── assets/
│   │       ├── cline-runtime.bin
│   │       ├── offline-rootfs.bin
│   │       └── recovery-agent.js
│   │
│   ├── build.gradle
│   └── proguard-rules.pro
│
├── tools/                            # Build tools
│   ├── build-cline-runtime.py      # Build Cline runtime
│   ├── build-rootfs.py              # Build Ubuntu rootfs
│   ├── prepare-assets.py            # Prepare APK assets
│   ├── prepare-recovery-assets.py  # Prepare recovery assets
│   ├── prepare-web-compat.mjs      # Web compatibility
│   ├── cline-runtime/              # Runtime files
│   ├── recovery-runtime/           # Recovery runtime
│   │   └── lock.json
│   └── web-compat/                  # Web compatibility
│       ├── entry.cjs
│       └── package.json
│
├── .gitignore
├── build.gradle                    # Root build
├── build.sh                        # Main build script
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── README.md
```

## 📚 Documentation

- [Architecture Guide](docs/ARCHITECTURE.md) - Detailed architecture overview
- [Development Guide](docs/DEVELOPMENT.md) - Development workflow and best practices
- [Build Guide](docs/BUILD.md) - Building from source
- [Testing Guide](docs/TESTING.md) - Testing strategies and commands
- [Contributing Guide](docs/CONTRIBUTING.md) - How to contribute
- [Security Model](docs/SECURITY.md) - Security considerations
- [API Documentation](docs/API.md) - API reference

## 🔒 Security

### Security Features
- ✅ **API Key Encryption**: All API keys encrypted using Android Keystore (AES-GCM)
- ✅ **Sandboxed Runtime**: Each session runs in isolated proot container
- ✅ **Network Isolation**: LAN access only by default
- ✅ **File Access Restrictions**: App-specific directories only
- ✅ **Permission Requests**: Only necessary permissions requested
- ✅ **Backup Encryption**: All backups are encrypted
- ✅ **Plugin Sandboxing**: Plugins run with limited permissions

### Permissions
The app requests the following permissions:

| Permission | Purpose |
|-----------|---------|
| `INTERNET` | API calls, updates |
| `READ_EXTERNAL_STORAGE` | File access |
| `WRITE_EXTERNAL_STORAGE` | File operations |
| `FOREGROUND_SERVICE` | Background operations |
| `LOCAL_NETWORK` | Local network access |

## 📊 Performance Optimizations

| Technique | Implementation | Impact |
|-----------|----------------|--------|
| Lazy Load Runtime | Load on first use | Faster app start |
| Pre-extracted Assets | Extract at install time | Faster first launch |
| Asset Compression | Use .bin instead of .tar.gz | Smaller APK |
| Parallel Initialization | Start web UI while loading runtime | Better UX |
| Memory Management | Monitor and cleanup processes | Prevent crashes |
| Background Execution | Use WorkManager for backups | Reliable operations |
| Caching | Cache plugin list, language files | Faster subsequent loads |

## 🎯 Feature Roadmap

| Version | Timeline | Features |
|---------|----------|----------|
| v1.0.0 | 4-6 weeks | MVP: Core runtime, basic UI, backup/restore |
| v1.1.0 | 2-3 weeks | Plugin system, ADB bridge, multi-language |
| v1.2.0 | 2-3 weeks | Advanced terminal, web UI enhancements |
| v1.3.0 | 2-3 weeks | Cloud sync, custom themes, widgets |
| v1.4.0 | 2-3 weeks | Voice input, camera access, file browser |
| v2.0.0 | 4-6 weeks | Multi-tab support, project management |

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](docs/CONTRIBUTING.md) for details.

### Quick Start for Contributors
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Make your changes
4. Run tests (`./gradlew :app:testStandardDebugUnitTest`)
5. Commit your changes (`git commit -m 'Add some feature'`)
6. Push to the branch (`git push origin feature/your-feature`)
7. Open a Pull Request

### Code Style
- Use Kotlin idiomatic style
- Follow Material Design 3 principles
- Use Compose for all new UI
- Use coroutines for async operations
- Keep commits atomic and focused

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Cline AI](https://cline.ai) - The AI coding agent that powers this app
- [Material Design 3](https://m3.material.io/) - Design system inspiration
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI framework
- [proot](https://proot-me.github.io/) - User-space chroot for runtime
- All contributors and supporters

---

**Cline Android** - Your AI coding companion on Android

Built with ❤️ and Kotlin
