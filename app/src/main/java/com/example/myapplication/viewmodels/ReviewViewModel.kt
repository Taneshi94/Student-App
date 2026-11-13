package com.example.myapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.models.Question
import com.example.myapplication.services.QuestionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReviewViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        // Mọi 10 câu liên tiếp đều khác nhau => câu mới phải khác 9 câu liền trước
        private const val NO_REPEAT_WINDOW = 9
    }

    private var subjectId: Int? = null
    private var pool: List<Question> = emptyList()
    private val recentIds = mutableListOf<Int>()
    private var loadJob: Job? = null

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question> = _question

    private val _selectedIndex = MutableLiveData<Int?>(null)
    val selectedIndex: LiveData<Int?> = _selectedIndex

    private val _confirmed = MutableLiveData(false)
    val confirmed: LiveData<Boolean> = _confirmed

    /** Gọi khi vào màn hình hoặc khi đổi môn. Đổi môn thì nạp lại và làm lại từ đầu. */
    fun setSubject(id: Int) {
        if (id == subjectId) return
        subjectId = id
        recentIds.clear()

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            pool = QuestionRepository.getQuestions(getApplication<Application>(), id)
            nextQuestion()
        }
    }

    fun selectOption(index: Int) {
        if (_confirmed.value == true) return
        _selectedIndex.value = index
    }

    fun confirm() {
        if (_selectedIndex.value == null) return
        _confirmed.value = true
    }

    fun nextQuestion() {
        if (pool.isEmpty()) return

        // Nếu bộ câu hỏi ít hơn 10 câu thì cửa sổ tự thu nhỏ để vẫn chọn được
        val window = minOf(NO_REPEAT_WINDOW, pool.size - 1)
        val blocked = recentIds.takeLast(window)
        val chosen = pool.filter { it.id !in blocked }.random()

        recentIds.add(chosen.id)
        if (recentIds.size > NO_REPEAT_WINDOW) recentIds.removeAt(0)

        _question.value = chosen
        _selectedIndex.value = null
        _confirmed.value = false
    }
}