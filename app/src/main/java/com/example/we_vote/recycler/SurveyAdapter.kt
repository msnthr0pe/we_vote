package com.example.we_vote.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.R
import com.example.we_vote.ktor.DTOs

class SurveyAdapter(private var surveys: List<DTOs.SurveyDTO>, val access: String?) :
    RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder>() {

    inner class SurveyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.recycler_title)
        val deleteBtn: Button = itemView.findViewById(R.id.delete_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_archive, parent, false)
        return SurveyViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurveyViewHolder, position: Int) {
        val item = surveys[position]
        holder.titleText.text = item.title
        if (access == "user") {
            holder.deleteBtn.isVisible = false
        }
    }

    override fun getItemCount(): Int = surveys.size

    fun updateList(newList: List<DTOs.SurveyDTO>) {
        surveys = newList
        notifyDataSetChanged()
    }
}