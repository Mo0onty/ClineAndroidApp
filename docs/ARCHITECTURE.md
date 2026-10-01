# Cline Android Architecture

## 🏗️ Overview

Cline Android follows a **layered architecture** pattern with **Material Design 3 Expressive** principles at its core. The architecture is designed for:

- **Maintainability**: Clear separation of concerns
- **Testability**: Easy to test each layer independently
- **Extensibility**: Easy to add new features
- **Performance**: Optimized for modern Android devices
- **Security**: Secure by design with encryption and sandboxing

## 🎯 Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                         UI Layer                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Activities  │  │  Screens    │  │   Components        │  │
│  │             │  │ (Compose)   │  │ (Buttons, Cards, etc)│  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                    ▲
                    │
┌─────────────────────────────────────────────────────────────┐
│                      Design System                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Tokens     │  │   Theme     │  │   Animations        │  │
│  │ (Colors,    │  │ (Light/Dark)│  │ (Spring, Transitions)│  │
│  │  Spacing,   │  │             │  │                     │  │
│  │  Typography)│  │             │  │                     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                    ▲
                    │
┌─────────────────────────────────────────────────────────────┐
│                       Core Layer                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Cline      │  │  Runtime    │  │   Configuration      │  │
│  │  Controller │  │  Manager   │  │   & Environment      │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                    ▲
                    │
┌─────────────────────────────────────────────────────────────┐
│                      Data Layer                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  KeyVault   │  │  Backup     │  │   Repositories       │  │
│  │ (Encryption) │  │  Manager    │  │ (Plugin, Backup)      │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                    ▲
                    │
┌─────────────────────────────────────────────────────────────┐
│                     Bridge Layer                              │
│  ┌─────────────────────┐  ┌─────────────────────┐              │
│  │  LocalNetworkAccess  │  │   AppBridge          │              │
│  │ (Network connectivity)│  │ (App operations)    │              │
│  └─────────────────────┘  └─────────────────────┘              │
│                                                          │
│  ┌─────────────────────┐                                     │
│  │  AdbBridge           │                                     │
│  │ (ADB communication) │                                     │
│  └─────────────────────┘                                     │
└─────────────────────────────────────────────────────────────┘
                    ▲
                    │
┌─────────────────────────────────────────────────────────────┐
│                     Recovery Layer                            │
│  ┌─────────────────────┐  ┌─────────────────────┐              │
│  │  RecoveryController  │  │   RecoveryRuntime    │              │
│  │ (Orchestration)      │  │ (Safe environment)   │              │
│  └─────────────────────┘  └─────────────────────┘              │
│                                                          │
│  ┌─────────────────────┐                                     │
│  │  RecoveryService     │                                     │
│  │ (Background service) │                                     │
│  └─────────────────────┘                                     │
└─────────────────────────────────────────────────────────────┘
                    ▲
                    │
┌─────────────────────────────────────────────────────────────┐
│                     Utilities                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ Constants   │  │ Shell        │  │   System            │  │
│  │             │  │ Executor     │  │   Language          │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 📦 Module Structure

The project uses a **single module** structure with **product flavors**:

```
cline-android/
├── app/                          # Single Android module
│   ├── src/main/                 # Main source set
│   │   ├── java/com/cline/app/   # All Kotlin code
│   │   └── res/                 # All resources
│   └── build.gradle              # App-level build config
└── build.gradle                  # Root build config
```

### Product Flavors

| Flavor | Min SDK | Description | Features |
|--------|---------|-------------|----------|
| `standard` | 30 | Full features | All features enabled |
| `low` | 23 | Compatibility | Limited features for older devices |

### Build Types

| Type | Description | Configuration |
|------|-------------|---------------|
| `debug` | Development | Debuggable, logging enabled |
| `release` | Production | Minified, signed, optimized |

## 🎨 Design System Architecture

### Design Tokens

The **DesignTokens** object provides a centralized, type-safe way to access all design values:

