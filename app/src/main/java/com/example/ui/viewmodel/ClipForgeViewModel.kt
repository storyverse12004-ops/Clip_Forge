package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ClipForgeDatabase
import com.example.data.model.CaptionPreset
import com.example.data.model.ClipEntity
import com.example.data.model.ExportEntity
import com.example.data.model.PlatformMetadataEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ReframeMode
import com.example.data.model.ShortsVideoRatio
import com.example.data.model.SocialPlatform
import com.example.data.model.SourceType
import com.example.data.model.WordTimestamp
import com.example.data.repository.ClipForgeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    data object Dashboard : ScreenDestination()
    data object Ingestion : ScreenDestination()
    data class Processing(val projectTitle: String) : ScreenDestination()
    data class ClipList(val projectId: Long) : ScreenDestination()
    data class Editor(val clipId: Long) : ScreenDestination()
    data class Metadata(val clipId: Long) : ScreenDestination()
    data object ArchitectureSpecs : ScreenDestination()
}

class ClipForgeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = ClipForgeDatabase.getDatabase(application)
    private val repository = ClipForgeRepository(
        projectDao = db.projectDao(),
        clipDao = db.clipDao(),
        metadataDao = db.platformMetadataDao(),
        exportDao = db.exportDao()
    )

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExports: StateFlow<List<ExportEntity>> = repository.allExports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Dashboard)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    // Processing state
    private val _processingStep = MutableStateFlow(0)
    val processingStep: StateFlow<Int> = _processingStep.asStateFlow()

    private val _processingStatusText = MutableStateFlow("")
    val processingStatusText: StateFlow<String> = _processingStatusText.asStateFlow()

    private val _processingProgress = MutableStateFlow(0f)
    val processingProgress: StateFlow<Float> = _processingProgress.asStateFlow()

    // Active Clip & Project
    private val _selectedClip = MutableStateFlow<ClipEntity?>(null)
    val selectedClip: StateFlow<ClipEntity?> = _selectedClip.asStateFlow()

    // Platform & Ratio features
    private val _selectedPlatform = MutableStateFlow(SocialPlatform.TIKTOK)
    val selectedPlatform: StateFlow<SocialPlatform> = _selectedPlatform.asStateFlow()

    private val _selectedRatio = MutableStateFlow(ShortsVideoRatio.RATIO_9_16)
    val selectedRatio: StateFlow<ShortsVideoRatio> = _selectedRatio.asStateFlow()

    private val _showPlatformUiOverlay = MutableStateFlow(true)
    val showPlatformUiOverlay: StateFlow<Boolean> = _showPlatformUiOverlay.asStateFlow()

    private val _showSafeZoneGuides = MutableStateFlow(true)
    val showSafeZoneGuides: StateFlow<Boolean> = _showSafeZoneGuides.asStateFlow()

    private val _selectedMetadata = MutableStateFlow<PlatformMetadataEntity?>(null)
    val selectedMetadata: StateFlow<PlatformMetadataEntity?> = _selectedMetadata.asStateFlow()

    private val _transcriptWords = MutableStateFlow<List<WordTimestamp>>(emptyList())
    val transcriptWords: StateFlow<List<WordTimestamp>> = _transcriptWords.asStateFlow()

    // Export Modal State
    private val _showExportModal = MutableStateFlow(false)
    val showExportModal: StateFlow<Boolean> = _showExportModal.asStateFlow()

    private val _exportQuality = MutableStateFlow("1080p")
    val exportQuality: StateFlow<String> = _exportQuality.asStateFlow()

    private val _exportFormat = MutableStateFlow("MP4")
    val exportFormat: StateFlow<String> = _exportFormat.asStateFlow()

    private val _exportFps = MutableStateFlow(60)
    val exportFps: StateFlow<Int> = _exportFps.asStateFlow()

    private val _exportBitrate = MutableStateFlow("Balanced")
    val exportBitrate: StateFlow<String> = _exportBitrate.asStateFlow()

    private val _exportBurnCaptions = MutableStateFlow(true)
    val exportBurnCaptions: StateFlow<Boolean> = _exportBurnCaptions.asStateFlow()

    private val _exportRemoveWatermark = MutableStateFlow(true)
    val exportRemoveWatermark: StateFlow<Boolean> = _exportRemoveWatermark.asStateFlow()

    private val _exportIncludeSrt = MutableStateFlow(true)
    val exportIncludeSrt: StateFlow<Boolean> = _exportIncludeSrt.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportProgress = MutableStateFlow(0)
    val exportProgress: StateFlow<Int> = _exportProgress.asStateFlow()

    private val _exportDownloadUrl = MutableStateFlow<String?>(null)
    val exportDownloadUrl: StateFlow<String?> = _exportDownloadUrl.asStateFlow()

    // Notification toast / snackbar
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var metadataJob: Job? = null

    fun navigateTo(dest: ScreenDestination) {
        _currentScreen.value = dest
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun selectPlatform(platform: SocialPlatform) {
        _selectedPlatform.value = platform
        _toastMessage.value = "Switched to ${platform.displayName} mode"
    }

    fun selectRatio(ratio: ShortsVideoRatio) {
        _selectedRatio.value = ratio
        _toastMessage.value = "Aspect ratio set to ${ratio.label} (${ratio.resolutionLabel})"
    }

    fun togglePlatformUiOverlay() {
        _showPlatformUiOverlay.value = !_showPlatformUiOverlay.value
    }

    fun toggleSafeZoneGuides() {
        _showSafeZoneGuides.value = !_showSafeZoneGuides.value
    }

    fun selectProject(projectId: Long) {
        _currentScreen.value = ScreenDestination.ClipList(projectId)
    }

    fun getClipsForProject(projectId: Long) = repository.getClipsForProject(projectId)

    fun loadClipForEditing(clipId: Long) {
        metadataJob?.cancel()
        metadataJob = viewModelScope.launch {
            val clip = repository.getClipById(clipId)
            _selectedClip.value = clip
            if (clip != null) {
                _transcriptWords.value = repository.getTranscriptWordsForClip(clipId)
            }
            repository.getMetadataForClip(clipId).collect {
                _selectedMetadata.value = it
            }
        }
    }

    fun updateClipReframeMode(mode: ReframeMode) {
        val current = _selectedClip.value ?: return
        val updated = current.copy(reframeMode = mode)
        _selectedClip.value = updated
        viewModelScope.launch { repository.updateClip(updated) }
    }

    fun updateCaptionPreset(preset: CaptionPreset) {
        val current = _selectedClip.value ?: return
        val updated = current.copy(captionPreset = preset)
        _selectedClip.value = updated
        viewModelScope.launch { repository.updateClip(updated) }
    }

    fun updateCaptionSize(fontSize: Int) {
        val current = _selectedClip.value ?: return
        val updated = current.copy(captionFontSize = fontSize)
        _selectedClip.value = updated
        viewModelScope.launch { repository.updateClip(updated) }
    }

    fun updateCaptionPosition(pos: String) {
        val current = _selectedClip.value ?: return
        val updated = current.copy(captionPosition = pos)
        _selectedClip.value = updated
        viewModelScope.launch { repository.updateClip(updated) }
    }

    fun toggleSilenceRemoval() {
        val current = _selectedClip.value ?: return
        val updated = current.copy(silenceRemoved = !current.silenceRemoved)
        _selectedClip.value = updated
        viewModelScope.launch { repository.updateClip(updated) }
    }

    fun updateBackgroundMusic(music: String, ducking: Int) {
        val current = _selectedClip.value ?: return
        val updated = current.copy(backgroundMusic = music, duckingPercent = ducking)
        _selectedClip.value = updated
        viewModelScope.launch { repository.updateClip(updated) }
    }

    fun updateTrimRange(startMs: Long, endMs: Long) {
        val current = _selectedClip.value ?: return
        val updated = current.copy(startTimeMs = startMs, endTimeMs = endMs)
        _selectedClip.value = updated
        viewModelScope.launch {
            repository.updateClip(updated)
            _toastMessage.value = "Trim updated: ${(startMs / 1000)}s - ${(endMs / 1000)}s"
        }
    }

    fun toggleCutWord(index: Int) {
        val list = _transcriptWords.value.toMutableList()
        if (index in list.indices) {
            val word = list[index]
            list[index] = word.copy(isCut = !word.isCut)
            _transcriptWords.value = list
            _toastMessage.value = if (list[index].isCut) "Word '${word.word}' cut from audio timeline" else "Word restored"
        }
    }

    // Launch pipeline
    fun startIngestionPipeline(
        title: String,
        sourceType: SourceType,
        sourceUrl: String
    ) {
        _currentScreen.value = ScreenDestination.Processing(title.ifBlank { "New Video Pipeline" })
        _processingStep.value = 0
        _processingProgress.value = 0.05f
        _processingStatusText.value = "Starting ingestion worker..."

        viewModelScope.launch {
            try {
                val newProjectId = repository.runVideoPipeline(
                    title = title,
                    sourceType = sourceType,
                    sourceUrl = sourceUrl
                ) { stepIndex, stepName, progress ->
                    _processingStep.value = stepIndex
                    _processingStatusText.value = stepName
                    _processingProgress.value = progress
                }
                _toastMessage.value = "Viral moments detected! 3 clips generated."
                _currentScreen.value = ScreenDestination.ClipList(newProjectId)
            } catch (e: Exception) {
                _processingStatusText.value = "Error: ${e.localizedMessage}"
                _toastMessage.value = "Processing failed: ${e.localizedMessage}"
            }
        }
    }

    // Export modal controls
    fun openExportModal(clip: ClipEntity) {
        _selectedClip.value = clip
        _showExportModal.value = true
        _exportProgress.value = 0
        _isExporting.value = false
        _exportDownloadUrl.value = null
    }

    fun closeExportModal() {
        _showExportModal.value = false
    }

    fun setExportQuality(quality: String) {
        _exportQuality.value = quality
    }

    fun setExportFormat(format: String) {
        _exportFormat.value = format
    }

    fun setExportFps(fps: Int) {
        _exportFps.value = fps
    }

    fun setExportBitrate(bitrate: String) {
        _exportBitrate.value = bitrate
    }

    fun setExportBurnCaptions(enabled: Boolean) {
        _exportBurnCaptions.value = enabled
    }

    fun setExportRemoveWatermark(remove: Boolean) {
        _exportRemoveWatermark.value = remove
    }

    fun setExportIncludeSrt(include: Boolean) {
        _exportIncludeSrt.value = include
    }

    fun startExport() {
        val clip = _selectedClip.value ?: return
        _isExporting.value = true
        _exportProgress.value = 10

        viewModelScope.launch {
            repository.createExportJob(
                clipId = clip.id,
                quality = _exportQuality.value,
                format = _exportFormat.value,
                fps = _exportFps.value,
                bitratePreset = _exportBitrate.value,
                burnedCaptions = _exportBurnCaptions.value,
                removeWatermark = _exportRemoveWatermark.value,
                includeSrt = _exportIncludeSrt.value
            ) { progress, _ ->
                _exportProgress.value = progress
            }
            _isExporting.value = false
            _exportDownloadUrl.value = "https://clipforge.app/downloads/clip_${clip.id}_${_exportQuality.value}.${_exportFormat.value.lowercase()}"
            _toastMessage.value = "Render complete! File ready to download."
        }
    }

    fun regenerateMetadata(clipId: Long) {
        viewModelScope.launch {
            _toastMessage.value = "Regenerating AI metadata..."
            delay(800)
            val current = _selectedMetadata.value ?: return@launch
            val updated = current.copy(
                titlesJson = "[\"The Unspoken Rule of Viral Retention\", \"Why Most Creators Fail in the First 3 Seconds\", \"The Hook Formula That Never Fails\"]",
                hookText = "If you don't do this in 3 seconds, viewers swipe.",
                cta = "Bookmark this for your next video shoot"
            )
            _selectedMetadata.value = updated
            repository.updateMetadata(updated)
            _toastMessage.value = "New titles & descriptions generated!"
        }
    }

    fun updateMetadataField(updated: PlatformMetadataEntity) {
        _selectedMetadata.value = updated
        viewModelScope.launch {
            repository.updateMetadata(updated)
        }
    }
}
