package com.example.we_vote.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.R
import com.example.we_vote.domain.model.ApplicationStatus
import com.example.we_vote.domain.model.SurveyApplication
import com.google.android.material.button.MaterialButton

class MyApplicationsAdapter(
    items: List<SurveyApplication>,
    private val onItemClick: (SurveyApplication) -> Unit,
    private val onCancel: (SurveyApplication, Int) -> Unit,
) : RecyclerView.Adapter<MyApplicationsAdapter.ViewHolder>() {

    private val items: MutableList<SurveyApplication> = items.toMutableList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.app_title)
        val statusLayout: View = itemView.findViewById(R.id.status_layout)
        val statusDot: View = itemView.findViewById(R.id.status_dot)
        val statusText: TextView = itemView.findViewById(R.id.app_status)
        val btnCancel: MaterialButton = itemView.findViewById(R.id.btn_cancel_application)

        fun bind(item: SurveyApplication) {
            titleText.text = item.title
            statusLayout.isVisible = true
            statusText.text = item.status.displayName

            val color = when (item.status) {
                ApplicationStatus.PENDING   -> Color.parseColor("#AEAF50")
                ApplicationStatus.ACCEPTED  -> Color.parseColor("#4CAF50")
                ApplicationStatus.REJECTED  -> Color.parseColor("#DD2C00")
                ApplicationStatus.CANCELLED -> Color.parseColor("#80FFFFFF")
            }
            statusText.setTextColor(color)
            statusDot.background.setTint(color)

            itemView.findViewById<View>(R.id.admin_buttons_layout).isVisible = false
            btnCancel.isVisible = item.status == ApplicationStatus.PENDING
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_application, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
        holder.btnCancel.setOnClickListener { onCancel(items[position], position) }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<SurveyApplication>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