```kotlin
object DesignTokens {
    object Colors {
        val Primary = Color(0xFF00D4AA)      // Cline AI green
        val Secondary = Color(0xFF00B8A3)    // Teal/blue-green
        val Tertiary = Color(0xFF00E5B8)     // Lighter green
        val Surface = Color(0xFF1A1C1E)      // Dark surface
        val Background = Color(0xFF121416)   // Darker background
    }
    
    object Typography {
        val DisplayLarge = TextStyle(...)
        val HeadlineLarge = TextStyle(...)
        val TitleLarge = TextStyle(...)
        val BodyLarge = TextStyle(...)
        val LabelLarge = TextStyle(...)
    }
    
    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 16.dp
        val lg = 24.dp
        val xl = 32.dp
        val xxl = 48.dp
    }
    
    object Shape {
        val None = 0.dp
        val Small = 4.dp
        val Medium = 8.dp
        val Large = 12.dp
        val Full = 16.dp
    }
    
    object Elevation {
        val Level0 = 0.dp
        val Level1 = 1.dp
        val Level2 = 3.dp
        val Level3 = 6.dp
        val Level4 = 8.dp
        val Level5 = 12.dp
    }
    
    object Motion {
        val Standard = 300.ms
        val Extended = 400.ms
        val Short = 200.ms
        val Spring = SpringSpec(...)
    }
}
```

### Theme System

The **ClineTheme** composable provides Material Design 3 theming with dynamic color support:

```kotlin
@Composable
fun ClineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = ClineTypography,
        shapes = ClineShapes,
        content = content
    )
}
```

### Custom Components

All custom components follow Material Design 3 guidelines:

- **Buttons**: Primary, Secondary, Icon, FAB, Segmented
- **Cards**: Standard, Elevated, Outlined, ChatBubble, Terminal, Plugin, Settings, Backup
- **Inputs**: TextField, SearchField, PasswordField, CodeEditor, ChatInput, TerminalInput
- **Dialogs**: Alert, Info, Warning, Error, Input, BottomSheet, ActionBottomSheet, Loading, Toast, Snackbar
- **Navigation**: BottomNavigationBar, NavigationRail, TopAppBar, Scaffold, TabRow

### Animation System

Spring-based animations for expressive UI:

```kotlin
object Animations {
    fun springSpec(
        dampingRatio: Float = Spring.DampingRatioMediumBouncy,
        stiffness: Float = Spring.StiffnessMedium
    ): SpringSpec<Float> = SpringSpec(dampingRatio, stiffness)
    
    fun slideInFromStart(): EnterTransition = slideIntoContainer(...)
    fun slideInFromEnd(): EnterTransition = slideIntoContainer(...)
    fun fadeIn(): EnterTransition = fadeIn(...)
    fun slideOutToStart(): ExitTransition = slideOutOfContainer(...)
    fun slideOutToEnd(): ExitTransition = slideOutOfContainer(...)
    fun fadeOut(): ExitTransition = fadeOut(...)
}
```

## ⚙️ Core Layer Architecture

### ClineController

The **ClineController** is the main orchestration class that coordinates all major subsystems:

```kotlin
class ClineController(
    private val context: Context,
    private val configStore: ConfigStore,
    private val runtimeHostPorts: RuntimeHostPorts,
    private val prootBootstrap: ProotBootstrap,
    private val containerRuntime: ContainerRuntime,
    private val webProcessManager: WebProcessManager,
    private val environmentAccess: EnvironmentAccess,
    private val keyVault: KeyVault
) {
    fun start()
    fun stop()
    fun restart()
    fun getStatus(): RuntimeStatus
    fun getWebUrl(): String
    fun openWebUi()
}
```

### Runtime Management

#### ProotBootstrap
Manages the proot binary:
- Extract proot from assets
- Validate proot binary
- Check proot availability

#### ContainerRuntime
Manages the container environment:
- Extract rootfs
- Set up container directories
- Configure environment variables
- Manage container lifecycle

#### WebProcessManager
Manages the Web UI process:
- Start/stop web process
- Monitor process health
- Manage PID files
- Handle process lifecycle

#### TerminalProcess
Manages terminal sessions:
- Create PTY sessions
- Handle terminal I/O
- Manage terminal lifecycle
- Support multiple terminal sessions

### Configuration Management

