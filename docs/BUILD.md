# Building Cline Android

This guide covers building Cline Android from source.

## 📋 Table of Contents

- [Prerequisites](#-prerequisites)
- [Setup](#-setup)
- [Building](#-building)
- [Build Script](#-build-script)
- [Build Flavors](#-build-flavors)
- [Build Types](#-build-types)
- [Assets](#-assets)
- [Troubleshooting](#-troubleshooting)

## 📋 Prerequisites

### Required Tools

| Tool | Version | Purpose |
|------|---------|---------|
| JDK | 17 | Java Development Kit |
| Android SDK | API 37 | Android development |
| Android NDK | 26 | Native development |
| Python | 3.9+ | Asset preparation |
| Gradle | 9.3.1 | Build system |
| AGP | 9.1.1 | Android Gradle Plugin |

### System Requirements

| OS | Requirements |
|----|--------------|
| Linux | Ubuntu 20.04+, Debian 10+, Fedora 36+ |
| macOS | 10.15+, Xcode 13+ |
| Windows | 10+, WSL2 recommended |

### Disk Space
- **Minimum**: 20 GB free space
- **Recommended**: 50 GB free space (for all assets and builds)

### Memory
- **Minimum**: 8 GB RAM
- **Recommended**: 16 GB RAM (for smooth builds)

## 🛠️ Setup

### 1. Install JDK 17

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk
```

#### macOS (Homebrew)
```bash
brew install openjdk@17
```

#### Windows
Download from [Adoptium](https://adoptium.net/temurin/releases/?version=17) or [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).

Verify installation:
```bash
java -version
# Should output: openjdk version "17"...
```

### 2. Install Android SDK and NDK

#### Option A: Android Studio (Recommended)
1. Download and install [Android Studio](https://developer.android.com/studio)
2. Open SDK Manager
3. Install:
   - Android SDK: API 37 (Android 14)
   - Android SDK: API 30 (Android 11)
   - Android SDK: API 23 (Android 6.0)
   - Android NDK: Version 26
   - Build Tools: 34.0.0+
   - CMake: 3.22.1+
   - Android Emulator

#### Option B: Command Line
```bash
# Set ANDROID_SDK_ROOT
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
export ANDROID_NDK_ROOT=$ANDROID_SDK_ROOT/ndk/26.3.11579264

# Install SDK packages
sdkmanager "platforms;android-37"
sdkmanager "platforms;android-30"
sdkmanager "platforms;android-23"
sdkmanager "build-tools;34.0.0"
sdkmanager "ndk;26.3.11579264"
sdkmanager "cmake;3.22.1"
```

Verify installation:
```bash
sdkmanager --list
ndk-build --version
```

### 3. Install Python 3.9+

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install -y python3 python3-pip python3-venv
```

#### macOS (Homebrew)
```bash
brew install python@3.9
```

#### Windows
Download from [Python.org](https://www.python.org/downloads/).

Verify installation:
```bash
python3 --version
# Should output: Python 3.9.x or higher
pip3 --version
```

### 4. Clone the Repository

```bash
git clone https://github.com/your-org/cline-android.git
cd cline-android
```

### 5. Configure Local Properties

Create `local.properties` file in the project root:

```properties
# Android SDK path
sdk.dir=/path/to/android/sdk

# Android NDK path (optional, will use default if not specified)
ndk.dir=/path/to/android/ndk

# Custom Gradle properties (optional)
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

Or use environment variables:
```bash
export ANDROID_SDK_ROOT=/path/to/android/sdk
export ANDROID_NDK_ROOT=/path/to/android/ndk
```

### 6. Set Environment Variables

Add to your shell configuration file (`~/.bashrc`, `~/.zshrc`, or `~/.profile`):

```bash
# Java
export JAVA_HOME=/path/to/jdk-17
export PATH=$JAVA_HOME/bin:$PATH

# Android
export ANDROID_SDK_ROOT=/path/to/android/sdk
export ANDROID_NDK_ROOT=/path/to/android/ndk
export ANDROID_HOME=$ANDROID_SDK_ROOT
export PATH=$ANDROID_SDK_ROOT/platform-tools:$PATH

# Python (optional)
export CLINE_PYTHON=python3

# Gradle (optional)
export GRADLE_OPTS="-Xmx4096m -Dorg.gradle.daemon=true"
```

Then reload your shell:
```bash
source ~/.bashrc  # or ~/.zshrc
```

## 🏗️ Building

### Quick Build

```bash
# Make gradlew executable
chmod +x gradlew

# Build debug APK (standard flavor)
./gradlew :app:assembleStandardDebug

# APK will be at: app/build/outputs/apk/standard/debug/app-standard-debug.apk
```

### Build All Variants

```bash
# Build all debug variants
./gradlew :app:assembleDebug

# Build all release variants
./gradlew :app:assembleRelease

# Build specific variant
./gradlew :app:assembleStandardDebug
./gradlew :app:assembleStandardRelease
./gradlew :app:assembleLowDebug
./gradlew :app:assembleLowRelease
```

### Build with Tests

```bash
# Run unit tests and build
./gradlew :app:testStandardDebugUnitTest :app:assembleStandardDebug

# Run all tests
./gradlew :app:test :app:assembleDebug
```

### Clean Build

```bash
# Clean all builds
./gradlew clean

# Clean and rebuild
./gradlew clean :app:assembleStandardDebug

# Clean specific variant
./gradlew clean :app:assembleStandardDebug
```

## 📜 Build Script

The `build.sh` script provides a convenient way to build the project:

### Usage

```bash
./build.sh [target] [flavor] [build_type]
```

### Options

| Argument | Description | Options | Default |
|----------|-------------|---------|---------|
| target | Build target | rootfs, runtime, assets, apk, all | all |
| flavor | Product flavor | standard, low | standard |
| build_type | Build type | debug, release | debug |

### Examples

```bash
# Build everything (rootfs, runtime, assets, APK)
./build.sh all standard debug

# Build only rootfs
./build.sh rootfs

# Build only runtime
./build.sh runtime

# Build only assets
./build.sh assets

# Build APK only
./build.sh apk standard debug

# Build release APK
./build.sh apk standard release

# Build low flavor debug APK
./build.sh apk low debug
```

### What Each Target Does

| Target | Description |
|--------|-------------|
| `rootfs` | Build Ubuntu 24.04 root filesystem using debootstrap |
| `runtime` | Build Cline runtime package with Node.js and Cline agent |
| `assets` | Prepare all assets for APK (copy and process) |
| `apk` | Build Android APK using Gradle |
| `all` | Run all targets in sequence |

### Environment Variables

The build script uses the following environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `JAVA_HOME` | JDK path | Required |
| `ANDROID_SDK_ROOT` | Android SDK path | Required |
| `ANDROID_NDK_ROOT` | Android NDK path | Auto-detected |
| `CLINE_PYTHON` | Python command | `python3` |

## 🎯 Build Flavors

### Standard Flavor

- **Min SDK**: 30 (Android 11)
- **Target SDK**: 37 (Android 14)
- **Features**: All features enabled
- **Dependencies**: Full dependencies
- **Use Case**: Modern Android devices

Build commands:
```bash
# Debug
./gradlew :app:assembleStandardDebug
./build.sh apk standard debug

# Release
./gradlew :app:assembleStandardRelease
./build.sh apk standard release
```

### Low Flavor

- **Min SDK**: 23 (Android 6.0)
- **Target SDK**: 37 (Android 14)
- **Features**: Limited features for compatibility
- **Dependencies**: Compatibility versions
- **Use Case**: Older Android devices

Build commands:
```bash
# Debug
./gradlew :app:assembleLowDebug
./build.sh apk low debug

# Release
./gradlew :app:assembleLowRelease
./build.sh apk low release
```

### Flavor Differences

| Feature | Standard | Low |
|---------|----------|-----|
| Min SDK | 30 | 23 |
| GeckoView | No | Yes |
| Shizuku | Latest | Older version |
| Full Features | ✅ | ⚠️ Limited |
| Performance | ✅ Optimized | ✅ Compatible |

## 📦 Build Types

### Debug

- **Debuggable**: Yes
- **Logging**: Verbose
- **Optimization**: None
- **Signing**: Debug keystore
- **Use Case**: Development and testing

Build commands:
```bash
./gradlew :app:assembleStandardDebug
./build.sh apk standard debug
```

### Release

- **Debuggable**: No
- **Logging**: Minimal
- **Optimization**: Full (ProGuard/R8)
- **Signing**: Release keystore
- **Use Case**: Production distribution

Build commands:
```bash
./gradlew :app:assembleStandardRelease
./build.sh apk standard release
```

### Signing Configuration

#### Debug Signing
Automatic debug keystore is used for debug builds.

#### Release Signing
Configure in `app/build.gradle`:

```gradle
signingConfigs {
    release {
        storeFile System.getenv("CLINE_KEYSTORE") ?: file('../cline-release.keystore')
        storePassword System.getenv("CLINE_KEYSTORE_PASSWORD") ?: 'android'
        keyAlias System.getenv("CLINE_KEY_ALIAS") ?: 'cline-release'
        keyPassword System.getenv("CLINE_KEY_PASSWORD") ?: 'android'
        
        enableV1Signing true
        enableV2Signing true
        enableV3Signing true
    }
}
```

Set environment variables:
```bash
export CLINE_KEYSTORE=/path/to/cline-release.keystore
export CLINE_KEYSTORE_PASSWORD=your_password
export CLINE_KEY_ALIAS=cline-release
export CLINE_KEY_PASSWORD=your_key_password
```

Or build with environment variables:
```bash
CLINE_KEYSTORE=/path/to/keystore \
CLINE_KEYSTORE_PASSWORD=password \
CLINE_KEY_ALIAS=alias \
CLINE_KEY_PASSWORD=key_password \
./gradlew :app:assembleStandardRelease
```

## 💾 Assets

### Required Assets

The following assets must be present in `app/src/main/assets/`:

| Asset | Size | Description |
|-------|------|-------------|
| `offline-rootfs.bin` | ~100 MB | Ubuntu 24.04 root filesystem |
| `cline-runtime.bin` | ~50 MB | Cline runtime package |
| `recovery-agent.js` | ~10 KB | Recovery agent script |

### Building Assets

#### Build RootFS

Requires:
- Linux system (for debootstrap)
- sudo/root access
- ~5 GB free space

```bash
# Build Ubuntu rootfs
sudo ./tools/build-rootfs.py

# Output: app/src/main/assets/offline-rootfs.bin
```

The script:
1. Creates a minimal Ubuntu 24.04 system using debootstrap
2. Installs base packages (bash, coreutils, etc.)
3. Installs Cline-specific packages (Node.js, Python, etc.)
4. Configures the environment
5. Creates a compressed archive

#### Build Cline Runtime

Requires:
- Node.js 18+
- npm/yarn/pnpm
- ~2 GB free space

```bash
# Build Cline runtime
./tools/build-cline-runtime.py

# Output: app/src/main/assets/cline-runtime.bin
```

The script:
1. Downloads Node.js binary
2. Installs Cline agent via npm
3. Installs additional dependencies
4. Creates startup scripts
5. Creates a compressed archive

#### Prepare Assets

```bash
# Prepare all assets for APK
./tools/prepare-assets.py

# Output: app/build/generated/standardAssets/
```

The script:
1. Copies assets to generated directory
2. Prepares web compatibility files
3. Prepares language files
4. Prepares plugin files
5. Generates asset report

### Asset Verification

```bash
# Check if assets exist
ls -lh app/src/main/assets/

# Verify asset sizes
du -sh app/src/main/assets/*

# Check checksums
sha256sum app/src/main/assets/*.bin
```

## 🧪 Testing

### Run Unit Tests

```bash
# All unit tests
./gradlew :app:testStandardDebugUnitTest

# Specific test class
./gradlew :app:testStandardDebugUnitTest --tests "com.cline.app.*"

# Specific test method
./gradlew :app:testStandardDebugUnitTest --tests "com.cline.app.ExampleUnitTest.addition_isCorrect"
```

### Run Instrumentation Tests

```bash
# Connect a device or start an emulator
adb devices

# Run instrumentation tests
./gradlew :app:connectedStandardDebugAndroidTest

# Run specific test
./gradlew :app:connectedStandardDebugAndroidTest --tests "com.cline.app.*"
```

### Run Lint

```bash
# Run lint
./gradlew :app:lintStandardDebug

# Run lint with HTML report
./gradlew :app:lintStandardDebug --output-file=lint-report.html

# Fix lint issues
./gradlew :app:lintStandardDebug --fix
```

### Test Coverage

```bash
# Generate test coverage report
./gradlew :app:testStandardDebugUnitTest --coverage

# Report location: app/build/reports/coverage/standard/debug/
```

## 🚀 Installation

### Install Debug APK

```bash
# Build debug APK
./gradlew :app:assembleStandardDebug

# Install on connected device
adb install app/build/outputs/apk/standard/debug/app-standard-debug.apk
```

### Install Release APK

```bash
# Build release APK
./gradlew :app:assembleStandardRelease

# Install on connected device
adb install app/build/outputs/apk/standard/release/app-standard-release.apk
```

### Uninstall

```bash
adb uninstall com.cline.app
```

## 📊 Build Information

### Version Management

Version is defined in `app/build.gradle`:

```gradle
android {
    defaultConfig {
        versionCode 1
        versionName "1.0.0"
    }
}
```

Update version:
```bash
# Update version name
sed -i 's/versionName "[^"]*"/versionName "1.0.1"/' app/build.gradle

# Update version code
sed -i 's/versionCode [0-9]*/versionCode 2/' app/build.gradle
```

### Build Information

Get build information:
```bash
# Get version name
./gradlew :app:printVersionName

# Get version code
./gradlew :app:printVersionCode

# Get all build info
./gradlew :app:printBuildInfo
```

### Dependencies

View dependencies:
```bash
# All dependencies
./gradlew :app:dependencies

# Dependency tree
./gradlew :app:dependencyInsight --dependency=compose

# Dependency graph
./gradlew :app:dependencies --configuration=releaseRuntimeClasspath
```

## 🐛 Troubleshooting

### Common Issues

#### 1. JDK Not Found

**Error**: `JAVA_HOME is not set and no 'java' command could be found`

**Solution**:
```bash
# Set JAVA_HOME
export JAVA_HOME=/path/to/jdk-17
export PATH=$JAVA_HOME/bin:$PATH

# Verify
java -version
```

#### 2. Android SDK Not Found

**Error**: `Android SDK not found`

**Solution**:
```bash
# Set ANDROID_SDK_ROOT
export ANDROID_SDK_ROOT=/path/to/android/sdk

# Or create local.properties
echo "sdk.dir=/path/to/android/sdk" > local.properties
```

#### 3. NDK Not Found

**Error**: `NDK not found`

**Solution**:
```bash
# Set ANDROID_NDK_ROOT
export ANDROID_NDK_ROOT=/path/to/android/ndk

# Or install via SDK Manager
sdkmanager "ndk;26.3.11579264"
```

#### 4. Gradle Daemon Issues

**Error**: `Could not stop Gradle daemon`

**Solution**:
```bash
# Stop all Gradle daemons
./gradlew --stop

# Clean Gradle cache
rm -rf ~/.gradle/caches/

# Retry build
./gradlew clean :app:assembleStandardDebug
```

#### 5. Out of Memory

**Error**: `Out of memory` or `GC overhead limit exceeded`

**Solution**:
```bash
# Increase Gradle JVM heap size
export GRADLE_OPTS="-Xmx4096m -Dorg.gradle.jvmargs=-Xmx4096m"

# Or in gradle.properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=1024m
```

#### 6. Missing Assets

**Error**: `Required file not found: offline-rootfs.bin`

**Solution**:
```bash
# Build assets first
./build.sh rootfs
./build.sh runtime
./build.sh assets
```

#### 7. Permission Denied

**Error**: `Permission denied` when building rootfs

**Solution**:
```bash
# Run with sudo
sudo ./build.sh rootfs

# Or ensure you have proper permissions
chmod +x tools/*.py
```

#### 8. Network Issues

**Error**: `Could not download` or timeout errors

**Solution**:
```bash
# Check network connection
ping google.com

# Use a mirror for Gradle
# In gradle.properties:
org.gradle.wrapper.DistributionUrl=https\://mirrors.example.com/gradle-9.3.1-bin.zip
```

### Debug Mode

Enable verbose logging:
```bash
# Debug mode
./gradlew :app:assembleStandardDebug --debug

# Stacktrace
./gradlew :app:assembleStandardDebug --stacktrace

# Info mode
./gradlew :app:assembleStandardDebug --info
```

### Clean Everything

```bash
# Clean all
./gradlew clean

# Clean Gradle cache
rm -rf ~/.gradle/caches/

# Clean Android Studio cache
rm -rf ~/.AndroidStudio*/system/caches/

# Clean project
rm -rf app/build/
rm -rf .gradle/
rm -rf build/
```

## 📚 Additional Resources

- [Android Developer Documentation](https://developer.android.com/docs)
- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose/documentation)
- [Material Design 3 Guidelines](https://m3.material.io/)

## 🙏 Support

For build-related issues:
- Check [Troubleshooting](#-troubleshooting) section
- Search [GitHub Issues](https://github.com/your-org/cline-android/issues)
- Create a new issue with:
  - Android version
  - JDK version
  - Gradle version
  - Error message
  - Steps to reproduce

---

*Last updated: $(date)*
