package com.example.util

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

object QuranAudioPlayer {
    private const val TAG = "QuranAudioPlayer"
    private var mediaPlayer: MediaPlayer? = null

    var currentSurahNo: Int? = null
        private set
    var currentAyahNo: Int? = null
        private set
    var isPlaying: Boolean = false
        private set
    var isLoading: Boolean = false
        private set

    private var onCompletionListener: (() -> Unit)? = null
    private var onErrorListener: ((String) -> Unit)? = null

    fun playAyah(
        surahNo: Int,
        ayahNo: Int,
        onStart: () -> Unit = {},
        onCompletion: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        stop()

        currentSurahNo = surahNo
        currentAyahNo = ayahNo
        isLoading = true
        onCompletionListener = onCompletion
        onErrorListener = onError

        val formattedSurah = String.format("%03d", surahNo)
        val formattedAyah = String.format("%03d", ayahNo)
        val audioUrl = "https://everyayah.com/data/Alafasy_128kbps/$formattedSurah$formattedAyah.mp3"

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    QuranAudioPlayer.isLoading = false
                    QuranAudioPlayer.isPlaying = true
                    mp.start()
                    Log.d(TAG, "Playing Ayah audio: $audioUrl")
                    onStart()
                }
                setOnCompletionListener {
                    QuranAudioPlayer.isPlaying = false
                    QuranAudioPlayer.isLoading = false
                    Log.d(TAG, "Completed Ayah $surahNo:$ayahNo")
                    onCompletionListener?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    QuranAudioPlayer.isLoading = false
                    QuranAudioPlayer.isPlaying = false
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    onErrorListener?.invoke("Unable to play recitation. Please check your internet connection.")
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            isLoading = false
            isPlaying = false
            Log.e(TAG, "Failed to start recitation", e)
            onError("Error loading audio: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                isPlaying = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Pause error", e)
        }
    }

    fun resume() {
        try {
            if (mediaPlayer != null && !isPlaying) {
                mediaPlayer?.start()
                isPlaying = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Resume error", e)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Stop error", e)
        } finally {
            mediaPlayer = null
            isPlaying = false
            isLoading = false
            currentSurahNo = null
            currentAyahNo = null
        }
    }
}
