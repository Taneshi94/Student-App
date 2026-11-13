package com.example.myapplication.views

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.services.ThemeSettings
import com.google.android.material.materialswitch.MaterialSwitch
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var showingContent = false

    private val backCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = showMenu()
    }

    private fun showContent(title: String, fragment: Fragment) {
        showingContent = true
        backCallback.isEnabled = true
        binding.menuContainer.visibility = View.GONE
        binding.contentContainer.visibility = View.VISIBLE
        binding.tvTitle.text = title
        binding.imgArrow.visibility = View.VISIBLE
        binding.imgArrow.rotation = 90f   // mũi tên chỉ sang trái = quay lại
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentContainer, fragment)
            .commit()
    }

    private fun showMenu() {
        showingContent = false
        backCallback.isEnabled = false
        binding.contentContainer.visibility = View.GONE
        binding.menuContainer.visibility = View.VISIBLE
        binding.tvTitle.text = getString(R.string.study_politics)
        binding.imgArrow.visibility = View.INVISIBLE
        supportFragmentManager.findFragmentById(R.id.contentContainer)?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeSettings.applySaved(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, backCallback)
        binding.cardHeader.setOnClickListener { if (showingContent) showMenu() }
        binding.imgArrow.visibility = View.INVISIBLE
        setupDropdown()
        setupClicks()
        setupSettings()
    }

    private fun setupDropdown() {
        val names = viewModel.subjects.map { it.name }
        binding.dropdownSubject.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        )
        binding.dropdownSubject.setText(viewModel.selectedSubject.value?.name, false)
        binding.dropdownSubject.setOnItemClickListener { _, _, position, _ ->
            viewModel.selectSubject(position)
        }
    }

    private fun setupClicks() {
        binding.rowReview.setOnClickListener {
            showContent("Ôn tập", ReviewFragment())
        }
        binding.rowPractice.setOnClickListener {
            showContent("Luyện thi", ContentFragment.newInstance("Luyện thi - ${currentSubject()}"))
        }
        binding.rowHistory.setOnClickListener {
            showContent("Lịch sử thi", ContentFragment.newInstance("Lịch sử thi - ${currentSubject()}"))
        }
    }

    private fun setupSettings() {
        binding.btnSettings.setOnClickListener { anchor ->
            val popupView = LayoutInflater.from(this).inflate(R.layout.layout_settings_popup, null)

            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true // bấm ra ngoài để đóng popup
            )

            val switchDarkMode = popupView.findViewById<MaterialSwitch>(R.id.switchDarkMode)
            val tvSelectRingtone = popupView.findViewById<TextView>(R.id.tvSelectRingtone)

            // Đồng bộ nút gạt với chế độ đang áp dụng thật sự (kể cả khi đang theo hệ thống)
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            switchDarkMode.isChecked = (currentNightMode == Configuration.UI_MODE_NIGHT_YES)

            // Gạt xong chờ một chút cho nút kịp trượt, rồi mới đóng popup và đổi chế độ
            switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                popupView.postDelayed({
                    popupWindow.dismiss()
                    ThemeSettings.setDarkMode(this, isChecked)
                }, 250)
            }

            tvSelectRingtone.setOnClickListener {
                toast("Mở chọn nhạc chuông")
                // TODO: Thêm logic mở màn hình/dialog chọn nhạc chuông tại đây
                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(anchor, 0, 8)
        }
    }

    private fun currentSubject() = viewModel.selectedSubject.value?.name.orEmpty()

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

}