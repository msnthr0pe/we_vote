package com.example.we_vote.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.R
import com.example.we_vote.domain.model.ApplicationStatus
import com.example.we_vote.domain.model.SurveyApplication

class NewApplicationsAdapter(
    private val items: MutableList<SurveyApplication>,
    private val onItemClick: (SurveyApplication) -> Unit,
    private val onAccept: (SurveyApplication, Int) -> Unit,
    private val onReject: (SurveyApplication, Int) -> Unit,
) : RecyclerView.Adapter<NewApplicationsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.app_title)
        val cityText: TextView = itemView.findViewById(R.id.app_city)
        val statusLayout: View = itemView.findViewById(R.id.status_layout)
        val statusDot: View = itemView.findViewById(R.id.status_dot)
        val statusText: TextView = itemView.findViewById(R.id.app_status)
        val buttonsLayout: View = itemView.findViewById(R.id.admin_buttons_layout)
        val btnAccept: Button = itemView.findViewById(R.id.btn_accept)
        val btnReject: Button = itemView.findViewById(R.id.btn_reject)

        fun bind(item: SurveyApplication, position: Int) {
            titleText.text = item.title
            if (item.userCity.isNotBlank()) {
                cityText.text = itemView.context.getString(R.string.applicant_city) + " " + item.userCity
                cityText.isVisible = true
            } else {
                cityText.isVisible = false
            }

            when (item.status) {
                ApplicationStatus.PENDING -> {
                    statusLayout.isVisible = false
                    buttonsLayout.isVisible = true
                }
                ApplicationStatus.ACCEPTED -> {
                    statusLayout.isVisible = true
                    statusText.text = item.status.displayName
                    statusText.setTextColor(Color.parseColor("#4CAF50"))
                    statusDot.background.setTint(Color.parseColor("#4CAF50"))
                    buttonsLayout.isVisible = false
                }
                ApplicationStatus.REJECTED -> {
                    statusLayout.isVisible = true
                    statusText.text = item.status.displayName
                    statusText.setTextColor(Color.parseColor("#DD2C00"))
                    statusDot.background.setTint(Color.parseColor("#DD2C00"))
                    buttonsLayout.isVisible = false
                }
                ApplicationStatus.CANCELLED -> {
                    statusLayout.isVisible = false
                    buttonsLayout.isVisible = false
                }
            }

            btnAccept.setOnClickListener { onAccept(item, position) }
            btnReject.setOnClickListener { onReject(item, position) }
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_application, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<SurveyApplication>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
