package com.algokids.ads

import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.ResponseInfo

/** Ad response details used to distinguish no-fill from integration and presentation failures. */
internal object AdDiagnostics {
    private const val TAG = "AlgoKidsAds"

    fun loaded(format: String, responseInfo: ResponseInfo?) {
        Log.i(TAG, "$format onAdLoaded responseInfo=${responseInfo ?: "unavailable"}")
    }

    fun impression(format: String, responseInfo: ResponseInfo?) {
        Log.i(TAG, "$format onAdImpression responseInfo=${responseInfo ?: "unavailable"}")
    }

    fun dismissed(format: String, responseInfo: ResponseInfo?) {
        Log.i(TAG, "$format onAdDismissedFullScreenContent responseInfo=${responseInfo ?: "unavailable"}")
    }

    fun loadFailed(format: String, error: LoadAdError) {
        val kind = if (error.code == AdRequest.ERROR_CODE_NO_FILL) "NO_FILL" else "LOAD_ERROR"
        Log.w(
            TAG,
            "$format onAdFailedToLoad kind=$kind code=${error.code} domain=${error.domain} " +
                "message=${error.message} responseInfo=${error.responseInfo ?: "unavailable"} " +
                "cause=${error.cause ?: "unavailable"}"
        )
    }

    fun showFailed(format: String, error: AdError, responseInfo: ResponseInfo?) {
        Log.e(
            TAG,
            "$format onAdFailedToShowFullScreenContent code=${error.code} domain=${error.domain} " +
                "message=${error.message} responseInfo=${responseInfo ?: "unavailable"} " +
                "cause=${error.cause ?: "unavailable"}"
        )
    }
}
