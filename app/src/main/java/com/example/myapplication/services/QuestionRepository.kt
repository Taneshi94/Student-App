package com.example.myapplication.services

import android.content.Context
import com.example.myapplication.models.Question
import com.example.myapplication.models.QuestionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray

object QuestionRepository {

    private val seedMutex = Mutex()
    private var seeded = false

    suspend fun getQuestions(context: Context, subjectId: Int): List<Question> {
        val appContext = context.applicationContext
        val dao = AppDatabase.get(appContext).questionDao()
        ensureSeeded(appContext, dao)
        return dao.getBySubject(subjectId).map { it.toQuestion() }
    }

    /** Lần đầu chạy, nếu bảng trống thì nạp câu hỏi từ assets/questions.json. */
    private suspend fun ensureSeeded(context: Context, dao: QuestionDao) {
        seedMutex.withLock {
            if (!seeded) {
                if (dao.count() == 0) {
                    dao.insertAll(withContext(Dispatchers.IO) { readQuestionsFromAssets(context) })
                }
                seeded = true
            }
        }
    }

    private fun readQuestionsFromAssets(context: Context): List<QuestionEntity> {
        val json = context.assets.open("questions.json").bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        return List(array.length()) { i ->
            val o = array.getJSONObject(i)
            val options = o.getJSONArray("options")
            QuestionEntity(
                id = o.getInt("id"),
                subjectId = o.getInt("subjectId"),
                text = o.getString("text"),
                optionA = options.getString(0),
                optionB = options.getString(1),
                optionC = options.getString(2),
                optionD = options.getString(3),
                correctIndex = o.getInt("correctIndex")
            )
        }
    }
}