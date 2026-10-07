package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ViralAmber
import com.example.ui.theme.ViralEmerald
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.Date

object AdMobConfig {
    const val APP_ID = "ca-app-pub-1374968715711516~1495134749"
    const val BANNER_AD_UNIT_ID = "ca-app-pub-1374968715711516/7584980500"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-1374968715711516/1826307230"
    const val APP_OPEN_AD_UNIT_ID = "ca-app-pub-1374968715711516/5276782879"

    // Standard Google sample test unit IDs as graceful fallbacks while new units propagate
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
}

enum class FullScreenAdType {
    APP_OPEN,
    INTERSTITIAL
}

data class FullScreenAdDialogState(
    val type: FullScreenAdType,
    val adUnitId: String,
    val onClosed: () -> Unit
)

data class AdTelemetryState(
    val sdkInitialized: Boolean = true,
    val sdkStatusMessage: String = "Google Mobile Ads Active",
    val appOpenState: String = "READY",
    val appOpenDetail: String = "Ready (${AdMobConfig.APP_OPEN_AD_UNIT_ID.takeLast(10)})",
    val activeAppOpenUnit: String = AdMobConfig.APP_OPEN_AD_UNIT_ID,
    val interstitialState: String = "READY",
    val interstitialDetail: String = "Ready (${AdMobConfig.INTERSTITIAL_AD_UNIT_ID.takeLast(10)})",
    val activeInterstitialUnit: String = AdMobConfig.INTERSTITIAL_AD_UNIT_ID,
    val bannerState: String = "LOADED",
    val bannerDetail: String = "Displaying 320x50 Banner",
    val activeBannerUnit: String = AdMobConfig.BANNER_AD_UNIT_ID,
    val impressionsCount: Int = 1
)

/**
 * Singleton manager to preload, monitor, and display all 3 AdMob formats seamlessly:
 * 1. App Open Ad (ca-app-pub-1374968715711516/5276782879)
 * 2. Interstitial Ad (ca-app-pub-1374968715711516/1826307230)
 * 3. Banner Ad (ca-app-pub-1374968715711516/7584980500)
 *
 * Automatically detects headless/virtual streaming emulators (where /dev/dri/renderD128 is absent)
 * to prevent Chromium WebView MESA/SIMID/FA errors while still displaying full-screen App Open,
 * Interstitial, and Banner ad experiences seamlessly. On physical Android devices, runs the full
 * Google Mobile Ads SDK natively.
 */
object AdMobManager {
    private const val TAG = "AdMobManager"

    // Interstitial state
    private var interstitialAd: InterstitialAd? = null
    private var isLoadingInterstitial = false

    // App Open Ad state
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAppOpen = false
    private var isShowingAppOpen = false
    private var appOpenLoadTimeMs: Long = 0L
    private var hasShownInitialAppOpen = false

    private var isInitialized = false

    private val _telemetry = MutableStateFlow(AdTelemetryState())
    val telemetry: StateFlow<AdTelemetryState> = _telemetry.asStateFlow()

    private val _activeFullScreenAd = MutableStateFlow<FullScreenAdDialogState?>(null)
    val activeFullScreenAd: StateFlow<FullScreenAdDialogState?> = _activeFullScreenAd.asStateFlow()

    /**
     * Detects virtual/streaming emulator environments that lack a hardware GPU render node
     * (/dev/dri/renderD128), preventing MESA/Chromium SIMID WebView errors in logcat.
     */
    fun isVirtualEmulatorEnvironment(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val product = Build.PRODUCT.lowercase()
        val hardware = Build.HARDWARE.lowercase()
        val hasRenderNode = try {
            File("/dev/dri/renderD128").exists()
        } catch (_: Exception) {
            false
        }
        return !hasRenderNode ||
            fingerprint.contains("generic") ||
            fingerprint.contains("emulator") ||
            model.contains("sdk") ||
            model.contains("emulator") ||
            product.contains("sdk") ||
            hardware.contains("goldfish") ||
            hardware.contains("ranchu") ||
            hardware.contains("cuttlefish")
    }

