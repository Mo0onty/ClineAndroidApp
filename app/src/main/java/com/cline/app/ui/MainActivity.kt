// MainActivity.kt - Primary Entry Point
package com.cline.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cline.app.design.theme.ClineTheme
import com.cline.app.design.tokens.DesignTokens
import com.cline.app.ui.navigation.ClineNavHost
import com.cline.app.ui.screens.WelcomeScreen

/**
 * Main Activity - Primary entry point for the Cline Android app
 * 
 * This activity hosts the navigation graph and provides the root of the
 * composable hierarchy. It handles:
 * - Theme application
 * - Navigation setup
 * - Device configuration changes
 */
class MainActivity : ComponentActivity() {
    
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            ClineApp()
        }
    }
}

/**
 * Root composable for the Cline application
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ClineApp() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    
    // State for theme preferences
    var darkTheme by remember { mutableStateOf(false) }
    var dynamicColor by remember { mutableStateOf(true) }
    
    // Check if we should show welcome screen (first launch)
    var showWelcome by remember { mutableStateOf(true) }
    
    ClineTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            
            if (showWelcome) {
                WelcomeScreen(
                    onGetStarted = {
                        showWelcome = false
                    }
                )
            } else {
                ClineNavHost(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Preview function for MainActivity
 */
@Composable
fun MainActivityPreview() {
    ClineTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            WelcomeScreen(
                onGetStarted = {}
            )
        }
    }
}
