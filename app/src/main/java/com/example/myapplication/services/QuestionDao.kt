package com.example.myapplication.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.models.QuestionEntity

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE subjectId = :subjectId")
    suspend fun getBySubject(subjectId: Int): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)
}