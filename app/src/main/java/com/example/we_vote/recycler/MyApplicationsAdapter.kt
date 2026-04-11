package com.example.we_vote.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.R
import com.example.we_vote.ktor.ApplicationStatus
import com.example.we_vote.ktor.DTOs

class MyApplicationsAdapter(
    private val items: List<DTOs.ApplicationDTO>,
    private val onItemClick: (DTOs.ApplicationDTO) -> Unit
) : RecyclerView.Adapter<MyApplicationsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.app_title)
        val statusLayout: View = itemView.findViewById(R.id.status_layout)
        val statusDot: View = itemView.findViewById(R.id.status_dot)
        val statusText: TextView = itemView.findViewById(R.id.app_status)

        fun bind(item: DTOs.ApplicationDTO) {
            titleText.text = item.title

            statusLayout.isVisible = true
            statusText.text = item.status.displayName

            val color = when (item.status) {
                ApplicationStatus.PENDING  -> Color.parseColor("#AEAF50")
                ApplicationStatus.ACCEPTED -> Color.parseColor("#4CAF50")
                ApplicationStatus.REJECTED -> Color.parseColor("#DD2C00")
            }
            statusText.setTextColor(color)
            statusDot.background.setTint(color)

            // Admin buttons are never shown for users
            itemView.findViewById<View>(R.id.admin_buttons_layout).isVisible = false

            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_application, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
