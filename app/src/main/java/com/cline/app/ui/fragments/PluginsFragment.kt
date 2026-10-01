package com.cline.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.cline.app.design.theme.ClineTheme
import com.cline.app.ui.screens.PluginsScreen

/**
 * Plugins Fragment - Displays the plugin management interface.
 * 
 * Note: This fragment is for legacy View system compatibility.
 * For Compose-first apps, use PluginsActivity with Compose directly.
 */
class PluginsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ClineTheme {
                    PluginsScreen(
                        onBack = { activity?.onBackPressed() },
                        onPluginClick = { pluginId ->
                            // Navigate to plugin detail
                        }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance() = PluginsFragment()
    }
}
