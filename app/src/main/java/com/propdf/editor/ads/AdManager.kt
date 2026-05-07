package com.propdf.editor.ads

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AdManager stub - Ads are disabled in this build.
 * To enable ads in future, integrate an ad SDK and replace this with full implementation.
 */
@Singleton
class AdManager @Inject constructor(
    private val context: Context
) {

    private val _bannerAdState = MutableStateFlow<AdLoadState>(AdLoadState.Disabled)
    val bannerAdState: StateFlow<AdLoadState> = _bannerAdState

    private val _nativeAdState = MutableStateFlow<AdLoadState>(AdLoadState.Disabled)
    val nativeAdState: StateFlow<AdLoadState> = _nativeAdState

    sealed class AdLoadState {
        object Loading : AdLoadState()
        data class Loaded(val ad: Any) : AdLoadState()
        object Failed : AdLoadState()
        object Disabled : AdLoadState()
    }

    fun loadBannerAd(container: FrameLayout) {
        // Ads disabled - hide container
        container.visibility = View.GONE
    }

    fun loadNativeAd(nativeAdView: Any) {
        // Ads disabled
    }

    fun showInterstitialIfReady(activity: Activity, onDismiss: () -> Unit) {
        // Ads disabled - immediately dismiss
        onDismiss()
    }
}
