package com.propdf.editor.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.propdf.editor.BuildConfig
import com.propdf.editor.R
import com.propdf.editor.databinding.ActivitySettingsBinding
import com.propdf.editor.domain.repository.SettingsRepository
import com.propdf.editor.utils.DeviceCapabilities
import com.propdf.editor.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSettings()
        loadSettings()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)
    }

    private fun setupSettings() {
        // Dark Mode
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsRepository.setDarkMode(isChecked)
            }
        }

        // Low Memory Mode
        binding.switchLowMemory.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsRepository.setLowMemoryMode(isChecked)
            }
        }

        // Clear Cache
        binding.btnClearCache.setOnClickListener {
            lifecycleScope.launch {
                val success = FileUtils.clearCache(this@SettingsActivity)
                if (success) {
                    Toast.makeText(this@SettingsActivity, R.string.cache_cleared, Toast.LENGTH_SHORT).show()
                    updateCacheSize()
                }
            }
        }

        // Open Source Licenses
        binding.btnOpenSource.setOnClickListener {
            startActivity(Intent(this, OpenSourceLicensesActivity::class.java))
        }

        // Version info
        binding.tvVersion.text = getString(R.string.version_format, BuildConfig.VERSION_NAME)
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            val darkMode = settingsRepository.isDarkModeEnabled()
            binding.switchDarkMode.isChecked = darkMode

            val lowMemory = settingsRepository.isLowMemoryModeEnabled()
            binding.switchLowMemory.isChecked = lowMemory

            updateCacheSize()
        }
    }

    private fun updateCacheSize() {
        val size = FileUtils.getAppCacheSize(this)
        binding.tvCacheSize.text = FileUtils.formatFileSize(size)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
