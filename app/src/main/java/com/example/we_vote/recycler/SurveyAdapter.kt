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
    private val greenButtonText: String,
    private val redButtonText: String,
    private val onVoteClick: (DTOs.SurveyDTO) -> Unit,
    private val onArchiveClick: (DTOs.SurveyDTO, Int, Int) -> Unit) :
    RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder>() {

    inner class SurveyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.voting_card_title)
        val greenBtn: TextView = itemView.findViewById(R.id.vote_btn)
        val redBtn: Button = itemView.findViewById(R.id.voting_archive_btn)

        fun bind(item: DTOs.SurveyDTO, position: Int) {
            this.titleText.text = item.title
            greenBtn.text = greenButtonText
            redBtn.text = redButtonText
            if (access == "user") {
                this.redBtn.isVisible = false
            } else {
                redBtn.setOnClickListener {
                    onArchiveClick(item, position, itemCount)
                }
            }
            greenBtn.setOnClickListener {
                onVoteClick.invoke(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vote, parent, false)
        return SurveyViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurveyViewHolder, position: Int) {
        holder.bind(surveys[position], position)
    }

    override fun getItemCount(): Int = surveys.size

    fun updateList(newList: List<DTOs.SurveyDTO>) {
        surveys = newList
        notifyDataSetChanged()
    }
}