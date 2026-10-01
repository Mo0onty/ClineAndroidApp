// SystemLanguage.kt - System Language Initialization
package com.cline.app.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.util.Log
import java.util.Locale

/**
 * System Language - Manages system language and locale settings
 * 
 * This class provides:
 * - System locale detection
 * - App language configuration
 * - Locale comparison
 */
object SystemLanguage {
    
    private const val TAG = "SystemLanguage"
    
    // Default locale
    private var defaultLocale: Locale? = null
    
    // ========================================================================
    // INITIALIZATION
    // ========================================================================
    
    /**
     * Initialize system language
     */
    fun initialize(context: Context) {
        try {
            val configuration = context.resources.configuration
            defaultLocale = getSystemLocale(configuration)
            Log.i(TAG, "System language initialized: ${defaultLocale?.language}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize system language: ${e.message}")
        }
    }
    
    // ========================================================================
    // LOCALE
    // ========================================================================
    
    /**
     * Get the current system locale
     */
    fun getCurrentLocale(): Locale {
        return defaultLocale ?: Locale.getDefault()
    }
    
    /**
     * Get the system locale from configuration
     */
    private fun getSystemLocale(configuration: Configuration): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales.get(0) ?: Locale.getDefault()
        } else {
            @Suppress("DEPRECATION")
            configuration.locale ?: Locale.getDefault()
        }
    }
    
    /**
     * Set the app locale
     */
    fun setAppLocale(context: Context, locale: Locale) {
        try {
            val configuration = Configuration(context.resources.configuration)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocales(LocaleList(locale))
            } else {
                @Suppress("DEPRECATION")
                configuration.locale = locale
            }
            
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            defaultLocale = locale
            
            Log.i(TAG, "App locale set to: ${locale.language}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set app locale: ${e.message}")
        }
    }
    
    /**
     * Set the app locale by language code
     */
    fun setAppLocale(context: Context, language: String) {
        val locale = Locale(language)
        setAppLocale(context, locale)
    }
    
    // ========================================================================
    // LANGUAGE CODES
    // ========================================================================
    
    /**
     * Get the current language code
     */
    fun getCurrentLanguageCode(): String {
        return getCurrentLocale().language
    }
    
    /**
     * Get the current country code
     */
    fun getCurrentCountryCode(): String {
        return getCurrentLocale().country
    }
    
    /**
     * Get the current language tag
     */
    fun getCurrentLanguageTag(): String {
        return getCurrentLocale().toLanguageTag()
    }
    
    // ========================================================================
    // LANGUAGE DETECTION
    // ========================================================================
    
    /**
     * Check if the system language is RTL (Right-to-Left)
     */
    fun isRTL(): Boolean {
        val locale = getCurrentLocale()
        val directionality = Character.getDirectionality(locale.displayName[0])
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
               directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
    }
    
    /**
     * Check if the current locale matches a language code
     */
    fun matchesLanguage(languageCode: String): Boolean {
        return getCurrentLanguageCode() == languageCode
    }
    
    // ========================================================================
    // LOCALE UTILITIES
    // ========================================================================
    
    /**
     * Get locale from language code
     */
    fun getLocaleFromCode(code: String): Locale {
        return Locale(code)
    }
    
    /**
     * Get locale from language and country codes
     */
    fun getLocaleFromCodes(language: String, country: String): Locale {
        return Locale(language, country)
    }
    
    /**
     * Get locale from language tag
     */
    fun getLocaleFromTag(tag: String): Locale {
        return Locale.forLanguageTag(tag)
    }
    
    // ========================================================================
    // FORMATTING
    // ========================================================================
    
    /**
     * Format a number according to the current locale
     */
    fun formatNumber(number: Number): String {
        return java.text.NumberFormat.getInstance(getCurrentLocale()).format(number)
    }
    
    /**
     * Format a date according to the current locale
     */
    fun formatDate(date: java.util.Date): String {
        return java.text.DateFormat.getDateInstance().format(date)
    }
    
    /**
     * Format a time according to the current locale
     */
    fun formatTime(date: java.util.Date): String {
        return java.text.DateFormat.getTimeInstance().format(date)
    }
    
    /**
     * Format a date and time according to the current locale
     */
    fun formatDateTime(date: java.util.Date): String {
        return java.text.DateFormat.getDateTimeInstance().format(date)
    }
}
