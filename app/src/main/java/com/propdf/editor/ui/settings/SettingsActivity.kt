package com.propdf.editor.ui.settings

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.materialswitch.MaterialSwitch
import com.propdf.editor.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        val switchDark = findViewById<MaterialSwitch>(R.id.switchDarkMode)
        val switchLowMem = findViewById<MaterialSwitch>(R.id.switchLowMemory)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.darkMode.collect { switchDark.isChecked = it }
                }
                launch {
                    viewModel.lowMemory.collect { switchLowMem.isChecked = it }
                }
            }
        }

        switchDark.setOnCheckedChangeListener { _, checked ->
            viewModel.setDarkMode(checked)
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        switchLowMem.setOnCheckedChangeListener { _, checked -> viewModel.setLowMemory(checked) }
    }
}
