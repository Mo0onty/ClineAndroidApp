// ForegroundActivity.kt - Foreground Activity Tracking
package com.cline.app.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import java.lang.ref.WeakReference

/**
 * Foreground Activity - Tracks which activity is currently in the foreground
 * 
 * This class provides:
 * - Tracking of the current foreground activity
 * - Callback when activity changes
 * - App foreground/background state tracking
 */
class ForegroundActivity(private val application: Application) : Application.ActivityLifecycleCallbacks {
    
    companion object {
        private const val TAG = "ForegroundActivity"
    }
    
    // Current activity
    private var currentActivityRef: WeakReference<Activity>? = null
    private var activityCount = 0
    
    // Callbacks
    private var onActivityChanged: ((Activity?) -> Unit)? = null
    private var onAppForegrounded: (() -> Unit)? = null
    private var onAppBackgrounded: (() -> Unit)? = null
    
    // State
    private var isAppForeground = false
    
    // ========================================================================
    // INITIALIZATION
    // ========================================================================
    
    init {
        application.registerActivityLifecycleCallbacks(this)
    }
    
    // ========================================================================
    // LIFECYCLE CALLBACKS
    // ========================================================================
    
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(TAG, "Activity created: ${activity.javaClass.simpleName}")
        currentActivityRef = WeakReference(activity)
        onActivityChanged?.invoke(activity)
    }
    
    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "Activity started: ${activity.javaClass.simpleName}")
        activityCount++
        currentActivityRef = WeakReference(activity)
        onActivityChanged?.invoke(activity)
        
        if (activityCount == 1) {
            // App moved to foreground
            isAppForeground = true
            onAppForegrounded?.invoke()
        }
    }
    
    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "Activity resumed: ${activity.javaClass.simpleName}")
        currentActivityRef = WeakReference(activity)
        onActivityChanged?.invoke(activity)
    }
    
    override fun onActivityPaused(activity: Activity) {
        Log.d(TAG, "Activity paused: ${activity.javaClass.simpleName}")
    }
    
    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "Activity stopped: ${activity.javaClass.simpleName}")
        activityCount--
        
        if (activityCount == 0) {
            // App moved to background
            isAppForeground = false
            onAppBackgrounded?.invoke()
        }
    }
    
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }
    
    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "Activity destroyed: ${activity.javaClass.simpleName}")
        
        // Clear reference if this is the current activity
        currentActivityRef?.get()?.let { current ->
            if (current == activity) {
                currentActivityRef = null
                onActivityChanged?.invoke(null)
            }
        }
    }
    
    // ========================================================================
    // CURRENT ACTIVITY
    // ========================================================================
    
    /**
     * Get the current foreground activity
     */
    fun getCurrentActivity(): Activity? {
        return currentActivityRef?.get()
    }
    
    /**
     * Check if a specific activity is the current foreground activity
     */
    fun isCurrentActivity(activity: Activity): Boolean {
        return currentActivityRef?.get() == activity
    }
    
    // ========================================================================
    // APP STATE
    // ========================================================================
    
    /**
     * Check if app is in foreground
     */
    fun isAppForeground(): Boolean {
        return isAppForeground
    }
    
    /**
     * Called when app moves to foreground
     */
    fun onAppForegrounded() {
        isAppForeground = true
        onAppForegrounded?.invoke()
    }
    
    /**
     * Called when app moves to background
     */
    fun onAppBackgrounded() {
        isAppForeground = false
        onAppBackgrounded?.invoke()
    }
    
    // ========================================================================
    // CALLBACKS
    // ========================================================================
    
    /**
     * Set callback for activity changes
     */
    fun setOnActivityChanged(callback: (Activity?) -> Unit) {
        onActivityChanged = callback
    }
    
    /**
     * Set callback for app foregrounded
     */
    fun setOnAppForegrounded(callback: () -> Unit) {
        onAppForegrounded = callback
    }
    
    /**
     * Set callback for app backgrounded
     */
    fun setOnAppBackgrounded(callback: () -> Unit) {
        onAppBackgrounded = callback
    }
    
    // ========================================================================
    // UTILITY
    // ========================================================================
    
    /**
     * Get the top activity from the activity stack
     */
    fun getTopActivity(): Activity? {
        // In a real implementation, we would have access to the activity stack
        // For now, we just return the current activity
        return currentActivityRef?.get()
    }
    
    /**
     * Check if any activity is in a specific state
     */
    fun hasActivityInState(state: String): Boolean {
        // This would require tracking all activities and their states
        return false
    }
}
