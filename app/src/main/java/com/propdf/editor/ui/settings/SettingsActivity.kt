package com.propdf.editor.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.propdf.editor.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}
