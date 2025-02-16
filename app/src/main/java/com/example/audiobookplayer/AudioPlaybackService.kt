package com.example.audiobookplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.utils.MediaConstants
import android.media.MediaMetadataRetriever
import com.google.firebase.firestore.FirebaseFirestore

class AudioPlaybackService : Service() {

    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var notificationManager: NotificationManager? = null
    private var currentFilePath: String? = null
    private var currentChapter = 1
    private val chapterMarks = mutableListOf<Int>()
    private var sleepTimer: CountDownTimer? = null
    private var sleepTimerActive = false
    private var sleepTimerMinutes = 0
    private var mediaSession: MediaSessionCompat? = null
    private var firestore: FirebaseFirestore? = null

    fun getCurrentChapter(): Int = currentChapter

    fun getMediaPlayerPosition(): Int = mediaPlayer?.currentPosition ?: 0
    
    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    inner class LocalBinder : Binder() {
        fun getService(): AudioPlaybackService = this@AudioPlaybackService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Initialize media session and other components
        mediaSession = MediaSessionCompat(this, "AudioPlaybackService").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or 
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            isActive = true
        }
        createMediaSession()
        initializeFirebase()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveState()
        mediaPlayer?.release()
        mediaPlayer = null
        mediaSession?.release()
    }

    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    fun getDuration(): Int = mediaPlayer?.duration ?: 0

    fun playAudio(filePath: String, position: Int = 0) {
        currentFilePath = filePath
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                seekTo(position)
                start()
            }
            isPlaying = true
        } else {
            mediaPlayer?.start()
            isPlaying = true
        }
        showNotification()
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
        isPlaying = false
        showNotification()
    }

    fun resumeAudio() {
        mediaPlayer?.start()
        isPlaying = true
        showNotification()
    }

    fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun nextChapter() {
        if (currentChapter < chapterMarks.size) {
            currentChapter++
            mediaPlayer?.seekTo(chapterMarks[currentChapter - 1])
            updateNotification("Chapter $currentChapter")
            saveState()
        }
    }

    fun previousChapter() {
        if (currentChapter > 1) {
            currentChapter--
            mediaPlayer?.seekTo(chapterMarks[currentChapter - 1])
            updateNotification("Chapter $currentChapter")
            saveState()
        }
    }

    fun setSleepTimer(minutes: Int) {
        cancelSleepTimer()
        sleepTimerActive = true
        sleepTimerMinutes = minutes
        sleepTimer = object : CountDownTimer(minutes * 60 * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateNotification("Sleeping in ${millisUntilFinished / 1000}s")
            }

            override fun onFinish() {
                pauseAudio()
                sleepTimerActive = false
                sleepTimerMinutes = 0
            }
        }.start()
    }

    fun cancelSleepTimer() {
        sleepTimer?.cancel()
        sleepTimer = null
        sleepTimerActive = false
        sleepTimerMinutes = 0
        showNotification()
    }

    fun loadChapterMarks(filePath: String) {
        chapterMarks.clear()
        try {
            val retriever = MediaMetadataRetriever().apply { setDataSource(filePath) }
            var chapterCount = 1
            
            while (true) {
                val id = "Chapter_$chapterCount"
                val startTime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.toIntOrNull()
                
                if (startTime == null) break
                
                chapterMarks.add((startTime * 1000).toInt()) // Convert seconds to milliseconds
                chapterCount++
            }
        } catch (e: Exception) {
            // Fallback to duration-based chapters
            val duration = mediaPlayer?.duration ?: 0
            if (duration > 0) {
                var position = 0
                while (position < duration) {
                    chapterMarks.add(position)
                    position += 10 * 60 * 1000 // 10 minutes
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Audio Playback"
            val descriptionText = "Controls for audiobook playback"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("audio_channel", name, importance).apply {
                description = descriptionText
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createMediaSession() {
        mediaSession = MediaSessionCompat(this, "AudioPlaybackService").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() = resumeAudio()
                override fun onPause() = pauseAudio()
                override fun onSkipToNext() = nextChapter()
                override fun onSkipToPrevious() = previousChapter()
                override fun onSeekTo(pos: Long) {
                    mediaPlayer?.seekTo(pos.toInt())
                }
            })
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            isActive = true
        }
    }

    private fun initializeFirebase() {
        firestore = FirebaseFirestore.getInstance()
        // Load saved state
        firestore?.collection("users")?.document("current")?.get()
            ?.addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data
                    val position = data?.get("position") as? Int ?: 0
                    val chapter = data?.get("chapter") as? Int ?: 1
                    val filePath = data?.get("filePath") as? String
                    if (filePath != null) {
                        currentChapter = chapter
                        playAudio(filePath, position)
                    }
                }
            }
    }

    companion object {
        const val ACTION_PROGRESS_UPDATE = "com.example.audiobookplayer.ACTION_PROGRESS_UPDATE"
        const val MEDIA_NOTIFICATION_ID = 1
    }

    fun broadcastProgressUpdate() {
        val intent = Intent(ACTION_PROGRESS_UPDATE).apply {
            putExtra("progress", getCurrentPosition())
        }
        sendBroadcast(intent)
    }

    private fun updateNotification(contentText: String) {
        notificationManager?.notify(MEDIA_NOTIFICATION_ID, createNotification(contentText))
    }

    private fun createNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, "audio_channel")
            .setContentTitle("Audiobook Player")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSession?.sessionToken)
            )
            .build()
    }

    fun saveState() {
        firestore?.collection("users")?.document("current")?.set(
            mapOf(
                "position" to mediaPlayer?.currentPosition,
                "chapter" to currentChapter,
                "filePath" to currentFilePath
            )
        )
    }

    fun getSleepTimerStatus(): Pair<Boolean, Int> = Pair(sleepTimerActive, sleepTimerMinutes)

    private fun showNotification() {
        updateNotification("Chapter $currentChapter")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_PAUSE" -> pauseAudio()
            "ACTION_PLAY" -> resumeAudio()
            "ACTION_PREVIOUS" -> previousChapter()
            "ACTION_NEXT" -> nextChapter()
        }
        return START_STICKY
    }
}
