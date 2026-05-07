package com.propdf.editor.ui.settings

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.propdf.editor.databinding.ActivityOpenSourceLicensesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpenSourceLicensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenSourceLicensesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenSourceLicensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadLicenses()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Open Source Licenses"
    }

    private fun loadLicenses() {
        val htmlContent = """
            <html>
            <head>
                <style>
                    body { font-family: sans-serif; padding: 16px; background: #0D0D0D; color: #FFFFFF; }
                    h2 { color: #FF6B35; }
                    h3 { color: #B0B0B0; }
                    pre { background: #1A1A1A; padding: 12px; border-radius: 8px; overflow-x: auto; }
                </style>
            </head>
            <body>
                <h2>MIT License</h2>
                <p>This application is licensed under the MIT License.</p>
                <pre>
Copyright (c) 2026 ProPDF Editor Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
                </pre>

                <h2>Third-Party Licenses</h2>
                <h3>AndroidX / Jetpack</h3>
                <p>Apache License 2.0 - Copyright The Android Open Source Project</p>

                <h3>PdfiumAndroid</h3>
                <p>Apache License 2.0 / BSD 3-Clause - Copyright Bartosz Schiller</p>

                <h3>Tesseract OCR</h3>
                <p>Apache License 2.0 - Copyright Apache Software Foundation</p>

                <h3>OkHttp</h3>
                <p>Apache License 2.0 - Copyright Square, Inc.</p>

                <h3>Dagger / Hilt</h3>
                <p>Apache License 2.0 - Copyright Google LLC</p>

                <h3>Kotlin Coroutines</h3>
                <p>Apache License 2.0 - Copyright JetBrains s.r.o.</p>
            </body>
            </html>
        """.trimIndent()

        binding.webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
