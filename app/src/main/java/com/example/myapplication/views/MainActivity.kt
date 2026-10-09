package com.example.myapplication.views

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Danh sách 4 môn học
        val subjects = arrayOf(
            "Triết học Mác-Lênin",
            "Kinh tế CT Mác-Lênin",
            "Chủ nghĩa XHKH Mác-Lênin",
            "Lịch sử Đảng"
        )

        // Thiết lập Adapter cho Dropdown
        val adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, subjects)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteSubject)

        autoCompleteTextView.setAdapter(adapter)
    }
}