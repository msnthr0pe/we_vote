package com.example.we_vote.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.R
import com.example.we_vote.ktor.DTOs

class ArchiveAdapter(
    items: List<ArchiveItem>,
    private val access: String?,
    private val onDeleteClick: (DTOs.SurveyDTO, Int, Int) -> Unit
) : RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder>() {

    private val items: MutableList<ArchiveItem> = items.toMutableList()

    data class ArchiveItem(
        val survey: DTOs.SurveyDTO,
        val stats: DTOs.SurveyVotesDTO?
    )

    inner class ArchiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.archive_card_title)
        val choice1: TextView = itemView.findViewById(R.id.archive_choice_1)
        val choice2: TextView = itemView.findViewById(R.id.archive_choice_2)
        val choice3: TextView = itemView.findViewById(R.id.archive_choice_3)
        val percent1: TextView = itemView.findViewById(R.id.archive_percent_1)
        val percent2: TextView = itemView.findViewById(R.id.archive_percent_2)
        val percent3: TextView = itemView.findViewById(R.id.archive_percent_3)
        val adminRow: LinearLayout = itemView.findViewById(R.id.archive_admin_row)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.archive_delete_btn)

        fun bind(item: ArchiveItem, position: Int) {
            title.text = item.survey.title

            val p1 = item.stats?.votesPercentage?.get(1) ?: 0
            val p2 = item.stats?.votesPercentage?.get(2) ?: 0
            val p3 = item.stats?.votesPercentage?.get(3) ?: 0
            val maxPercent = maxOf(p1, p2, p3)

            bindChoice(choice1, percent1, item.survey.firstChoice, p1, p1 == maxPercent && maxPercent > 0)
            bindChoice(choice2, percent2, item.survey.secondChoice, p2, p2 == maxPercent && maxPercent > 0)
            bindChoice(choice3, percent3, item.survey.thirdChoice, p3, p3 == maxPercent && maxPercent > 0)

            val isAdmin = access == "admin" || access == "developer"
            adminRow.isVisible = isAdmin
            if (isAdmin) {
                deleteBtn.setOnClickListener {
                    onDeleteClick(item.survey, position, itemCount)
                }
            }
        }

        private fun bindChoice(textView: TextView, percentView: TextView, text: String, percent: Int, isWinner: Boolean) {
            val color = if (isWinner) Color.parseColor("#4CAF50") else Color.WHITE
            textView.text = text
            textView.setTextColor(color)
            percentView.text = "$percent%"
            percentView.setTextColor(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_archive, parent, false)
        return ArchiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArchiveViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }
}
