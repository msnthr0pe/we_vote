package com.example.we_vote.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.we_vote.R
import com.example.we_vote.RelationBar
import com.example.we_vote.ktor.DTOs
import com.google.android.material.button.MaterialButton

class MainSurveyAdapter(
    private var surveys: List<DTOs.SurveyDTO>,
    private val access: String?,
    private val votedIds: MutableSet<Int>,
    private val onVoteClick: (DTOs.SurveyDTO) -> Unit,
    private val onArchiveClick: (DTOs.SurveyDTO, Int, Int) -> Unit,
    private val onFetchResults: (surveyId: Int, onResult: (DTOs.SurveyVotesDTO?) -> Unit) -> Unit
) : RecyclerView.Adapter<MainSurveyAdapter.MainViewHolder>() {

    private val resultsCache = mutableMapOf<Int, DTOs.SurveyVotesDTO?>()
    private val expandedResults = mutableSetOf<Int>()

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.voting_card_title)
        val voteRow: LinearLayout = itemView.findViewById(R.id.vote_row)
        val voteBtn: MaterialButton = itemView.findViewById(R.id.vote_btn)
        val votedSection: LinearLayout = itemView.findViewById(R.id.voted_section)
        val alreadyVotedText: TextView = itemView.findViewById(R.id.already_voted_text)
        val currentResultsBtn: MaterialButton = itemView.findViewById(R.id.current_results_btn)
        val resultsSection: LinearLayout = itemView.findViewById(R.id.results_section)
        val bar1: RelationBar = itemView.findViewById(R.id.result_bar_1)
        val bar2: RelationBar = itemView.findViewById(R.id.result_bar_2)
        val bar3: RelationBar = itemView.findViewById(R.id.result_bar_3)
        val archiveBtn: ImageButton = itemView.findViewById(R.id.voting_archive_btn)

        fun bind(survey: DTOs.SurveyDTO, position: Int) {
            titleText.text = survey.title

            val hasVoted = survey.id in votedIds
            voteRow.isVisible = !hasVoted
            votedSection.isVisible = hasVoted

            if (hasVoted) {
                val isExpanded = survey.id in expandedResults
                resultsSection.isVisible = isExpanded
                currentResultsBtn.text = itemView.context.getString(
                    if (isExpanded) R.string.hide_results else R.string.current_results
                )

                if (isExpanded) {
                    val stats = resultsCache[survey.id]
                    bindBars(survey, stats)
                }

                currentResultsBtn.setOnClickListener {
                    val surveyId = survey.id
                    if (surveyId in resultsCache) {
                        if (surveyId in expandedResults) {
                            expandedResults.remove(surveyId)
                        } else {
                            expandedResults.add(surveyId)
                        }
                        notifyItemChanged(position)
                    } else {
                        onFetchResults(surveyId) { stats ->
                            resultsCache[surveyId] = stats
                            expandedResults.add(surveyId)
                            notifyItemChanged(position)
                        }
                    }
                }
            } else {
                voteBtn.setOnClickListener { onVoteClick(survey) }
            }

            val isAdmin = access == "admin" || access == "developer"
            archiveBtn.isVisible = isAdmin
            if (isAdmin) {
                archiveBtn.setOnClickListener {
                    onArchiveClick(survey, position, itemCount)
                }
            }
        }

        private fun bindBars(survey: DTOs.SurveyDTO, stats: DTOs.SurveyVotesDTO?) {
            bar1.titleText = survey.firstChoice
            bar1.progress = stats?.votesPercentage?.get(1) ?: 0

            bar2.titleText = survey.secondChoice
            bar2.progress = stats?.votesPercentage?.get(2) ?: 0

            bar3.titleText = survey.thirdChoice
            bar3.progress = stats?.votesPercentage?.get(3) ?: 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vote, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(surveys[position], position)
    }

    override fun getItemCount(): Int = surveys.size

    fun updateList(newList: List<DTOs.SurveyDTO>) {
        surveys = newList
        notifyDataSetChanged()
    }

    fun markVoted(surveyId: Int) {
        votedIds.add(surveyId)
        val position = surveys.indexOfFirst { it.id == surveyId }
        if (position >= 0) notifyItemChanged(position)
    }
}