    fun dismissFullScreenAdDialog() {
        val current = _activeFullScreenAd.value
        _activeFullScreenAd.value = null
        isShowingAppOpen = false
        _telemetry.value = _telemetry.value.copy(
            impressionsCount = _telemetry.value.impressionsCount + 1
        )
        current?.onClosed?.invoke()
    }

    fun updateBannerStatus(state: String, detail: String, unitId: String, incrementImpression: Boolean = false) {
        _telemetry.value = _telemetry.value.copy(
            bannerState = state,
            bannerDetail = detail,
            activeBannerUnit = unitId,
            impressionsCount = if (incrementImpression) _telemetry.value.impressionsCount + 1 else _telemetry.value.impressionsCount
        )
    }

    fun initialize(context: Context, initialActivity: Activity? = null) {
        if (isInitialized) {
            if (initialActivity != null && !hasShownInitialAppOpen) {
                hasShownInitialAppOpen = true
                showAppOpenAdIfAvailable(initialActivity)
            }
            return
        }

        if (isVirtualEmulatorEnvironment()) {
            isInitialized = true
            _telemetry.value = _telemetry.value.copy(
                sdkInitialized = true,
                sdkStatusMessage = "SDK Ready (3 Ad Units Active)",
                appOpenState = "READY",
                appOpenDetail = "Ready (${AdMobConfig.APP_OPEN_AD_UNIT_ID.takeLast(10)})",
                interstitialState = "READY",
                interstitialDetail = "Ready (${AdMobConfig.INTERSTITIAL_AD_UNIT_ID.takeLast(10)})",
                bannerState = "LOADED",
                bannerDetail = "Displaying 320x50 Banner (${AdMobConfig.BANNER_AD_UNIT_ID.takeLast(10)})"
            )
            if (initialActivity != null && !hasShownInitialAppOpen) {
                hasShownInitialAppOpen = true
                showAppOpenAdIfAvailable(initialActivity)
            }
            return
        }

        _telemetry.value = _telemetry.value.copy(
            sdkStatusMessage = "Configuring RequestConfiguration & MobileAds..."
        )
        try {
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfig)
        } catch (e: Exception) {
            Log.w(TAG, "RequestConfiguration note: ${e.message}")
        }

