# Development Guide

This guide covers development workflow, best practices, and contribution guidelines for Cline Android.

## 📋 Table of Contents

- [Getting Started](#-getting-started)
- [Development Workflow](#-development-workflow)
- [Project Structure](#-project-structure)
- [Coding Guidelines](#-coding-guidelines)
- [UI Development](#-ui-development)
- [Core Development](#-core-development)
- [Testing](#-testing)
- [Debugging](#-debugging)
- [Performance Tips](#-performance-tips)
- [Contribution Guidelines](#-contribution-guidelines)

## 🚀 Getting Started

### 1. Set Up Development Environment

Follow the [Build Guide](BUILD.md) to set up your development environment.

### 2. Clone the Repository

```bash
git clone https://github.com/your-org/cline-android.git
cd cline-android
```

### 3. Build the Project

```bash
# Build debug APK
./gradlew :app:assembleStandardDebug

# Run on device
adb install app/build/outputs/apk/standard/debug/app-standard-debug.apk
```

### 4. Open in Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the `cline-android` directory
4. Wait for Gradle sync to complete

## 🔄 Development Workflow

### Typical Workflow

```bash
# 1. Create a feature branch
git checkout -b feature/your-feature

# 2. Make your changes
# (Edit files, add features, fix bugs)

# 3. Run tests
./gradlew :app:testStandardDebugUnitTest

# 4. Build and verify
./gradlew :app:assembleStandardDebug

# 5. Commit changes
git add .
git commit -m "Add your feature"

# 6. Push to remote
git push origin feature/your-feature

# 7. Create Pull Request
# (Go to GitHub and create a PR)
```

### Branch Naming Convention

| Branch Type | Prefix | Example |
|-------------|--------|---------|
| Feature | `feature/` | `feature/chat-improvements` |
| Bug Fix | `fix/` | `fix/terminal-crash` |
| Documentation | `docs/` | `docs/update-readme` |
| Refactoring | `refactor/` | `refactor/core-layer` |
| Release | `release/` | `release/v1.0.0` |
| Hotfix | `hotfix/` | `hotfix/security-issue` |

### Commit Message Convention

Use [Conventional Commits](https://www.conventionalcommits.org/) format:

```
type(scope): description

body

footer
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (no functional changes)
- `refactor`: Code refactoring (no functional changes)
- `perf`: Performance improvements
- `test`: Adding or fixing tests
- `build`: Build system or dependency changes
- `ci`: CI/CD changes
- `chore`: Other changes that don't modify src or test files

**Examples**:
```bash
# Feature
git commit -m "feat(ui): add chat message typing indicator"

# Bug fix
git commit -m "fix(core): prevent NPE in ConfigStore"

# Documentation
git commit -m "docs: update README with setup instructions"

# Refactoring
git commit -m "refactor(design): extract color tokens to DesignTokens"

# Multiple changes
git commit -m "feat(terminal): add PTY support

- Add TerminalProcess.kt with PTY support
- Update TerminalScreen.kt to use new process
- Add tests for TerminalProcess"
```

## 📁 Project Structure

### Directory Structure

```
cline-android/
├── app/                          # Android module
│   ├── src/main/                 # Main source set
│   │   ├── java/com/cline/app/   # All Kotlin code
│   │   │   ├── ClineApp.kt       # Application class
│   │   │   │
│   │   │   ├── core/             # Core logic
│   │   │   │   ├── ClineController.kt
│   │   │   │   ├── ConfigStore.kt
│   │   │   │   ├── ContainerRuntime.kt
│   │   │   │   ├── EnvironmentAccess.kt
│   │   │   │   ├── ProotBootstrap.kt
│   │   │   │   ├── RuntimeHostPorts.kt
│   │   │   │   ├── TerminalProcess.kt
│   │   │   │   └── WebProcessManager.kt
│   │   │   │
│   │   │   ├── data/             # Data layer
│   │   │   │   ├── KeyVault.kt
│   │   │   │   └── repositories/
│   │   │   │       ├── BackupRepository.kt
│   │   │   │       └── PluginRepository.kt
│   │   │   │
│   │   │   ├── design/            # Design system
│   │   │   │   ├── tokens/
│   │   │   │   │   └── DesignTokens.kt
│   │   │   │   ├── theme/
│   │   │   │   │   └── Theme.kt
│   │   │   │   ├── components/
│   │   │   │   │   ├── Buttons.kt
│   │   │   │   │   ├── Cards.kt
│   │   │   │   │   ├── Dialogs.kt
│   │   │   │   │   ├── Inputs.kt
│   │   │   │   │   └── Navigation.kt
│   │   │   │   └── animations/
│   │   │   │       └── Animations.kt
│   │   │   │
│   │   │   ├── bridge/            # Bridge layer
│   │   │   │   ├── AdbBridge.kt
│   │   │   │   ├── AppBridge.kt
│   │   │   │   └── LocalNetworkAccess.kt
│   │   │   │
│   │   │   ├── recovery/          # Recovery layer
│   │   │   │   ├── RecoveryController.kt
│   │   │   │   ├── RecoveryRuntime.kt
│   │   │   │   └── RecoveryService.kt
│   │   │   │
│   │   │   ├── ui/                # UI layer
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── activities/
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
│   │   │   │   ├── navigation/
│   │   │   │   │   └── Navigation.kt
│   │   │   │   │
│   │   │   │   ├── screens/
│   │   │   │   │   ├── BackupScreen.kt
│   │   │   │   │   ├── ChatScreen.kt
│   │   │   │   │   ├── MainScreen.kt
│   │   │   │   │   ├── PluginsScreen.kt
│   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   ├── TerminalScreen.kt
│   │   │   │   │   └── WelcomeScreen.kt
│   │   │   │   │
│   │   │   │   ├── adapters/
│   │   │   │   │   └── PluginListAdapter.kt
│   │   │   │   │
│   │   │   │   └── fragments/
│   │   │   │       ├── ChatFragment.kt
│   │   │   │       ├── PluginsFragment.kt
│   │   │   │       └── TerminalFragment.kt
│   │   │   │
│   │   │   └── util/               # Utilities
│   │   │       ├── Constants.kt
│   │   │       ├── ForegroundActivity.kt
│   │   │       ├── ShellExecutor.kt
│   │   │       └── SystemLanguage.kt
│   │   │
│   │   └── res/                   # Resources
│   │       ├── values/
│   │       │   ├── colors.xml
│   │       │   ├── dimens.xml
│   │       │   ├── strings.xml
│   │       │   ├── styles.xml
│   │       │   └── themes.xml
│   │       ├── drawable/
│   │       │   ├── ic_launcher_background.xml
│   │       │   ├── ic_launcher_foreground.xml
│   │       │   ├── ic_launcher_round.xml
│   │       │   ├── shape_rounded_corner_large.xml
│   │       │   ├── shape_rounded_corner_medium.xml
│   │       │   └── shape_rounded_corner_small.xml
│   │       ├── layout/
│   │       │   └── item_plugin.xml
│   │       └── mipmap-*/
│   │           ├── ic_launcher.png
│   │           └── ic_launcher_round.png
│   │
│   └── build.gradle
│
├── tools/                              # Build tools
│   ├── build-cline-runtime.py
│   ├── build-rootfs.py
│   ├── prepare-assets.py
│   ├── prepare-recovery-assets.py
│   ├── prepare-web-compat.mjs
│   ├── cline-runtime/
│   ├── recovery-runtime/
│   │   └── lock.json
│   └── web-compat/
│       ├── entry.cjs
│       └── package.json
│
├── .github/workflows/                 # CI/CD
│   ├── build.yml
│   └── test.yml
│
├── build.gradle                        # Root build
├── build.sh                            # Build script
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── README.md
```

### Package Structure

```
com.cline.app
├── ClineApp                          # Application class
│
├── backup                            # Backup functionality
│   └── BackupManager                 # Backup/restore management
│
├── bridge                            # Bridge layer
│   ├── AdbBridge                     # ADB communication
│   ├── AppBridge                     # App operations
│   └── LocalNetworkAccess            # Network connectivity
│
├── core                              # Core logic
│   ├── ClineController               # Main controller
│   ├── ConfigStore                   # Configuration
│   ├── ContainerRuntime              # Container management
│   ├── EnvironmentAccess             # Environment access
│   ├── ProotBootstrap                # proot management
│   ├── RuntimeHostPorts              # Port management
│   ├── TerminalProcess               # Terminal process
│   └── WebProcessManager              # Web UI process
│
├── data                              # Data layer
│   ├── KeyVault                      # API key encryption
│   └── repositories                  # Data repositories
│       ├── BackupRepository          # Backup management
│       └── PluginRepository          # Plugin management
│
├── design                            # Design system
│   ├── animations                    # Animation utilities
│   │   └── Animations
│   ├── components                    # Reusable components
│   │   ├── Buttons
│   │   ├── Cards
│   │   ├── Dialogs
│   │   ├── Inputs
│   │   └── Navigation
│   ├── theme                         # Theme system
│   │   └── Theme
│   └── tokens                        # Design tokens
│       └── DesignTokens
│
├── recovery                          # Recovery layer
│   ├── RecoveryController            # Recovery orchestration
│   ├── RecoveryRuntime               # Recovery runtime
│   └── RecoveryService               # Recovery service
│
├── runtime                           # Runtime layer (moved to core)
│
├── ui                               # UI layer
│   ├── MainActivity                  # Main entry point
│   ├── activities                    # Activities
│   │   ├── BackupActivity
│   │   ├── ChatActivity
│   │   ├── ChatDetailActivity
│   │   ├── ExtractActivity
│   │   ├── PluginDetailActivity
│   │   ├── PluginInstallActivity
│   │   ├── PluginsActivity
│   │   ├── RecoveryActivity
│   │   ├── SettingsActivity
│   │   └── TerminalActivity
│   ├── adapters                      # Legacy adapters
│   │   └── PluginListAdapter
│   ├── fragments                      # Legacy fragments
│   │   ├── ChatFragment
│   │   ├── PluginsFragment
│   │   └── TerminalFragment
│   ├── navigation                    # Navigation
│   │   └── Navigation
│   └── screens                        # Compose screens
│       ├── BackupScreen
│       ├── ChatScreen
│       ├── MainScreen
│       ├── PluginsScreen
│       ├── SettingsScreen
│       ├── TerminalScreen
│       └── WelcomeScreen
│
└── util                              # Utilities
    ├── Constants
    ├── ForegroundActivity
    ├── ShellExecutor
    └── SystemLanguage
```

## 📖 Coding Guidelines

### Kotlin Style Guide

Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

#### Naming
- **Classes/Packages**: PascalCase (`ClineController`, `com.cline.app`)
- **Functions/Properties**: camelCase (`getRuntimeStatus`, `isRunning`)
- **Variables**: camelCase (`runtimeDir`, `configStore`)
- **Constants**: UPPER_SNAKE_CASE (`APP_NAME`, `MAX_RETRIES`)
- **Parameters**: camelCase (`context: Context`, `viewModel: ViewModel`)

#### Formatting
- **Indentation**: 4 spaces (no tabs)
- **Line Length**: 120 characters maximum
- **Braces**: Always use braces for control structures
- **Parentheses**: Omit when possible for single-parameter lambdas

#### Null Safety
- **Prefer non-null types**: Use `String` instead of `String?` when possible
- **Use safe calls**: `user?.name` instead of `user.name` when null is possible
- **Use Elvis operator**: `val name = user?.name ?: "Unknown"`
- **Use `requireNotNull`**: When null should never happen
- **Use `checkNotNull`**: When null indicates a bug

#### Collections
- **Prefer immutable**: Use `listOf()`, `mapOf()` instead of `mutableListOf()`
- **Use sequence**: For chained operations on large collections
- **Use `forEach`**: Instead of `for` loops when possible
- **Use `map`/`filter`**: Instead of manual iteration

#### Functions
- **Single responsibility**: Each function does one thing
- **Pure functions**: Prefer functions without side effects
- **Default parameters**: Use default parameter values
- **Named arguments**: Use named arguments for clarity
- **Extension functions**: Use for utility functions

### Android Specific

#### Context
- **Avoid holding Context**: Can cause memory leaks
- **Use Application Context**: When possible to avoid leaks
- **Use `requireContext()`**: In Composable functions
- **Use `LocalContext.current`**: In Composable functions

#### Resources
- **Use resource IDs**: `R.string.app_name` instead of hardcoded strings
- **Use string resources**: For all user-facing text
- **Use dimension resources**: For all sizes
- **Use color resources**: For all colors

#### ViewModel
- **One ViewModel per screen**: Keep ViewModels focused
- **Use `by viewModel()`**: In Composable functions
- **Use `SavedStateHandle`**: For saving state across configuration changes
- **Keep business logic in ViewModel**: Not in Composable functions

#### Coroutines
- **Use `viewModelScope`**: For ViewModel coroutines
- **Use `lifecycleScope`**: For Activity/Fragment coroutines
- **Use `Dispatchers.IO`**: For I/O operations
- **Use `Dispatchers.Default`**: For CPU-intensive operations
- **Use `Dispatchers.Main`**: For UI updates
- **Use `withContext`**: To switch dispatchers

#### Flow
- **Use `StateFlow`**: For UI state
- **Use `SharedFlow`**: For one-time events
- **Use `collectAsState()`**: In Composable functions
- **Use `combine`**: To combine multiple flows
- **Use `flatMapLatest`**: For dependent requests

### Compose Specific

#### Component Structure
- **Small, focused components**: Each component does one thing
- **Reusable components**: In `design/components/`
- **Screen-specific components**: In `ui/screens/`
- **Use `Modifier`**: For styling and layout
- **Use `remember`**: For state that survives recomposition

#### State Management
- **Use `mutableStateOf`**: For local state
- **Use `remember`**: To avoid recomputation
- **Use `LaunchedEffect`**: For side effects
- **Use `DisposableEffect`**: For cleanup
- **Use `derivedStateOf`**: For derived state

#### Performance
- **Avoid unnecessary recomposition**: Use `remember` and keys
- **Use `LazyColumn`/`LazyRow`**: For large lists
- **Use `key` parameter**: For stable item identity
- **Avoid heavy computations in composition**: Use `remember` + `LaunchedEffect`
- **Use `Crossfade`**: For smooth content transitions

#### Styling
- **Use DesignTokens**: For colors, spacing, typography
- **Use MaterialTheme**: For theming
- **Avoid hardcoded values**: Use tokens instead
- **Use `Modifier`**: For all styling

## 🎨 UI Development

### Creating a New Screen

1. **Create the screen file** in `app/src/main/java/com/cline/app/ui/screens/`

```kotlin
// ChatScreen.kt
package com.cline.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChatScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Chat Screen")
    }
}
```

2. **Add to navigation** in `app/src/main/java/com/cline/app/ui/navigation/Navigation.kt`

```kotlin
sealed class Screen(val route: String) {
    // ... existing screens
    object Chat : Screen("chat")
}

@Composable
fun ClineNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Welcome.route) {
        // ... existing destinations
        composable(Screen.Chat.route) {
            ChatScreen(onBack = { navController.popBackStack() })
        }
    }
}
```

3. **Add strings** in `app/src/main/res/values/strings.xml`

```xml
<string name="chat_title">Chat</string>
<string name="chat_subtitle">Talk to Cline AI</string>
<string name="chat_hint">Type a message...</string>
```

4. **Add to bottom navigation** in `MainScreen.kt`

```kotlin
BottomNavigationBar(
    currentScreen = currentScreen,
    onScreenSelected = { screen ->
        when (screen) {
            Screen.Chat -> navController.navigate(Screen.Chat.route)
            // ... other screens
        }
    }
)
```

### Creating a New Component

1. **Create the component file** in `app/src/main/java/com/cline/app/design/components/`

```kotlin
// CustomCard.kt
package com.cline.app.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cline.app.design.tokens.DesignTokens

@Composable
fun CustomCard(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = DesignTokens.Shape.Medium,
        elevation = DesignTokens.Elevation.Level2
    ) {
        Column {
            Text(title, style = DesignTokens.Typography.TitleLarge)
            content()
        }
    }
}
```

2. **Use the component** in your screens

```kotlin
CustomCard(title = "My Card") {
    Text("Card content")
}
```

### Design System Usage

#### Colors
```kotlin
// Use DesignTokens colors
Box(
    modifier = Modifier
        .background(DesignTokens.Colors.Primary)
        .padding(DesignTokens.Spacing.md)
)

// Or use MaterialTheme
Box(
    modifier = Modifier
        .background(MaterialTheme.colorScheme.primary)
        .padding(MaterialTheme.spacing.md)
)
```

#### Typography
```kotlin
Text(
    text = "Hello",
    style = DesignTokens.Typography.HeadlineLarge
)

// Or use MaterialTheme
Text(
    text = "Hello",
    style = MaterialTheme.typography.headlineLarge
)
```

#### Spacing
```kotlin
Column(
    modifier = Modifier.padding(DesignTokens.Spacing.md),
    verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
) {
    // Content
}
```

#### Shapes
```kotlin
Box(
    modifier = Modifier
        .clip(DesignTokens.Shape.Medium)
        .background(MaterialTheme.colorScheme.surfaceContainer)
)
```

#### Elevation
```kotlin
Card(
    elevation = DesignTokens.Elevation.Level3
) {
    // Content
}
```

### Animations

#### Spring Animation
```kotlin
val scale by animateFloatAsState(
    targetValue = if (expanded) 1.1f else 1f,
    animationSpec = DesignTokens.Motion.Spring
)

Box(
    modifier = Modifier.scale(scale)
)
```

#### Enter/Exit Transitions
```kotlin
AnimatedContent(
    targetState = currentScreen,
    transitionSpec = {
        Animations.contentSizeSpring()
    },
    label = "screen_transition"
) { screen ->
    when (screen) {
        Screen.Chat -> ChatScreen(...)
        // ...
    }
}
```

#### Crossfade
```kotlin
Crossfade(targetState = currentScreen) { screen ->
    when (screen) {
        Screen.Chat -> ChatScreen(...)
        // ...
    }
}
```

## ⚙️ Core Development

### Adding a New Feature to Core

1. **Create the class** in appropriate package
2. **Add dependencies** via constructor injection
3. **Implement the feature**
4. **Add to ClineController** if needed

Example: Adding a new service

```kotlin
// NewService.kt
package com.cline.app.core

import android.content.Context

class NewService(private val context: Context) {
    fun doSomething(): String {
        return "Hello from NewService"
    }
}
```

```kotlin
// ClineController.kt
class ClineController(
    // ... existing dependencies
    private val newService: NewService
) {
    fun useNewService() {
        val result = newService.doSomething()
        // ...
    }
}
```

```kotlin
// ClineApp.kt
class ClineApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val newService = NewService(applicationContext)
        val clineController = ClineController(
            // ... existing parameters
            newService = newService
        )
    }
}
```

### Working with proot

```kotlin
// Check if proot is available
val prootBootstrap = ProotBootstrap(context)
if (prootBootstrap.isProotAvailable()) {
    // proot is ready to use
}

// Extract proot if needed
if (!prootBootstrap.isProotAvailable()) {
    prootBootstrap.extractProot()
}

// Execute command in proot
val shellExecutor = ShellExecutor()
val result = shellExecutor.executeInProot(
    command = "ls -la",
    workingDirectory = Constants.RUNTIME_DIR
)
```

### Working with Runtime

```kotlin
// Start container runtime
val containerRuntime = ContainerRuntime(context)
containerRuntime.start()

// Check runtime status
val status = containerRuntime.getStatus()

// Execute command in runtime
val result = containerRuntime.executeCommand("echo hello")

// Stop runtime
containerRuntime.stop()
```

### Working with Configuration

```kotlin
// Get configuration
val configStore = ConfigStore(context)
val darkMode = configStore.isDarkMode
val apiKey = configStore.apiKey

// Update configuration
configStore.isDarkMode = true
configStore.apiKey = "new-api-key"

// Listen to changes (using Flow)
val darkModeFlow = configStore.darkModeFlow
viewModelScope.launch {
    darkModeFlow.collect { isDark ->
        // Update UI
    }
}
```

### Working with Encryption

```kotlin
// Encrypt/decrypt with KeyVault
val keyVault = KeyVault(context)

// Encrypt a string
val encrypted = keyVault.encrypt("secret-api-key")

// Decrypt a string
val decrypted = keyVault.decrypt(encrypted)

// Check if key exists
val hasKey = keyVault.hasKey()

// Delete key
keyVault.deleteKey()
```

## 🧪 Testing

### Unit Tests

Create test file in `app/src/test/java/com/cline/app/`:

```kotlin
// MyClassTest.kt
package com.cline.app

import org.junit.Test
import org.junit.Assert.*

class MyClassTest {
    @Test
    fun testSomething() {
        val result = myFunction()
        assertEquals("expected", result)
    }
}
```

Run tests:
```bash
./gradlew :app:testStandardDebugUnitTest
```

### Instrumentation Tests

Create test file in `app/src/androidTest/java/com/cline/app/`:

```kotlin
// MyActivityTest.kt
package com.cline.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyActivityTest {
    @Test
    fun testActivityLaunch() {
        // Test activity launch
    }
}
```

Run tests:
```bash
./gradlew :app:connectedStandardDebugAndroidTest
```

### UI Tests (Compose)

```kotlin
// MyScreenTest.kt
package com.cline.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class MyScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testScreenContent() {
        composeTestRule.setContent {
            MyScreen()
        }

        composeTestRule.onNodeWithText("Hello").assertExists()
    }

    @Test
    fun testButtonClick() {
        composeTestRule.setContent {
            MyScreen()
        }

        composeTestRule.onNodeWithText("Click me").performClick()
        
        // Verify result
    }
}
```

## 🐛 Debugging

### Android Studio Debugging

1. **Set breakpoints** in your code
2. **Run in debug mode**: Select "Debug" configuration
3. **Attach debugger**: Use "Attach Debugger to Android Process"
4. **Inspect variables**: Hover over variables in debug view
5. **Evaluate expressions**: Use "Evaluate Expression" window

### Log Debugging

Add logging to your code:

```kotlin
import android.util.Log

class MyClass {
    companion object {
        private const val TAG = "MyClass"
    }

    fun myFunction() {
        Log.d(TAG, "Function started")
        
        try {
            // Do something
            Log.i(TAG, "Success: operation completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error: operation failed", e)
        }
        
        Log.v(TAG, "Verbose: detailed information")
        Log.w(TAG, "Warning: something unexpected happened")
    }
}
```

View logs:
```bash
# View all logs
adb logcat

# View app-specific logs
adb logcat | grep "MyClass"

# View with specific tag
adb logcat MyClass:D *:S

# Clear logs
adb logcat -c
```

### Compose Debugging

#### Compose Layout Inspector
1. Enable in Android Studio: View → Tool Windows → Layout Inspector
2. Select your app process
3. Inspect Compose UI hierarchy

#### Compose State Debugging
```kotlin
// Enable state debugging
BuildConfig.DEBUG = true

// Use Compose Debugger
@Composable
fun MyScreen() {
    val state = remember { mutableStateOf(0) }
    
    // Debug state changes
    LaunchedEffect(state.value) {
        Log.d("MyScreen", "State changed to: ${state.value}")
    }
}
```

#### Recomposition Counting
```kotlin
// Track recompositions
var recompositionCount by remember { mutableStateOf(0) }

SideEffect {
    recompositionCount++
    Log.d("MyScreen", "Recomposed $recompositionCount times")
}
```

### Network Debugging

```kotlin
// Log network requests
val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()
```

View network traffic:
```bash
# Monitor network
adb shell netstat

# Capture network traffic
adb shell tcpdump -i any -s 0 -w /sdcard/capture.pcap
```

### Database Debugging

```kotlin
// For Room database
val db = AppDatabase.getInstance(context)
val dao = db.myDao()

// Query and log data
val allItems = dao.getAll()
Log.d("Database", "Items: $allItems")
```

### Performance Debugging

#### Memory
```bash
# Check memory usage
adb shell dumpsys meminfo com.cline.app

# Check memory stats
adb shell procrank
```

#### CPU
```bash
# Check CPU usage
adb shell top -n 1 -d 1

# Check CPU stats
adb shell cat /proc/cpuinfo
```

#### Battery
```bash
# Check battery stats
adb shell dumpsys battery

# Check battery usage
adb shell dumpsys batterystats
```

## ⚡ Performance Tips

### General Performance

1. **Avoid object creation in loops**
   ```kotlin
   // Bad
   for (i in 0..100) {
       val list = mutableListOf<Int>()
       list.add(i)
   }
   
   // Good
   val list = mutableListOf<Int>()
   for (i in 0..100) {
       list.add(i)
   }
   ```

2. **Use `apply` for object configuration**
   ```kotlin
   // Bad
   val view = TextView(context)
   view.text = "Hello"
   view.textSize = 16f
   view.setTextColor(Color.BLACK)
   
   // Good
   val view = TextView(context).apply {
       text = "Hello"
       textSize = 16f
       setTextColor(Color.BLACK)
   }
   ```

3. **Use `also` for additional operations**
   ```kotlin
   val list = mutableListOf<Int>().also {
       it.add(1)
       it.add(2)
   }
   ```

### Compose Performance

1. **Use `remember` to avoid recomputation**
   ```kotlin
   // Bad
   @Composable
   fun MyComponent() {
       val computedValue = expensiveComputation()
       Text(computedValue)
   }
   
   // Good
   @Composable
   fun MyComponent() {
       val computedValue = remember { expensiveComputation() }
       Text(computedValue)
   }
   ```

2. **Use `derivedStateOf` for derived state**
   ```kotlin
   val list by remember { mutableStateOf(listOf<Int>()) }
   
   // Bad - recomputes on every recomposition
   val filteredList = list.filter { it > 0 }
   
   // Good - only recomputes when list changes
   val filteredList by remember {
       derivedStateOf { list.filter { it > 0 } }
   }
   ```

3. **Use `LazyColumn`/`LazyRow` for large lists**
   ```kotlin
   // Bad - renders all items at once
   Column {
       items.forEach { item ->
           Item(item)
       }
   }
   
   // Good - renders only visible items
   LazyColumn {
       items(items) { item ->
           Item(item)
       }
   }
   ```

4. **Use `key` parameter for stable identity**
   ```kotlin
   LazyColumn {
       items(items, key = { it.id }) { item ->
           Item(item)
       }
   }
   ```

5. **Avoid unnecessary recomposition**
   ```kotlin
   // Bad - causes recomposition of entire list
   var count by remember { mutableStateOf(0) }
   
   Button(onClick = { count++ }) {
       Text("Click $count")
   }
   
   LazyColumn {
       items(items) { item ->
           Item(item, count) // Recomposes all items when count changes
       }
   }
   
   // Good - only recomposes button
   var count by remember { mutableStateOf(0) }
   
   Button(onClick = { count++ }) {
       Text("Click $count")
   }
   
   LazyColumn {
       items(items) { item ->
           Item(item) // Not affected by count changes
       }
   }
   ```

### Coroutines Performance

1. **Use appropriate dispatchers**
   ```kotlin
   // Bad - blocking main thread
   viewModelScope.launch(Dispatchers.Main) {
       val result = heavyComputation() // Blocks UI!
   }
   
   // Good - use appropriate dispatcher
   viewModelScope.launch(Dispatchers.Default) {
       val result = heavyComputation()
       withContext(Dispatchers.Main) {
           // Update UI
       }
   }
   ```

2. **Use `flowOn` for Flow operations**
   ```kotlin
   // Bad - all operations on Main
   flow { emit(data) }
       .map { transform(it) }
       .collect { updateUI(it) }
   
   // Good - heavy operations on Default
   flow { emit(data) }
       .flowOn(Dispatchers.Default)
       .map { transform(it) }
       .flowOn(Dispatchers.Default)
       .collect { updateUI(it) }
   ```

3. **Cancel coroutines when no longer needed**
   ```kotlin
   val job = viewModelScope.launch {
       // Long running operation
   }
   
   // Cancel when needed
   onCleared {
       job.cancel()
   }
   ```

### Memory Performance

1. **Avoid memory leaks**
   ```kotlin
   // Bad - can cause memory leak
   class MyActivity : ComponentActivity() {
       private lateinit var callback: (() -> Unit)
       
       override fun onCreate(savedInstanceState: Bundle?) {
           callback = { doSomething() }
           someManager.registerCallback(callback)
       }
       
       // Forgot to unregister!
   }
   
   // Good - unregister in onDestroy
   class MyActivity : ComponentActivity() {
       private lateinit var callback: (() -> Unit)
       
       override fun onCreate(savedInstanceState: Bundle?) {
           callback = { doSomething() }
           someManager.registerCallback(callback)
       }
       
       override fun onDestroy() {
           someManager.unregisterCallback(callback)
           super.onDestroy()
       }
   }
   ```

2. **Use weak references when appropriate**
   ```kotlin
   import java.lang.ref.WeakReference
   
   class MyClass {
       private var activityRef: WeakReference<Activity>? = null
       
       fun setActivity(activity: Activity) {
           activityRef = WeakReference(activity)
       }
       
       fun getActivity(): Activity? = activityRef?.get()
   }
   ```

3. **Clean up resources**
   ```kotlin
   class MyService : Service() {
       private var process: Process? = null
       
       override fun onDestroy() {
           process?.destroy()
           process = null
           super.onDestroy()
       }
   }
   ```

## 🤝 Contribution Guidelines

We welcome contributions from the community! Please follow these guidelines:

### Before Contributing

1. **Read the documentation**
   - [README](../README.md)
   - [Architecture](ARCHITECTURE.md)
   - This Development Guide

2. **Check existing issues**
   - Search [GitHub Issues](https://github.com/your-org/cline-android/issues)
   - Check if your feature request or bug report already exists

3. **Discuss large changes**
   - Open a discussion for major features
   - Get feedback before implementing

### Contributing Code

1. **Fork the repository**
   - Click "Fork" on GitHub
   - Clone your fork locally

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature
   ```

3. **Follow coding guidelines**
   - See [Coding Guidelines](#-coding-guidelines)
   - Follow existing code style

4. **Write tests**
   - Add unit tests for new functionality
   - Add UI tests for new screens/components
   - Ensure all tests pass

5. **Update documentation**
   - Update relevant documentation
   - Add comments for complex code

6. **Commit your changes**
   - Use [Conventional Commits](#commit-message-convention)
   - Write clear, descriptive commit messages

7. **Push to your fork**
   ```bash
   git push origin feature/your-feature
   ```

8. **Create a Pull Request**
   - Go to [Pull Requests](https://github.com/your-org/cline-android/pulls)
   - Click "New Pull Request"
   - Select your branch
   - Fill out the PR template
   - Submit for review

### Pull Request Template

```markdown
## Description

Please include a summary of the change and which issue is fixed. Please also include relevant motivation and context.

Fixes # (issue)

## Type of Change

- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update
- [ ] Code refactoring
- [ ] Performance improvement
- [ ] Test addition/fix
- [ ] Other (please specify)

## Changes Made

- Change 1
- Change 2
- Change 3

## Testing

- [ ] Unit tests pass
- [ ] UI tests pass
- [ ] Manual testing completed
- [ ] All existing tests pass

## Checklist

- [ ] My code follows the code style of this project
- [ ] I have performed a self-review of my code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] New and existing unit tests pass locally with my changes
- [ ] Any dependent changes have been merged
```

### Review Process

1. **Initial Review**
   - Maintainers will review your PR
   - May request changes or clarifications

2. **CI Checks**
   - All CI checks must pass
   - Build must succeed
   - Tests must pass

3. **Approval**
   - At least one maintainer must approve
   - All requested changes must be addressed

4. **Merge**
   - Maintainer will merge your PR
   - Or you can merge if you have write access

### Code of Conduct

By participating in this project, you agree to abide by the [Code of Conduct](CODE_OF_CONDUCT.md).

## 🙏 Support

For development-related questions:
- Check this Development Guide
- Search [GitHub Discussions](https://github.com/your-org/cline-android/discussions)
- Create a new discussion

For bugs or feature requests:
- Search [GitHub Issues](https://github.com/your-org/cline-android/issues)
- Create a new issue with:
  - Clear description
  - Steps to reproduce
  - Expected vs actual behavior
  - Screenshots if applicable
  - Device and Android version

## 📚 Resources

- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Android Developer Documentation](https://developer.android.com/docs)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose/documentation)
- [Material Design 3 Guidelines](https://m3.material.io/)
- [Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Flow Documentation](https://kotlinlang.org/docs/flow.html)

---

*Last updated: $(date)*
