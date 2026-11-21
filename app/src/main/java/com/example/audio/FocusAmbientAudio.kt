package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Generates gentle, continuous low-frequency brown noise offline for Focus Mode
 * without needing external audio assets or network requests.
 */
object FocusAmbientAudio {
    private var playJob: Job? = null
    private var audioTrack: AudioTrack? = null

    fun setPlaying(scope: CoroutineScope, enabled: Boolean) {
        if (!enabled) {
            stop()
            return
        }
        if (playJob?.isActive == true) return

        playJob = scope.launch(Dispatchers.IO) {
            val sampleRate = 22050
            val chunkSamples = 2048
            try {
                val minBuf = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(chunkSamples * 2)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuf)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.play()

                val buffer = ShortArray(chunkSamples)
                var lastOut = 0.0
                while (isActive) {
                    for (i in 0 until chunkSamples) {
                        val white = Random.nextDouble(-1.0, 1.0)
                        lastOut = (lastOut + (0.02 * white)) / 1.02
                        val sample = (lastOut * 14000.0).coerceIn(-30000.0, 30000.0)
                        buffer[i] = sample.toInt().toShort()
                    }
                    track.write(buffer, 0, chunkSamples)
                }
            } catch (_: Throwable) {
                // Safe no-op in Robolectric or headless audio environments
            } finally {
                releaseTrack()
            }
        }
    }

    fun stop() {
        playJob?.cancel()
        playJob = null
        releaseTrack()
    }

    private fun releaseTrack() {
        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
        } catch (_: Throwable) {
            // Safe cleanup
        } finally {
            audioTrack = null
        }
    }
}
