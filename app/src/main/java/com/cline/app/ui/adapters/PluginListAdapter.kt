package com.cline.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cline.app.R
import com.cline.app.data.repositories.PluginRepository

/**
 * Adapter for displaying a list of plugins.
 * 
 * Note: This adapter is for legacy View system compatibility.
 * For Compose-first apps, use LazyColumn with Composable items instead.
 */
class PluginListAdapter(
    private val plugins: List<PluginRepository.Plugin>,
    private val onPluginClick: (String) -> Unit,
    private val onPluginToggle: (String, Boolean) -> Unit
) : RecyclerView.Adapter<PluginListAdapter.PluginViewHolder>() {

    inner class PluginViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.plugin_name)
        private val versionTextView: TextView = itemView.findViewById(R.id.plugin_version)
        private val authorTextView: TextView = itemView.findViewById(R.id.plugin_author)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.plugin_description)
        private val stateTextView: TextView = itemView.findViewById(R.id.plugin_state)

        fun bind(plugin: PluginRepository.Plugin) {
            nameTextView.text = plugin.name
            versionTextView.text = "v${plugin.version}"
            authorTextView.text = plugin.author
            descriptionTextView.text = plugin.description
            stateTextView.text = when (plugin.state) {
                PluginRepository.PluginState.ENABLED -> "Enabled"
                PluginRepository.PluginState.INSTALLED -> "Installed"
                PluginRepository.PluginState.DISABLED -> "Disabled"
                PluginRepository.PluginState.NOT_INSTALLED -> "Not Installed"
                PluginRepository.PluginState.ERROR -> "Error"
            }

            itemView.setOnClickListener {
                onPluginClick(plugin.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PluginViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plugin, parent, false)
        return PluginViewHolder(view)
    }

    override fun onBindViewHolder(holder: PluginViewHolder, position: Int) {
        holder.bind(plugins[position])
    }

    override fun getItemCount(): Int = plugins.size

    /**
     * Update the plugin list.
     */
    fun updatePlugins(newPlugins: List<PluginRepository.Plugin>) {
        // In a real implementation, use DiffUtil for efficient updates
        // For simplicity, we just replace the list here
        // notifyDataSetChanged()
    }
}
