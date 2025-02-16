package com.example.audiobookplayer.data

import android.content.Context
import androidx.work.*
import com.example.audiobookplayer.common.enums.BookStatus
import com.example.audiobookplayer.domain.AudiobookRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@HiltWorker
class AudiobookScannerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: AudiobookRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val baseDir = File(applicationContext.filesDir, "My Audiobooks")
            if (!baseDir.exists()) return@withContext Result.success()

            val bookFolders = baseDir.listFiles()
                ?.filter { it.isDirectory && it.list()?.isNotEmpty() == true }
                ?: emptyList()

            for (folder in bookFolders) {
                val audioFiles = folder.listFiles()
                    ?.filter { it.isFile && it.extension.lowercase() in SUPPORTED_FORMATS }
                    ?: continue

                val totalDuration = calculateTotalDuration(audioFiles)
                val coverFile = findCoverImage(folder)

                val book = AudiobookEntity(
                    id = folder.absolutePath.hashCode().toString(),
                    title = folder.name,
                    author = extractAuthorFromFolderName(folder.name),
                    coverUri = coverFile?.absolutePath,
                    totalDuration = totalDuration,
                    currentPosition = 0L,
                    lastPlayed = System.currentTimeMillis(),
                    status = BookStatus.NEW
                )

                repository.upsertBook(book)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun calculateTotalDuration(audioFiles: List<File>): Long {
        // Placeholder for actual duration calculation logic
        // Would use ExoPlayer's MediaMetadataRetriever or similar
        return audioFiles.size * 60_000L // Temporary mock value
    }

    private fun findCoverImage(folder: File): File? {
        return folder.listFiles()
            ?.firstOrNull { it.isFile && it.nameWithoutExtension.equals("cover", ignoreCase = true) }
    }

    private fun extractAuthorFromFolderName(folderName: String): String? {
        return folderName.split("-").getOrNull(0)?.trim()
    }

    companion object {
        private val SUPPORTED_FORMATS = setOf("mp3", "m4a", "aac", "flac", "ogg")

        fun schedulePeriodicScan(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build()

            val request = PeriodicWorkRequestBuilder<AudiobookScannerWorker>(
                15, java.util.concurrent.TimeUnit.MINUTES
            ).setConstraints(constraints)
                .addTag("AudiobookScan")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "AudiobookScanner",
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }
    }
}
