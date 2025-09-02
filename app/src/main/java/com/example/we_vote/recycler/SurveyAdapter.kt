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

class SurveyAdapter(private var surveys: List<DTOs.SurveyDTO>, val access: String?,
    private val onVoteClick: (DTOs.SurveyDTO) -> Unit) :
    RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder>() {

    inner class SurveyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.voting_card_title)
        val voteBtn: TextView = itemView.findViewById(R.id.vote_btn)
        val deleteBtn: Button = itemView.findViewById(R.id.voting_delete_btn)

        fun bind(item: DTOs.SurveyDTO) {
            this.titleText.text = item.title
            if (access == "user") {
                this.deleteBtn.isVisible = false
            }
            voteBtn.setOnClickListener {
                onVoteClick.invoke(item)
            }
        }

        init {
            voteBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onVoteClick(surveys[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vote, parent, false)
        return SurveyViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurveyViewHolder, position: Int) {
        holder.bind(surveys[position])
    }

    override fun getItemCount(): Int = surveys.size

    fun updateList(newList: List<DTOs.SurveyDTO>) {
        surveys = newList
        notifyDataSetChanged()
    }
}