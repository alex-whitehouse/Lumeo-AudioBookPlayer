package com.example.audiobookplayer

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import com.example.audiobookplayer.AudioPlaybackService.Companion.ACTION_PROGRESS_UPDATE
import android.os.IBinder
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.widget.ImageButton
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }

    private var playbackService: AudioPlaybackService? = null
    private var playbackBound = false
    private var currentAudioUri: Uri? = null
    private var isPlaying = false
    private var sleepTimer: CountDownTimer? = null
    private val firestore = FirebaseFirestore.getInstance()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playbackService = (service as AudioPlaybackService.LocalBinder).getService()
            playbackBound = true
            updateUI()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playbackBound = false
        }
    }

    private val progressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioPlaybackService.ACTION_PROGRESS_UPDATE) {
                val progress = intent.getIntExtra("progress", 0)
                findViewById<SeekBar>(R.id.progressBar)?.progress = progress
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        val playPauseButton = findViewById<ImageButton>(R.id.btnPlayPause)
        val progressBar = findViewById<SeekBar>(R.id.progressBar)
        val chapterText = findViewById<TextView>(R.id.txtChapter)

        // Setup listeners
        playPauseButton.setOnClickListener { togglePlayback() }
        findViewById<View>(R.id.btnNext).setOnClickListener { playbackService?.nextChapter() }
        findViewById<View>(R.id.btnPrevious).setOnClickListener { playbackService?.previousChapter() }
        findViewById<View>(R.id.btnSleepTimer).setOnClickListener { showSleepTimerDialog() }
        findViewById<View>(R.id.btnOpenFile).setOnClickListener { openFilePicker() }

        progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    playbackService?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                playbackService?.saveState()
            }
        })

        // Bind to service
        val intent = Intent(this, AudioPlaybackService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        // Register broadcast receiver
        registerReceiver(progressReceiver, IntentFilter(AudioPlaybackService.ACTION_PROGRESS_UPDATE))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (playbackBound) {
            unbindService(serviceConnection)
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(progressReceiver)
    }

    private fun showSleepTimerDialog() {
        val items = arrayOf("15 minutes", "30 minutes", "45 minutes", "60 minutes")
        AlertDialog.Builder(this)
            .setTitle("Set Sleep Timer")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> setSleepTimer(15)
                    1 -> setSleepTimer(30)
                    2 -> setSleepTimer(45)
                    3 -> setSleepTimer(60)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun openFilePicker() {
        pickAudioFile.launch("audio/*")
    }

    private fun togglePlayback() {
        if (playbackBound) {
            if (isPlaying) {
                pauseAudio()
            } else {
                playAudio()
            }
        }
    }

    private fun playAudio() {
        currentAudioUri?.let { uri ->
            playbackService?.playAudio(uri.path!!, findViewById<SeekBar>(R.id.progressBar).progress)
            isPlaying = true
            findViewById<View>(R.id.btnPlayPause).setBackgroundResource(R.drawable.ic_pause)
        }
    }

    private fun pauseAudio() {
        playbackService?.pauseAudio()
        isPlaying = false
        findViewById<View>(R.id.btnPlayPause).setBackgroundResource(R.drawable.ic_play_arrow)
        cancelSleepTimer()
    }

    private fun setSleepTimer(minutes: Int) {
        cancelSleepTimer()
        playbackService?.setSleepTimer(minutes)
    }

    private fun cancelSleepTimer() {
        playbackService?.cancelSleepTimer()
    }

    private fun updateUI() {
        playbackService?.let { service ->
            val duration = service.getDuration()
            val currentPosition = service.getCurrentPosition()
            
            findViewById<SeekBar>(R.id.progressBar)?.apply {
                max = duration
                progress = currentPosition
            }
            
            findViewById<TextView>(R.id.txtChapter)?.text = "Chapter ${service.getCurrentChapter()}"
            findViewById<TextView>(R.id.txtTimeRemaining)?.text = 
                "${getString(R.string.time_remaining)}: ${formatTime(duration - currentPosition)}"
            
            // Update sleep timer display
            val (isTimerActive, minutesRemaining) = service.getSleepTimerStatus()
            if (isTimerActive) {
                findViewById<View>(R.id.sleepTimerContainer).visibility = View.VISIBLE
                findViewById<TextView>(R.id.txtSleepTimer)?.text = 
                    "${getString(R.string.sleep_timer_active)}: $minutesRemaining min"
            } else {
                findViewById<View>(R.id.sleepTimerContainer).visibility = View.GONE
            }
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = milliseconds / 60000
        val seconds = (milliseconds % 60000) / 1000
        return String.format("%d:%02d", minutes, seconds)
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openFilePicker()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Storage permission is required to access your audiobook files.")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private val pickAudioFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                currentAudioUri = it
                val filePath = getRealPathFromURI(it)
                playbackService?.loadChapterMarks(filePath)
                playbackService?.playAudio(filePath, 0)
                isPlaying = true
                findViewById<View>(R.id.btnPlayPause).setBackgroundResource(R.drawable.ic_pause)
                findViewById<TextView>(R.id.txtChapter)?.text = getFileName(it)
            }
        }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(android.provider.MediaStore.Audio.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return contentUri.path ?: ""
    }

    private fun getFileName(uri: Uri): String {
        return uri.lastPathSegment ?: "Unknown Book"
    }
}
