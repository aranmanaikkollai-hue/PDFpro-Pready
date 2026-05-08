package com.propdf.editor.ads

import android.content.Context
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var adView: AdView? = null

    fun loadBanner(container: ViewGroup, adUnitId: String) {
        removeBanner()
        adView = AdView(context).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId
            loadAd(AdRequest.Builder().build())
        }
        container.removeAllViews()
        container.addView(adView)
    }

    fun removeBanner() {
        adView?.destroy()
        adView = null
    }

    fun pause() = adView?.pause()
    fun resume() = adView?.resume()
}
