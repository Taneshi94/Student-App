package com.example.myapplication.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentReviewBinding
import com.example.myapplication.models.Question
import com.example.myapplication.viewmodels.MainViewModel
import com.example.myapplication.viewmodels.ReviewViewModel

class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: ReviewViewModel by viewModels()

    private var rendering = false

    private val optionViews
        get() = listOf(binding.option0, binding.option1, binding.option2, binding.option3)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ẩn cho đến khi có câu hỏi đầu tiên, tránh nháy khung trống
        binding.root.visibility = View.INVISIBLE

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (rendering || checkedId == -1) return@setOnCheckedChangeListener
            val index = optionViews.indexOfFirst { it.id == checkedId }
            if (index >= 0) viewModel.selectOption(index)
        }
        binding.btnConfirm.setOnClickListener { viewModel.confirm() }
        binding.btnNext.setOnClickListener { viewModel.nextQuestion() }

        viewModel.question.observe(viewLifecycleOwner) { render() }
        viewModel.selectedIndex.observe(viewLifecycleOwner) { render() }
        viewModel.confirmed.observe(viewLifecycleOwner) { render() }

        // Đổi môn ở ô dropdown thì làm lại từ đầu với môn mới
        mainViewModel.selectedSubject.observe(viewLifecycleOwner) { viewModel.setSubject(it.id) }
    }

    private fun render() {
        val q = viewModel.question.value ?: return
        val selected = viewModel.selectedIndex.value
        val confirmed = viewModel.confirmed.value == true

        rendering = true
        binding.root.visibility = View.VISIBLE

        binding.tvQuestion.text = q.text

        optionViews.forEachIndexed { i, rb ->
            if (i < q.options.size) {
                rb.visibility = View.VISIBLE
                rb.text = "${'A' + i}. ${q.options[i]}"
                rb.isEnabled = !confirmed
                rb.setTextColor(optionColor(i, q, selected, confirmed))
            } else {
                rb.visibility = View.GONE
            }
        }
        if (selected == null) binding.radioGroup.clearCheck()
        else binding.radioGroup.check(optionViews[selected].id)

        if (confirmed) {
            val correct = selected == q.correctIndex
            binding.tvResult.visibility = View.VISIBLE
            binding.tvResult.text = if (correct) {
                getString(R.string.review_correct)
            } else {
                getString(R.string.review_wrong, "${'A' + q.correctIndex}. ${q.options[q.correctIndex]}")
            }
            binding.tvResult.setTextColor(
                ContextCompat.getColor(requireContext(), if (correct) R.color.correct_green else R.color.wrong_red)
            )
        } else {
            binding.tvResult.visibility = View.GONE
        }

        binding.btnConfirm.visibility = if (confirmed) View.GONE else View.VISIBLE
        binding.btnConfirm.isEnabled = selected != null
        binding.btnNext.visibility = if (confirmed) View.VISIBLE else View.GONE

        rendering = false
    }

    private fun optionColor(index: Int, q: Question, selected: Int?, confirmed: Boolean): Int {
        val res = when {
            !confirmed -> R.color.text_primary
            index == q.correctIndex -> R.color.correct_green
            index == selected -> R.color.wrong_red
            else -> R.color.text_primary
        }
        return ContextCompat.getColor(requireContext(), res)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}