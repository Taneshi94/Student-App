package com.example.myapplication.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ContentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val text = arguments?.getString("text").orEmpty()
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 18f
            setPadding(48, 64, 48, 64)
        }
    }

    companion object {
        fun newInstance(text: String) = ContentFragment().apply {
            arguments = Bundle().apply { putString("text", text) }
        }
    }
}