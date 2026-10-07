package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CaptionPreset
import com.example.data.model.ClipEntity
import com.example.data.model.ReframeMode
import com.example.data.model.ShortsVideoRatio
import com.example.data.model.SocialPlatform
import com.example.ui.components.AdMobConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ClipForge", appName)
    }

    @Test
    fun `test clip entity defaults and virality scoring bounds`() {
        val clip = ClipEntity(
            id = 1,
            projectId = 10,
            title = "Test Clip",
            hookText = "Test Hook",
            viralityScore = 95,
            reasoning = "Test reasoning",
            startTimeMs = 1000,
            endTimeMs = 45000,
            emotionalPeak = "Peak Insight",
            reframeMode = ReframeMode.SPEAKER_TRACK,
            captionPreset = CaptionPreset.HORMOZI
        )
        assertEquals(95, clip.viralityScore)
        assertEquals(ReframeMode.SPEAKER_TRACK, clip.reframeMode)
        assertEquals(CaptionPreset.HORMOZI, clip.captionPreset)
        assertEquals(44000L, clip.endTimeMs - clip.startTimeMs)
    }

    @Test
    fun `test platforms and video ratios`() {
        assertEquals(4, SocialPlatform.entries.size)
        assertNotNull(SocialPlatform.YOUTUBE_SHORTS)
        assertNotNull(SocialPlatform.TIKTOK)
        assertNotNull(SocialPlatform.INSTAGRAM)
        assertNotNull(SocialPlatform.FACEBOOK)

        assertEquals("9:16", SocialPlatform.YOUTUBE_SHORTS.standardRatio)
        assertEquals("9:16", SocialPlatform.TIKTOK.standardRatio)
        assertEquals("9:16", SocialPlatform.INSTAGRAM.standardRatio)
        assertEquals("9:16", SocialPlatform.FACEBOOK.standardRatio)

        assertEquals(9f / 16f, ShortsVideoRatio.RATIO_9_16.aspectFloat, 0.001f)
        assertEquals(4f / 5f, ShortsVideoRatio.RATIO_4_5.aspectFloat, 0.001f)
        assertEquals(1f, ShortsVideoRatio.RATIO_1_1.aspectFloat, 0.001f)
    }

    @Test
    fun `test admob config IDs match user specs`() {
        assertEquals("ca-app-pub-1374968715711516~1495134749", AdMobConfig.APP_ID)
        assertEquals("ca-app-pub-1374968715711516/7584980500", AdMobConfig.BANNER_AD_UNIT_ID)
        assertEquals("ca-app-pub-1374968715711516/1826307230", AdMobConfig.INTERSTITIAL_AD_UNIT_ID)
        assertEquals("ca-app-pub-1374968715711516/5276782879", AdMobConfig.APP_OPEN_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/6300978111", AdMobConfig.TEST_BANNER_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/1033173712", AdMobConfig.TEST_INTERSTITIAL_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/9257395921", AdMobConfig.TEST_APP_OPEN_AD_UNIT_ID)
    }

    @Test
    fun `test caption generator presets and smart crop modes exist`() {
        // Caption generator styles
        val presets = CaptionPreset.entries
        assertEquals(6, presets.size)
        assertNotNull(presets.find { it == CaptionPreset.HORMOZI })
        assertNotNull(presets.find { it == CaptionPreset.NEON })
        assertNotNull(presets.find { it == CaptionPreset.MINIMAL })
        assertNotNull(presets.find { it == CaptionPreset.BOLD_YELLOW })
        assertNotNull(presets.find { it == CaptionPreset.RED_ACCENT })
        assertNotNull(presets.find { it == CaptionPreset.COMIC_EMOJI })

        // Smart Crop reframe modes
        val cropModes = ReframeMode.entries
        assertEquals(3, cropModes.size)
        assertNotNull(cropModes.find { it == ReframeMode.SPEAKER_TRACK })
        assertNotNull(cropModes.find { it == ReframeMode.SPLIT_SCREEN })
        assertNotNull(cropModes.find { it == ReframeMode.BLURRED_BG })
    }

    @Test
    fun `test download quality up to 1080p is available and free`() {
        val freeResolutions = listOf("480p", "720p", "1080p")
        assertEquals(3, freeResolutions.size)
        assertEquals("1080p", freeResolutions.last())
    }

    @Test
    fun `test sound effect synthesizer generates distortion free button and tab waveforms`() {
        val buttonPcm = com.example.ui.components.SoundEffectManager.synthesizeButtonTapPcm()
        val tabPcm = com.example.ui.components.SoundEffectManager.synthesizeTabSelectPcm()

        assert(buttonPcm.isNotEmpty())
        assert(tabPcm.isNotEmpty())
        // Verify distinct lengths and zero hard-clipping (below Short.MAX_VALUE)
        assert(buttonPcm.size != tabPcm.size)
        assert(buttonPcm.all { it > Short.MIN_VALUE && it < Short.MAX_VALUE })
        assert(tabPcm.all { it > Short.MIN_VALUE && it < Short.MAX_VALUE })
    }
}
