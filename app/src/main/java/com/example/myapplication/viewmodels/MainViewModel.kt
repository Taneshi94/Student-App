package com.example.myapplication.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.models.Subject

class MainViewModel : ViewModel() {

    val subjects = listOf(
        Subject(0, "Triết học"),
        Subject(1, "Kinh tế chính trị"),
        Subject(2, "CNXHKH"),
        Subject(3, "Lịch sử Đảng")
    )

    val selectedSubject = MutableLiveData(subjects[0])

    fun selectSubject(position: Int) {
        selectedSubject.value = subjects[position]
    }
}