#### ConfigStore
Persistent configuration with encrypted storage:
- Dark mode preference
- Dynamic color preference
- Terminal font size
- Selected AI model
- Plugin enable/disable
- API key (encrypted)

#### KeyVault
API key encryption using Android Keystore:
- AES-GCM encryption
- Secure key generation
- Biometric authentication support (future)

## 💾 Data Layer Architecture

### BackupManager
Handles backup and restore operations:
- Create backups
- Restore from backups
- List available backups
- Delete backups
- Backup metadata management

### Repositories

#### PluginRepository
Manages plugins:
- Install/uninstall plugins
- Enable/disable plugins
- List installed plugins
- Get plugin metadata
- Check plugin state

#### BackupRepository
Manages backup files:
- Create backup archives
- Extract backup archives
- List backup files
- Delete backup files
- Get backup metadata

## 🔌 Bridge Layer Architecture

### LocalNetworkAccess
Manages local network connectivity:
- Check local network availability
- Request local network permission
- Monitor network state
- Get network type

### AppBridge
Handles app-level operations:
- Open URLs
- Share text/files
- Send broadcasts
- Start activities
- Get app version info

### AdbBridge
Manages ADB communication:
- Execute shell commands
- Execute root commands
- Execute commands in proot
- Check root access
- Get device info via ADB

## 🛠️ Recovery Layer Architecture

### RecoveryController
Orchestrates recovery operations:
- Enter/exit recovery mode
- Perform recovery backup
- Perform recovery restore
- Verify runtime integrity
- Repair runtime environment
- Get recovery status

### RecoveryRuntime
Provides safe runtime for recovery:
- Initialize recovery environment
- Start/stop recovery runtime
- Execute commands in recovery
- Get runtime configuration

### RecoveryService
Background service for recovery:
- Start/stop recovery operations
- Handle long-running tasks
- Foreground service with notification
- Cleanup resources

## 🧩 Plugin System Architecture

### Plugin Structure
```
plugins/
├── plugin-id/
│   ├── manifest.json          # Plugin metadata
│   ├── icon.png              # Plugin icon
│   ├── index.js              # Plugin entry point
│   └── ...                   # Plugin files
└── plugin-manifest.json      # All plugins index
```

### Plugin Manifest
```json
{
  "id": "plugin-id",
  "name": "Plugin Name",
  "version": "1.0.0",
  "description": "Plugin description",
  "author": "Author Name",
  "permissions": ["network", "storage"],
  "dependencies": ["dep1", "dep2"],
  "enabled": true
}
```

### Plugin Lifecycle
1. **Install**: Download and extract plugin archive
2. **Enable**: Mark plugin as enabled in manifest
3. **Load**: Load plugin into runtime
4. **Execute**: Run plugin code
5. **Disable**: Mark plugin as disabled
6. **Uninstall**: Remove plugin files

## 📡 Network Architecture

### Port Management
```kotlin
class RuntimeHostPorts {
    var webPort: Int = 8080
    var apiPort: Int = 8081
    var terminalPort: Int = 8022
    
    fun isValidPort(port: Int): Boolean
    fun hasPortConflict(): Boolean
    fun markPortAsUsed(port: Int)
    fun isPortInUse(port: Int): Boolean
}
```

### Network Isolation
- **LAN Access Only**: By default, only local network access
- **No Internet**: Can be configured for offline-only mode
- **Local Network Permission**: Required for LAN access
- **Firewall Rules**: Custom firewall rules (future)

## 🔒 Security Architecture

### Encryption
- **API Keys**: AES-GCM via Android Keystore
- **Backups**: Encrypted backup files
- **Configuration**: Sensitive config encrypted

### Sandboxing
- **Proot**: User-space chroot for isolation
- **Container**: Each session in separate container
- **Plugins**: Limited permissions for plugins

### Permissions
- **Minimal**: Only necessary permissions requested
- **Runtime**: Requested at runtime when needed
- **Background**: Foreground service for background tasks

## 📊 Performance Architecture

### Lazy Loading
- Runtime loaded on first use
- Assets extracted at install time
- Plugins loaded on demand

