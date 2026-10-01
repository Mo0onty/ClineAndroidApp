// KeyVault.kt - API Key Encryption
package com.cline.app.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Key Vault - Secure storage for API keys and sensitive data
 * 
 * This class provides:
 * - AES-GCM encryption for API keys
 * - Android Keystore integration
 * - Secure preference storage
 */
class KeyVault(private val context: Context) {
    
    companion object {
        private const val TAG = "KeyVault"
        private const val KEYSTORE_NAME = "AndroidKeyStore"
        private const val KEY_ALIAS = "cline_api_key_encryption"
        private const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"
        private const val KEY_SIZE = 256
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        
        // Shared preferences
        private const val PREFS_NAME = "cline_secure_prefs"
        private const val KEY_API_KEY = "api_key"
    }
    
    // Keystore
    private val keyStore: KeyStore by lazy { createKeyStore() }
    
    // Encrypted preferences
    private val encryptedPrefs: EncryptedSharedPreferences by lazy { createEncryptedPreferences() }
    
    // ========================================================================
    // KEYSTORE
    // ========================================================================
    
    /**
     * Create keystore
     */
    private fun createKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(KEYSTORE_NAME)
        keyStore.load(null)
        return keyStore
    }
    
    /**
     * Create or get encryption key
     */
    private fun getOrCreateKey(): SecretKey {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            createKey()
        }
        
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }
    
    /**
     * Create encryption key
     */
    private fun createKey() {
        try {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_NAME
            )
            
            val keyGenSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setKeySize(KEY_SIZE)
                .setRandomizedEncryptionRequired(true)
                .setUserAuthenticationRequired(false)
                .build()
            
            keyGenerator.init(keyGenSpec)
            keyGenerator.generateKey()
            
            Log.i(TAG, "Created encryption key")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create encryption key: ${e.message}")
            throw e
        }
    }
    
    // ========================================================================
    // ENCRYPTED PREFERENCES
    // ========================================================================
    
    /**
     * Create encrypted preferences
     */
    private fun createEncryptedPreferences(): EncryptedSharedPreferences {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create encrypted preferences: ${e.message}")
            throw e
        }
    }
    
    // ========================================================================
    // API KEY
    // ========================================================================
    
    /**
     * Set the API key (encrypted)
     */
    fun setApiKey(apiKey: String) {
        try {
            encryptedPrefs.edit().putString(KEY_API_KEY, apiKey).apply()
            Log.i(TAG, "API key stored securely")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store API key: ${e.message}")
            throw e
        }
    }
    
    /**
     * Get the API key (decrypted)
     */
    fun getApiKey(): String? {
        try {
            return encryptedPrefs.getString(KEY_API_KEY, null)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve API key: ${e.message}")
            return null
        }
    }
    
    /**
     * Check if API key is set
     */
    fun hasApiKey(): Boolean {
        return getApiKey()?.isNotBlank() == true
    }
    
    /**
     * Clear the API key
     */
    fun clearApiKey() {
        try {
            encryptedPrefs.edit().remove(KEY_API_KEY).apply()
            Log.i(TAG, "API key cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear API key: ${e.message}")
        }
    }
    
    // ========================================================================
    // ENCRYPTION (Direct)
    // ========================================================================
    
    /**
     * Encrypt data directly using AES-GCM
     */
    fun encrypt(data: String): ByteArray {
        try {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val key = getOrCreateKey()
            cipher.init(Cipher.ENCRYPT_MODE, key)
            
            val iv = cipher.iv
            val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            
            // Combine IV and encrypted data
            val result = ByteArray(iv.size + encrypted.size)
            System.arraycopy(iv, 0, result, 0, iv.size)
            System.arraycopy(encrypted, 0, result, iv.size, encrypted.size)
            
            return result
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to encrypt data: ${e.message}")
            throw e
        }
    }
    
    /**
     * Decrypt data directly using AES-GCM
     */
    fun decrypt(encryptedData: ByteArray): String {
        try {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val key = getOrCreateKey()
            
            // Extract IV (first 12 bytes for GCM)
            val iv = encryptedData.copyOfRange(0, 12)
            val data = encryptedData.copyOfRange(12, encryptedData.size)
            
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            
            val decrypted = cipher.doFinal(data)
            return String(decrypted, Charsets.UTF_8)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decrypt data: ${e.message}")
            throw e
        }
    }
    
    // ========================================================================
    // CLEANUP
    // ========================================================================
    
    /**
     * Clear all data
     */
    fun clearAll() {
        try {
            encryptedPrefs.edit().clear().apply()
            Log.i(TAG, "All secure data cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear all data: ${e.message}")
        }
    }
    
    // ========================================================================
    // KEY MANAGEMENT
    // ========================================================================
    
    /**
     * Delete encryption key
     */
    fun deleteKey() {
        try {
            keyStore.deleteEntry(KEY_ALIAS)
            Log.i(TAG, "Encryption key deleted")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete encryption key: ${e.message}")
        }
    }
    
    /**
     * Check if encryption key exists
     */
    fun hasKey(): Boolean {
        return try {
            keyStore.containsAlias(KEY_ALIAS)
        } catch (e: Exception) {
            false
        }
    }
}
