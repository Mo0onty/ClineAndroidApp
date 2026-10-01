package com.cline.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.cline.app.design.theme.ClineTheme
import com.cline.app.ui.screens.TerminalScreen

/**
 * Terminal Fragment - Displays the terminal interface.
 * 
 * Note: This fragment is for legacy View system compatibility.
 * For Compose-first apps, use TerminalActivity with Compose directly.
 */
class TerminalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ClineTheme {
                    TerminalScreen(
                        onBack = { activity?.onBackPressed() }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance() = TerminalFragment()
    }
}