### Caching
- Plugin list cached
- Language files cached
- Configuration cached

### Memory Management
- Process monitoring
- Cleanup on low memory
- Resource limits

### Parallel Execution
- Background tasks via WorkManager
- Concurrent operations
- Async I/O

## 🗃️ File System Architecture

### Directory Structure
```
/data/data/com.cline.app/
├── files/
│   ├── cline/                  # Root Cline directory
│   │   ├── runtime/            # Runtime environment
│   │   │   ├── rootfs/         # Ubuntu root filesystem
│   │   │   ├── bin/            # Binaries
│   │   │   ├── lib/            # Libraries
│   │   │   └── ...
│   │   ├── assets/             # Bundled assets
│   │   ├── backups/            # Backup files
│   │   ├── cache/              # Cache files
│   │   ├── data/               # Data files
│   │   ├── plugins/            # Plugin files
│   │   └── temp/               # Temporary files
│   └── ...
└── cache/                     # Android cache
```

### Asset Files
- `offline-rootfs.bin`: Ubuntu 24.04 root filesystem (~100MB)
- `cline-runtime.bin`: Cline runtime package (~50MB)
- `recovery-agent.js`: Recovery agent script

## 🎯 Navigation Architecture

### Screen Hierarchy
```
WelcomeScreen
    ↓
MainScreen (Bottom Navigation)
    ├── HomeScreen
    ├── ChatScreen
    │   └── ChatDetailScreen
    ├── TerminalScreen
    ├── PluginsScreen
    │   └── PluginDetailScreen
    │       └── PluginInstallScreen
    └── SettingsScreen
        └── BackupScreen
            └── ExtractScreen
                
RecoveryScreen (Separate)
```

### Navigation Graph
```kotlin
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Main : Screen("main")
    object Chat : Screen("chat")
    object Terminal : Screen("terminal")
    object Plugins : Screen("plugins")
    object Settings : Screen("settings")
    object Backup : Screen("backup")
}

sealed class LeafScreen(val route: String) {
    object ChatDetail : LeafScreen("chat/{chatId}")
    object PluginDetail : LeafScreen("plugin/{pluginId}")
}
```

## 🛠️ Build System Architecture

### Gradle Configuration
- **Root**: `build.gradle` - Project-level config
- **App**: `app/build.gradle` - App-level config
- **Properties**: `gradle.properties` - Global properties

### Build Scripts
- `build.sh`: Main build script
- `tools/build-rootfs.py`: Build Ubuntu rootfs
- `tools/build-cline-runtime.py`: Build Cline runtime
- `tools/prepare-assets.py`: Prepare APK assets
- `tools/prepare-recovery-assets.py`: Prepare recovery assets

### CI/CD Pipeline
- **GitHub Actions**: Automated builds and tests
- **Matrix Builds**: Multiple flavors and types
- **Artifacts**: APK, assets, documentation
- **Caching**: Gradle dependency caching

## 📞 Communication Architecture

### Inter-Process Communication
- **PID Files**: Track running processes
- **Socket Communication**: Between web UI and runtime
- **Broadcast Intents**: App-wide notifications
- **File System**: Shared files for data exchange

### Web UI Communication
- **HTTP API**: REST API for web UI
- **WebSocket**: Real-time communication
- **Shared Storage**: LocalStorage for shared state

### Terminal Communication
- **PTY**: Pseudo-terminal for terminal I/O
- **Process Streams**: stdin/stdout/stderr
- **Signal Handling**: Process control

## 🎨 UI Architecture

### Compose Structure
```kotlin
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    Column {
        ChatHeader()
        ChatMessagesList()
        ChatInputArea()
    }
}

@Composable
fun ChatMessage(message: ChatMessage) {
    Row {
        Avatar()
        MessageBubble()
    }
}
```

### State Management
- **ViewModel**: Business logic and state
- **remember**: Compose state holder
- **mutableStateOf**: Mutable state
- **LaunchedEffect**: Side effects
- **Flow**: Reactive data streams

### Animation
- **Spring**: Bouncy, expressive animations
- **tween**: Smooth transitions
- **Crossfade**: Content transitions
- **AnimatedContent**: Size animations

