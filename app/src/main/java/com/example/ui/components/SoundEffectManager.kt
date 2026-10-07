package com.example.ui.components

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.tanh

/**
 * Zero-latency acoustic synthesizer & in-memory AudioTrack engine for ClipForge.
 * Uses pure in-memory PCM AudioTrack (MODE_STATIC / low-latency stream) instead of SoundPool file decoding,
 * completely eliminating MediaCodec/C2 system resource query errors (resource error 6) on emulators.
 *
 * Generates two distinct, studio-grade 44.1kHz 16-bit PCM sounds at initialization:
 * 1. Button Tap: Crisp, punchy, tactile mechanical studio click (played at 0.88f volume, zero distortion).
 * 2. Tab Select: Subtle, warm, two-tone harmonic shimmer click for tab/mode selection (played at 0.84f volume).
 */
object SoundEffectManager {
    private const val TAG = "SoundEffectManager"
    private const val SAMPLE_RATE = 44100
    private const val BUTTON_VOLUME = 0.88f // 88% volume (within 80-90% target)
    private const val TAB_VOLUME = 0.84f    // 84% volume (within 80-90% target)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    private var buttonTapPcm: ShortArray = ShortArray(0)

    @Volatile
    private var tabSelectPcm: ShortArray = ShortArray(0)

    @Volatile
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            buttonTapPcm = synthesizeButtonTapPcm()
            tabSelectPcm = synthesizeTabSelectPcm()
            isInitialized = true
        } catch (e: Exception) {
            Log.w(TAG, "SoundEffectManager init note: ${e.message}")
        }
    }

    /**
     * Plays a crisp, punchy, satisfying tactile click for buttons and actions (~88% volume).
     */
    fun playButtonTap() {
        val pcm = if (buttonTapPcm.isNotEmpty()) buttonTapPcm else synthesizeButtonTapPcm().also { buttonTapPcm = it }
        playPcmAsync(pcm, BUTTON_VOLUME)
    }

    /**
     * Plays a subtle, distinct harmonic tick for navigation tabs and mode selectors (~84% volume).
     */
    fun playTabSelect() {
        val pcm = if (tabSelectPcm.isNotEmpty()) tabSelectPcm else synthesizeTabSelectPcm().also { tabSelectPcm = it }
        playPcmAsync(pcm, TAB_VOLUME)
    }

    private fun playPcmAsync(pcmData: ShortArray, volume: Float) {
        scope.launch {
            var track: AudioTrack? = null
            try {
                val byteLength = pcmData.size * 2
                val minBufSize = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(byteLength)

                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcmData, 0, pcmData.size)
                track.setVolume(volume)
                track.play()
            } catch (e: Exception) {
                Log.d(TAG, "AudioTrack playback skipped: ${e.message}")
                try {
                    track?.release()
                } catch (_: Exception) {}
                return@launch
            }

            // Release track cleanly after the short 45ms sample finishes playing
            kotlinx.coroutines.delay(85)
            try {
                track.stop()
            } catch (_: Exception) {}
            try {
                track.release()
            } catch (_: Exception) {}
        }
    }

    /**
     * Synthesizes a 36ms premium tactile button click:
     * - Ultra-fast 0.8ms attack envelope (no pop)
     * - Downward pitch-swept body (1350Hz -> 290Hz) for woody/mechanical thock
     * - Crisp 3100Hz harmonic transient for clarity
     * - Soft-clipped via tanh() so it never distorts even at high volume
     */
    internal fun synthesizeButtonTapPcm(): ShortArray {
        val durationSec = 0.036 // 36ms short, instant response
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        var phaseBody = 0.0
        var phaseClick = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE

            // Fast attack (0.8ms) to prevent DC click, followed by tight exponential decay
            val attack = (t / 0.0008).coerceAtMost(1.0)
            val bodyEnv = attack * exp(-t * 145.0)
            val clickEnv = attack * exp(-t * 420.0)

            // Pitch sweep from 1350Hz down to 290Hz for a rich, punchy mechanical feel
            val instantFreq = 290.0 + 1060.0 * exp(-t * 260.0)
            phaseBody += 2.0 * PI * instantFreq / SAMPLE_RATE
            phaseClick += 2.0 * PI * 3100.0 / SAMPLE_RATE

            val rawSignal = (sin(phaseBody) * 0.78 * bodyEnv) +
                (sin(phaseClick) * 0.38 * clickEnv)

            // Smooth tanh soft-saturation prevents any harsh digital clipping
            val cleanSignal = tanh(rawSignal * 1.35)
            samples[i] = (cleanSignal * Short.MAX_VALUE * 0.94).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
        }
        return samples
    }

    /**
     * Synthesizes a 44ms subtle, refined two-tone harmonic tab selection sound:
     * - Warm fundamental (660Hz -> 880Hz gentle upward inflection)
     * - Silky fifth harmonic overtone
     * - Soft, distortion-free envelope distinct from standard button taps
     */
    internal fun synthesizeTabSelectPcm(): ShortArray {
        val durationSec = 0.044 // 44ms
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val samples = ShortArray(numSamples)

        var phaseTone1 = 0.0
        var phaseTone2 = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE

            val attack = (t / 0.0012).coerceAtMost(1.0)
            val env1 = attack * exp(-t * 115.0)
            val env2 = attack * exp(-t * 190.0)

            // Subtle upward pitch inflection (660Hz -> 880Hz) for an uplifting tab switch feel
            val freq1 = 660.0 + 220.0 * (1.0 - exp(-t * 180.0))
            val freq2 = freq1 * 1.5 // Perfect fifth harmonic

            phaseTone1 += 2.0 * PI * freq1 / SAMPLE_RATE
            phaseTone2 += 2.0 * PI * freq2 / SAMPLE_RATE

            val rawSignal = (sin(phaseTone1) * 0.72 * env1) +
                (sin(phaseTone2) * 0.34 * env2)

            val cleanSignal = tanh(rawSignal * 1.25)
            samples[i] = (cleanSignal * Short.MAX_VALUE * 0.92).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
        }
        return samples
    }
}
