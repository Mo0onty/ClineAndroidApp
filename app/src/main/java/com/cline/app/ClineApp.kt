// ClineApp.kt - Application Entry Point
package com.cline.app

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.cline.app.core.ClineController
import com.cline.app.core.ConfigStore
import com.cline.app.core.EnvironmentAccess
import com.cline.app.core.RuntimeHostPorts
import com.cline.app.data.KeyVault
import com.cline.app.runtime.ContainerRuntime
import com.cline.app.runtime.ProotBootstrap
import com.cline.app.runtime.WebProcessManager
import com.cline.app.util.Constants
import com.cline.app.util.ForegroundActivity
import com.cline.app.util.SystemLanguage
import java.io.File

/**
 * Cline Application - Main entry point for the Cline Android app
 * 
 * This class initializes all subsystems and provides global access to:
 * - Configuration storage
 * - Runtime management
 * - Web process management
 * - Environment access
 * - Key vault for API key encryption
 */
class ClineApp : Application(), LifecycleObserver {
    
    companion object {
        private const val TAG = "ClineApp"
        
        // Singleton instance
        private var instance: ClineApp? = null
        
        /**
         * Get the application instance
         */
        fun getInstance(): ClineApp {
            return instance ?: throw IllegalStateException("ClineApp not initialized")
        }
        
        /**
         * Get application context
         */
        fun getAppContext(): Context {
            return instance?.applicationContext ?: throw IllegalStateException("ClineApp not initialized")
        }
    }
    
    // Subsystems
    private lateinit var configStore: ConfigStore
    private lateinit var runtimeHostPorts: RuntimeHostPorts
    private lateinit var prootBootstrap: ProotBootstrap
    private lateinit var containerRuntime: ContainerRuntime
    private lateinit var webProcessManager: WebProcessManager
    private lateinit var environmentAccess: EnvironmentAccess
    private lateinit var keyVault: KeyVault
    private lateinit var clineController: ClineController
    private lateinit var foregroundActivity: ForegroundActivity
    
    // State
    private var isInitialized = false
    
    // ========================================================================
    // LIFECYCLE
    // ========================================================================
    
    override fun onCreate() {
        super.onCreate()
        
        // Store singleton instance
        instance = this
        
        // Initialize subsystems
        initializeSubsystems()
        
        // Register lifecycle observer
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        
        // Initialize system language
        SystemLanguage.initialize(applicationContext)
        
        // Mark as initialized
        isInitialized = true
    }
    
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // Apply any context wrappers if needed
    }
    
    // ========================================================================
    // LIFECYCLE OBSERVER
    // ========================================================================
    
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        // App moved to foreground
        foregroundActivity.onAppForegrounded()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        // App moved to background
        foregroundActivity.onAppBackgrounded()
    }
    
    // ========================================================================
    // INITIALIZATION
    // ========================================================================
    
    /**
     * Initialize all subsystems
     */
    private fun initializeSubsystems() {
        // Initialize configuration
        configStore = ConfigStore(applicationContext)
        
        // Initialize ports configuration
        runtimeHostPorts = RuntimeHostPorts(applicationContext)
        
        // Initialize proot bootstrap
        prootBootstrap = ProotBootstrap(applicationContext)
        
        // Initialize container runtime
        containerRuntime = ContainerRuntime(applicationContext)
        
        // Initialize web process manager
        webProcessManager = WebProcessManager(applicationContext)
        
        // Initialize environment access
        environmentAccess = EnvironmentAccess(applicationContext)
        
        // Initialize key vault
        keyVault = KeyVault(applicationContext)
        
        // Initialize foreground activity tracker
        foregroundActivity = ForegroundActivity(applicationContext)
        
        // Initialize main controller
        clineController = ClineController(
            context = applicationContext,
            configStore = configStore,
            runtimeHostPorts = runtimeHostPorts,
            prootBootstrap = prootBootstrap,
            containerRuntime = containerRuntime,
            webProcessManager = webProcessManager,
            environmentAccess = environmentAccess,
            keyVault = keyVault
        )
    }
    
    // ========================================================================
    // ACCESSORS
    // ========================================================================
    
    /**
     * Get the configuration store
     */
    fun getConfigStore(): ConfigStore {
        ensureInitialized()
        return configStore
    }
    
    /**
     * Get the runtime host ports
     */
    fun getRuntimeHostPorts(): RuntimeHostPorts {
        ensureInitialized()
        return runtimeHostPorts
    }
    
    /**
     * Get the proot bootstrap
     */
    fun getProotBootstrap(): ProotBootstrap {
        ensureInitialized()
        return prootBootstrap
    }
    
    /**
     * Get the container runtime
     */
    fun getContainerRuntime(): ContainerRuntime {
        ensureInitialized()
        return containerRuntime
    }
    
    /**
     * Get the web process manager
     */
    fun getWebProcessManager(): WebProcessManager {
        ensureInitialized()
        return webProcessManager
    }
    
    /**
     * Get the environment access
     */
    fun getEnvironmentAccess(): EnvironmentAccess {
        ensureInitialized()
        return environmentAccess
    }
    
    /**
     * Get the key vault
     */
    fun getKeyVault(): KeyVault {
        ensureInitialized()
        return keyVault
    }
    
    /**
     * Get the Cline controller
     */
    fun getClineController(): ClineController {
        ensureInitialized()
        return clineController
    }
    
    /**
     * Get the foreground activity tracker
     */
    fun getForegroundActivity(): ForegroundActivity {
        ensureInitialized()
        return foregroundActivity
    }
    
    // ========================================================================
    // UTILITY
    // ========================================================================
    
    /**
     * Ensure the app is initialized
     */
    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("ClineApp subsystems not initialized")
        }
    }
    
    /**
     * Get the app files directory
     */
    fun getFilesDir(): File {
        return filesDir
    }
    
    /**
     * Get the app cache directory
     */
    fun getCacheDir(): File {
        return cacheDir
    }
    
    /**
     * Get the app external files directory
     */
    fun getExternalFilesDir(): File? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getExternalFilesDir(null)
        } else {
            @Suppress("DEPRECATION")
            externalCacheDir?.parentFile
        }
    }
    
    /**
     * Run on UI thread
     */
    fun runOnUiThread(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            Handler(Looper.getMainLooper()).post(action)
        }
    }
    
    /**
     * Run on background thread
     */
    fun runOnBackgroundThread(action: () -> Unit) {
        Thread(action).start()
    }
}

// ========================================================================
// GLOBAL ACCESSORS
// ========================================================================

/**
 * Get the Cline controller from anywhere in the app
 */
fun getClineController(): ClineController {
    return ClineApp.getInstance().getClineController()
}

/**
 * Get the configuration store from anywhere in the app
 */
fun getConfigStore(): ConfigStore {
    return ClineApp.getInstance().getConfigStore()
}

/**
 * Get the web process manager from anywhere in the app
 */
fun getWebProcessManager(): WebProcessManager {
    return ClineApp.getInstance().getWebProcessManager()
}

/**
 * Get the container runtime from anywhere in the app
 */
fun getContainerRuntime(): ContainerRuntime {
    return ClineApp.getInstance().getContainerRuntime()
}