## 📊 Monitoring and Analytics

### Logging
- **Tag-based**: Consistent logging tags
- **Level-based**: DEBUG, INFO, WARN, ERROR
- **File Logging**: Optional file logging

### Metrics
- **Performance**: Operation timing
- **Memory**: Memory usage tracking
- **Errors**: Error tracking and reporting

### Crash Reporting
- **Uncaught Exceptions**: Automatic reporting
- **ANR Detection**: Application Not Responding detection
- **Custom Events**: Manual event logging

## 🚀 Deployment Architecture

### Release Process
1. **Build**: Create signed release APK
2. **Test**: Run all tests
3. **Verify**: Verify APK integrity
4. **Upload**: Upload to Google Play Store
5. **Deploy**: Release to production

### Version Management
- **Semantic Versioning**: MAJOR.MINOR.PATCH
- **Version Code**: Incremental build number
- **Version Name**: Human-readable version

### Signing
- **Debug**: Automatic debug keystore
- **Release**: Configured release keystore
- **V1/V2/V3**: Multiple signing schemes

## 📚 Best Practices

### Code Organization
- **Single Responsibility**: Each class has one purpose
- **Layer Separation**: Keep layers independent
- **Dependency Injection**: Manual DI via constructor injection
- **Interface Segregation**: Small, focused interfaces

### Performance
- **Lazy Initialization**: Defer heavy operations
- **Caching**: Cache expensive operations
- **Background Work**: Use coroutines and WorkManager
- **Memory Management**: Clean up unused resources

### Security
- **Least Privilege**: Request only necessary permissions
- **Data Encryption**: Encrypt sensitive data
- **Input Validation**: Validate all inputs
- **Error Handling**: Graceful error handling

### Testing
- **Unit Tests**: Test individual functions
- **UI Tests**: Test Composable functions
- **Integration Tests**: Test component interactions
- **Instrumentation Tests**: Test on real devices

### Documentation
- **Code Comments**: Explain why, not what
- **README**: Clear setup instructions
- **ARCHITECTURE**: Design decisions
- **API Docs**: Public API documentation

## 🔗 External Dependencies

### AndroidX
- `androidx.compose:compose-bom:2024.02.00` - Compose BOM
- `androidx.compose.material3:material3` - Material Design 3
- `androidx.navigation:navigation-compose` - Compose Navigation
- `androidx.lifecycle:lifecycle-viewmodel-compose` - Compose ViewModel
- `androidx.webkit:webkit` - WebView support

### Kotlin
- `org.jetbrains.kotlin.android` - Kotlin Android plugin
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` - Coroutines
- `org.jetbrains.kotlinx:kotlinx-serialization-json` - JSON serialization

### Third-Party
- `com.google.code.gson:gson` - JSON parsing
- `org.bouncycastle:bcprov-jdk15to18` - Cryptography
- `com.termux.termux-app:terminal-view` - Terminal emulation
- `dev.rikka.shizuku:api` - Shizuku integration (standard flavor)
- `org.mozilla.geckoview:geckoview-arm64-v8a` - GeckoView (low flavor)

## 📖 Glossary

| Term | Definition |
|------|------------|
| **proot** | User-space chroot implementation for Linux |
| **PTY** | Pseudo-terminal for terminal I/O |
| **MD3** | Material Design 3 |
| **Compose** | Jetpack Compose UI framework |
| **Coroutines** | Kotlin's async programming model |
| **ViewModel** | Business logic holder for UI |
| **Flow** | Kotlin's reactive data stream |
| **Keystore** | Android's secure key storage |
| **AES-GCM** | Advanced Encryption Standard - Galois/Counter Mode |
| **Flavor** | Product flavor for different build variants |
| **ABI** | Application Binary Interface (arm64-v8a) |
| **NDK** | Native Development Kit |
| **APK** | Android Package Kit |

## 📞 Support

For questions, issues, or contributions:
- **Issues**: [GitHub Issues](https://github.com/your-org/cline-android/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-org/cline-android/discussions)
- **Email**: support@cline.ai

---

*Last updated: $(date)*