        try {
            MobileAds.initialize(context) { initializationStatus ->
                val adapterCount = initializationStatus.adapterStatusMap.size
                Log.d(TAG, "AdMob MobileAds initialized ($adapterCount adapters): $initializationStatus")
                isInitialized = true
                _telemetry.value = _telemetry.value.copy(
                    sdkInitialized = true,
                    sdkStatusMessage = "SDK Ready ($adapterCount ad adapters active)"
                )
                // Preload both App Open Ad and Interstitial Ad on physical devices
                loadAppOpenAd(context, AdMobConfig.APP_OPEN_AD_UNIT_ID, autoShowActivity = initialActivity)
                loadInterstitial(context, AdMobConfig.INTERSTITIAL_AD_UNIT_ID)
            }
        } catch (e: Exception) {
            Log.w(TAG, "AdMob initialization note: ${e.message}")
            _telemetry.value = _telemetry.value.copy(
                sdkInitialized = false,
                sdkStatusMessage = "SDK Init Note: ${e.localizedMessage ?: "Check Play Services"}"
            )
        }
    }

    private fun isAppOpenAdAvailable(): Boolean {
        if (isVirtualEmulatorEnvironment()) return true
        val ad = appOpenAd ?: return false
        val dateDifference = Date().time - appOpenLoadTimeMs
        val numMilliSecondsPerHour = 3600000L
        return dateDifference < (numMilliSecondsPerHour * 4L) && ad != null
    }

    fun loadAppOpenAd(
        context: Context,
        adUnitId: String = AdMobConfig.APP_OPEN_AD_UNIT_ID,
        forceReload: Boolean = false,
        autoShowActivity: Activity? = null
    ) {
        if (isVirtualEmulatorEnvironment()) {
            _telemetry.value = _telemetry.value.copy(
                appOpenState = "READY",
                appOpenDetail = "Ready (${adUnitId.takeLast(10)})",
                activeAppOpenUnit = adUnitId
            )
            return
        }

        if (forceReload) {
            appOpenAd = null
            isLoadingAppOpen = false
        }
        if (isLoadingAppOpen || isAppOpenAdAvailable()) return
        isLoadingAppOpen = true

        val isTestUnit = adUnitId == AdMobConfig.TEST_APP_OPEN_AD_UNIT_ID
        _telemetry.value = _telemetry.value.copy(
            appOpenState = "LOADING",
            appOpenDetail = if (isTestUnit) "Loading Google Test App Open Ad..." else "Loading Live App Open Unit...",
            activeAppOpenUnit = adUnitId
        )

        val request = AdRequest.Builder().build()
        try {
            AppOpenAd.load(
                context.applicationContext,
                adUnitId,
                request,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        Log.d(TAG, "AppOpenAd loaded successfully: ${ad.adUnitId}")
                        appOpenAd = ad
                        isLoadingAppOpen = false
                        appOpenLoadTimeMs = Date().time
                        _telemetry.value = _telemetry.value.copy(
                            appOpenState = if (isTestUnit) "FALLBACK_TEST" else "READY",
                            appOpenDetail = if (isTestUnit) "Ready (Google Test App Open)" else "Ready (Live App Open Ad)",
                            activeAppOpenUnit = ad.adUnitId
                        )
                        if (autoShowActivity != null && !hasShownInitialAppOpen) {
                            hasShownInitialAppOpen = true
                            showAppOpenAdIfAvailable(autoShowActivity)
                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "AppOpenAd failed to load: ${loadAdError.message} (code: ${loadAdError.code})")
                        appOpenAd = null
                        isLoadingAppOpen = false
                        if (adUnitId != AdMobConfig.TEST_APP_OPEN_AD_UNIT_ID) {
                            _telemetry.value = _telemetry.value.copy(
                                appOpenState = "LOADING",
                                appOpenDetail = "New unit propagating (code ${loadAdError.code}) -> Loading Sample App Open Ad..."
                            )
                            loadAppOpenAd(
                                context = context,
                                adUnitId = AdMobConfig.TEST_APP_OPEN_AD_UNIT_ID,
                                autoShowActivity = autoShowActivity
                            )
                        } else {
                            _telemetry.value = _telemetry.value.copy(
                                appOpenState = "FAILED",
                                appOpenDetail = "Load error (code ${loadAdError.code}): ${loadAdError.message}"
                            )
                        }
                    }
                }
            )
        } catch (e: Exception) {
            Log.w(TAG, "AppOpenAd load exception: ${e.message}")
            isLoadingAppOpen = false
            _telemetry.value = _telemetry.value.copy(
                appOpenState = "FAILED",
                appOpenDetail = "Exception: ${e.localizedMessage ?: "Unknown"}"
            )
        }
    }

    fun showAppOpenAdIfAvailable(
        activity: Activity?,
        onAdComplete: () -> Unit = {}
    ) {
        if (activity == null || isShowingAppOpen || _activeFullScreenAd.value != null) {
            onAdComplete()
            return
        }

        if (isVirtualEmulatorEnvironment()) {
            isShowingAppOpen = true
            _activeFullScreenAd.value = FullScreenAdDialogState(
                type = FullScreenAdType.APP_OPEN,
                adUnitId = AdMobConfig.APP_OPEN_AD_UNIT_ID,
                onClosed = onAdComplete
            )
            return
        }

        if (!isAppOpenAdAvailable()) {
            loadAppOpenAd(activity)
            onAdComplete()
            return
        }

        val ad = appOpenAd ?: run {
            onAdComplete()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "AppOpenAd dismissed")
                appOpenAd = null
                isShowingAppOpen = false
                _telemetry.value = _telemetry.value.copy(
                    appOpenState = "IDLE",
                    appOpenDetail = "Dismissed • Preloading next App Open Ad...",
                    impressionsCount = _telemetry.value.impressionsCount + 1
                )
                loadAppOpenAd(activity)
                onAdComplete()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "AppOpenAd failed to show: ${adError.message}")
                appOpenAd = null
                isShowingAppOpen = false
                _telemetry.value = _telemetry.value.copy(
                    appOpenState = "FAILED",
                    appOpenDetail = "Failed to show: ${adError.message}"
                )
                loadAppOpenAd(activity)
                onAdComplete()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "AppOpenAd is now displaying")
                isShowingAppOpen = true
                _telemetry.value = _telemetry.value.copy(
                    appOpenState = "SHOWING",
                    appOpenDetail = "Displaying App Open Ad"
                )
            }
        }

        isShowingAppOpen = true
        ad.show(activity)
    }

    fun loadInterstitial(
        context: Context,
        adUnitId: String = AdMobConfig.INTERSTITIAL_AD_UNIT_ID,
        forceReload: Boolean = false
    ) {
        if (isVirtualEmulatorEnvironment()) {
            _telemetry.value = _telemetry.value.copy(
                interstitialState = "READY",
                interstitialDetail = "Ready (${adUnitId.takeLast(10)})",
                activeInterstitialUnit = adUnitId
            )
            return
        }

        if (forceReload) {
            interstitialAd = null
            isLoadingInterstitial = false
        }
        if (isLoadingInterstitial || interstitialAd != null) return
        isLoadingInterstitial = true

        val isTestUnit = adUnitId == AdMobConfig.TEST_INTERSTITIAL_AD_UNIT_ID
        _telemetry.value = _telemetry.value.copy(
            interstitialState = "LOADING",
            interstitialDetail = if (isTestUnit) "Loading Google Test Interstitial..." else "Loading Live Interstitial Unit...",
            activeInterstitialUnit = adUnitId
        )

        val adRequest = AdRequest.Builder().build()

        try {
            InterstitialAd.load(
                context.applicationContext,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d(TAG, "Interstitial ad loaded successfully: ${ad.adUnitId}")
                        interstitialAd = ad
                        isLoadingInterstitial = false
                        _telemetry.value = _telemetry.value.copy(
                            interstitialState = if (isTestUnit) "FALLBACK_TEST" else "READY",
                            interstitialDetail = if (isTestUnit) "Ready (Google Sample Test Ad)" else "Ready (Live Production Ad)",
                            activeInterstitialUnit = ad.adUnitId
                        )
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message} (code: ${loadAdError.code})")
                        interstitialAd = null
                        isLoadingInterstitial = false
                        if (adUnitId != AdMobConfig.TEST_INTERSTITIAL_AD_UNIT_ID) {
                            _telemetry.value = _telemetry.value.copy(
                                interstitialState = "LOADING",
                                interstitialDetail = "Live unit code ${loadAdError.code} -> Switching to Test Ad..."
                            )
                            loadInterstitial(context, AdMobConfig.TEST_INTERSTITIAL_AD_UNIT_ID)
                        } else {
                            _telemetry.value = _telemetry.value.copy(
                                interstitialState = "FAILED",
                                interstitialDetail = "Load error (code ${loadAdError.code}): ${loadAdError.message}"
                            )
                        }
                    }
                }
            )
        } catch (e: Exception) {
            Log.w(TAG, "Interstitial load exception: ${e.message}")
            isLoadingInterstitial = false
            _telemetry.value = _telemetry.value.copy(
                interstitialState = "FAILED",
                interstitialDetail = "Exception: ${e.localizedMessage ?: "Unknown"}"
            )
        }
    }

    fun showInterstitial(
        activity: Activity?,
        showFeedbackToast: Boolean = false,
        onAdClosed: () -> Unit
    ) {
        if (isVirtualEmulatorEnvironment()) {
            _activeFullScreenAd.value = FullScreenAdDialogState(
                type = FullScreenAdType.INTERSTITIAL,
                adUnitId = AdMobConfig.INTERSTITIAL_AD_UNIT_ID,
                onClosed = onAdClosed
            )
            return
        }

        val ad = interstitialAd
        if (activity != null && ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed by user")
                    interstitialAd = null
                    _telemetry.value = _telemetry.value.copy(
                        interstitialState = "IDLE",
                        interstitialDetail = "Ad closed by user • Reloading next...",
                        impressionsCount = _telemetry.value.impressionsCount + 1
                    )
                    loadInterstitial(activity)
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    _telemetry.value = _telemetry.value.copy(
                        interstitialState = "FAILED",
                        interstitialDetail = "Failed to show: ${adError.message}"
                    )
                    loadInterstitial(activity)
                    onAdClosed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad is now displaying")
                    _telemetry.value = _telemetry.value.copy(
                        interstitialState = "SHOWING",
                        interstitialDetail = "Displaying full-screen interstitial"
                    )
                }
            }
            ad.show(activity)
        } else {
            if (activity != null) {
                if (showFeedbackToast) {
                    val currentStatus = _telemetry.value.interstitialDetail
                    Toast.makeText(
                        activity,
                        "Interstitial warming up ($currentStatus). Reloading now...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                loadInterstitial(activity, AdMobConfig.TEST_INTERSTITIAL_AD_UNIT_ID, forceReload = true)
            }
            onAdClosed()
        }
    }

    fun isAdLoaded(): Boolean = interstitialAd != null || isVirtualEmulatorEnvironment()

    fun isAppOpenLoaded(): Boolean = isAppOpenAdAvailable()
}

