package com.example.we_vote

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class TitleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_title, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleTextView = view.findViewById<TextView>(R.id.titleText)
        val fullText = "Твой взгляд формирует завтрашний день"
        val spannableString = SpannableString(fullText)

        val wordToColor = "формирует"
        val startIndex = fullText.indexOf(wordToColor)
        if (startIndex != -1) {
            val endIndex = startIndex + wordToColor.length
            val color = Color.parseColor("#AEAF50")
            spannableString.setSpan(
                ForegroundColorSpan(color),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        titleTextView.text = spannableString

        // Если вы хотите, чтобы TitleFragment тоже висел какое-то время и переходил на Login:
        view.postDelayed({
            if (isAdded) {
                findNavController().navigate(R.id.action_titleFragment_to_loginFragment)
            }
        }, 3000)
    }
}