package com.example.myapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: Int,
    val subjectId: Int,
    val text: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int
) {
    fun toQuestion() = Question(
        id = id,
        subjectId = subjectId,
        text = text,
        options = listOf(optionA, optionB, optionC, optionD),
        correctIndex = correctIndex
    )
}