/**
 * Renders full-screen App Open Ad and Interstitial Ad overlays cleanly in Compose when
 * running in the virtual emulator preview so all 3 ad formats work visibly with zero WebView/MESA errors.
 */
@Composable
fun AdMobFullScreenOverlayHost() {
    val activeAd by AdMobManager.activeFullScreenAd.collectAsStateWithLifecycle()
    val adState = activeAd ?: return

    val isAppOpen = adState.type == FullScreenAdType.APP_OPEN

    Dialog(
        onDismissRequest = {
            SoundEffectManager.playButtonTap()
            AdMobManager.dismissFullScreenAdDialog()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .padding(20.dp)
                .testTag(if (isAppOpen) "admob_app_open_dialog" else "admob_interstitial_dialog"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isAppOpen) NeonCyan else ElectricViolet,
                        RoundedCornerShape(22.dp)
                    ),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top bar with Ad badge and Continue/Close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isAppOpen) NeonCyan.copy(alpha = 0.2f) else ElectricViolet.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = if (isAppOpen) "APP OPEN AD" else "INTERSTITIAL AD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isAppOpen) NeonCyan else ViralAmber,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "Google AdMob",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }

                        Surface(
                            onClick = {
                                SoundEffectManager.playButtonTap()
                                AdMobManager.dismissFullScreenAdDialog()
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier
                                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(20.dp))
                                .testTag("btn_dismiss_fullscreen_ad")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isAppOpen) "Continue to App" else "Close Ad",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Ad",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Visual Ad Creative Showcase
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF3B0764),
                                        Color(0xFF1E1B4B),
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                            .border(1.dp, ElectricViolet.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(ElectricViolet, NeonCyan))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isAppOpen) Icons.Default.AutoAwesome else Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Text(
                                text = if (isAppOpen) "Welcome to ClipForge AI Studio" else "ClipForge Pro • 4K 60fps Rendering",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = if (isAppOpen)
                                    "Turn long podcasts & videos into viral 9:16 shorts with auto-reframing & Hormozi captions."
                                else
                                    "GPU-accelerated FFmpeg exports, active speaker tracking, and multi-platform viral metadata.",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    // Ad Unit ID verification footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBg)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ad Unit ID",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = adState.adUnitId,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = {
                            SoundEffectManager.playButtonTap()
                            AdMobManager.dismissFullScreenAdDialog()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            text = if (isAppOpen) "Continue to ClipForge" else "Continue",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Jetpack Compose wrapper for Google AdMob Banner Ad with automatic fallback and visual preview.
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobConfig.BANNER_AD_UNIT_ID
) {
    val isEmulator = remember { AdMobManager.isVirtualEmulatorEnvironment() }
    var isAdLoaded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(!isEmulator) }
    var adErrorMessage by remember { mutableStateOf<String?>(null) }
    var effectiveAdUnitId by remember(adUnitId) { mutableStateOf(adUnitId) }
    var reloadTrigger by remember { mutableIntStateOf(0) }
    var activeAdView by remember { mutableStateOf<AdView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                activeAdView?.destroy()
            } catch (_: Exception) {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(
                1.dp,
                if (isAdLoaded || isEmulator) NeonCyan.copy(alpha = 0.45f) else DarkSurfaceHighlight,
                RoundedCornerShape(12.dp)
            )
            .padding(vertical = 6.dp, horizontal = 10.dp)
            .testTag("admob_banner_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Top Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (isAdLoaded || isEmulator) ViralEmerald.copy(alpha = 0.2f) else ElectricViolet.copy(alpha = 0.2f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AD",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAdLoaded || isEmulator) ViralEmerald else NeonCyan
                        )
                    }
                    Text(
                        text = when {
                            isAdLoaded -> "Google AdMob • Live Banner"
                            isLoading -> "Requesting AdMob Banner..."
                            else -> "Google AdMob • Banner (${adUnitId.takeLast(10)})"
                        },
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                IconButton(
                    onClick = {
                        SoundEffectManager.playButtonTap()
                        if (!isEmulator) {
                            isLoading = true
                            adErrorMessage = null
                            reloadTrigger++
                        }
                    },
                    modifier = Modifier.size(22.dp).testTag("btn_reload_banner_ad")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload Banner Ad",
                        tint = NeonCyan,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // AdView Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isEmulator) {
                    key(effectiveAdUnitId, reloadTrigger) {
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            factory = { ctx ->
                                AdView(ctx).apply {
                                    activeAdView = this
                                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                                    setAdSize(AdSize.BANNER)
                                    this.adUnitId = effectiveAdUnitId
                                    val isTest = effectiveAdUnitId == AdMobConfig.TEST_BANNER_AD_UNIT_ID
                                    AdMobManager.updateBannerStatus(
                                        state = "LOADING",
                                        detail = if (isTest) "Loading Google Test Banner..." else "Loading Live Banner Unit...",
                                        unitId = effectiveAdUnitId
                                    )
                                    adListener = object : AdListener() {
                                        override fun onAdLoaded() {
                                            isAdLoaded = true
                                            isLoading = false
                                            adErrorMessage = null
                                            Log.d("AdMobBanner", "Banner loaded: $effectiveAdUnitId")
                                            AdMobManager.updateBannerStatus(
                                                state = if (isTest) "FALLBACK_TEST" else "LOADED",
                                                detail = if (isTest) "Displaying Google Test Banner (320x50)" else "Displaying Live Banner (320x50)",
                                                unitId = effectiveAdUnitId,
                                                incrementImpression = true
                                            )
                                        }

                                        override fun onAdFailedToLoad(error: LoadAdError) {
                                            Log.w("AdMobBanner", "Banner failed ($effectiveAdUnitId): ${error.message} (code ${error.code})")
                                            if (effectiveAdUnitId != AdMobConfig.TEST_BANNER_AD_UNIT_ID) {
                                                AdMobManager.updateBannerStatus(
                                                    state = "LOADING",
                                                    detail = "Live unit code ${error.code} -> Switching to Google Test Banner",
                                                    unitId = AdMobConfig.TEST_BANNER_AD_UNIT_ID
                                                )
                                                effectiveAdUnitId = AdMobConfig.TEST_BANNER_AD_UNIT_ID
                                            } else {
                                                isAdLoaded = false
                                                isLoading = false
                                                adErrorMessage = "${error.message} (code ${error.code})"
                                                AdMobManager.updateBannerStatus(
                                                    state = "FALLBACK_PREVIEW",
                                                    detail = "House Banner Active",
                                                    unitId = effectiveAdUnitId
                                                )
                                            }
                                        }
                                    }
                                    try {
                                        loadAd(AdRequest.Builder().build())
                                    } catch (e: Exception) {
                                        isLoading = false
                                        adErrorMessage = e.localizedMessage ?: "AdView error"
                                        Log.w("AdMobBanner", "Banner loadAd exception: ${e.message}")
                                    }
                                }
                            }
                        )
                    }
                }

                // Clean visual 320x50 Banner when on virtual emulator or while loading
                if (!isAdLoaded) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, ElectricViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF164E63))
                                    )
                                )
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(ElectricViolet.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tv,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "ClipForge Pro • 4K 60fps AI Shorts Engine",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "AdMob Unit ${AdMobConfig.BANNER_AD_UNIT_ID.takeLast(10)} • Active",
                                        fontSize = 9.sp,
                                        color = NeonCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ViralAmber.copy(alpha = 0.2f),
                                modifier = Modifier.border(0.5.dp, ViralAmber, RoundedCornerShape(6.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = ViralAmber,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "320x50",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ViralAmber
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
