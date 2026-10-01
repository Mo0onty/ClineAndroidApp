package com.cline.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.cline.app.design.theme.ClineTheme
import com.cline.app.ui.screens.ChatScreen

/**
 * Chat Fragment - Displays the chat interface.
 * 
 * Note: This fragment is for legacy View system compatibility.
 * For Compose-first apps, use ChatActivity with Compose directly.
 */
class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ClineTheme {
                    ChatScreen(
                        onBack = { activity?.onBackPressed() },
                        onOpenTerminal = {
                            // Navigate to terminal
                        }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance() = ChatFragment()
    }
}
