package com.example.myapplication.models

data class Question(
    val id: Int,
    val subjectId: Int,
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)