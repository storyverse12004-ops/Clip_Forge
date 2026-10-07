package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SourceType
import com.example.ui.components.AdMobBanner
import com.example.ui.components.AdMobConfig
import com.example.ui.components.ClipForgeTopAppBar
import com.example.ui.components.SoundEffectManager
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ViralEmerald
import java.util.Locale

@Composable
fun UploadIngestionScreen(
    onOpenDocs: () -> Unit,
    onNavigateBack: () -> Unit,
    onStartPipeline: (title: String, sourceType: SourceType, sourceUrl: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = YouTube, 1 = File Upload
    var youtubeUrl by remember { mutableStateOf("https://www.youtube.com/watch?v=lex_huberman_ai_future") }
    var videoTitle by remember { mutableStateOf("Why 99% of Software Engineers Will Become Orchestrators") }

    // Upload state with real Android Photo/Video Picker
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>("podcast_episode_42_master.mp4") }
    var videoFileSizeLabel by remember { mutableStateOf<String?>("842 MB") }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedVideoUri = uri
            val name = getFileNameFromUri(context, uri)
            selectedFileName = name
            val size = getFileSizeFromUri(context, uri)
            videoFileSizeLabel = size ?: "Device Video"

            val inferredTitle = name.substringBeforeLast(".")
                .replace("_", " ")
                .replace("-", " ")
                .trim()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            if (inferredTitle.isNotBlank()) {
                videoTitle = inferredTitle
            }
        }
    }

    // Pipeline options
    var removeSilence by remember { mutableStateOf(true) }
    var removeFillerWords by remember { mutableStateOf(true) }
    var speakerDiarization by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            ClipForgeTopAppBar(
                title = "New Clip Project",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onOpenDocs = onOpenDocs
            )
        },
        containerColor = DarkBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Source selection tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurfaceElevated,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ElectricViolet
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        SoundEffectManager.playTabSelect()
                        selectedTab = 0
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("YouTube Link", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_youtube_link")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        SoundEffectManager.playTabSelect()
                        selectedTab = 1
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Upload File", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_file_upload")
                )
            }

            // Ingestion inputs
            if (selectedTab == 0) {
                // YouTube link input
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "PASTE YOUTUBE OR PODCAST URL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        OutlinedTextField(
                            value = youtubeUrl,
                            onValueChange = { youtubeUrl = it },
                            placeholder = { Text("https://youtube.com/watch?v=...", color = TextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = DarkSurfaceHighlight,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_youtube_url")
                        )

                        Text(
                            text = "1-Tap Popular Presets to Test:",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PresetLinkChip(
                                title = "Lex & AI Agents",
                                onClick = {
                                    youtubeUrl = "https://youtube.com/watch?v=lex_ai_agents_clipforge"
                                    videoTitle = "Why 99% of Software Engineers Will Become Orchestrators"
                                }
                            )
                            PresetLinkChip(
                                title = "Huberman Focus",
                                onClick = {
                                    youtubeUrl = "https://youtube.com/watch?v=huberman_dopamine_focus"
                                    videoTitle = "The Science of Dopamine & Relentless Focus"
                                }
                            )
                            PresetLinkChip(
                                title = "Naval Leverage",
                                onClick = {
                                    youtubeUrl = "https://youtube.com/watch?v=naval_code_capital"
                                    videoTitle = "How to Build Unfair Leverage with Code & Media"
                                }
                            )
                        }
                    }
                }
            } else {
                // File upload dropzone with real System Video Picker
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.border(
                        1.dp,
                        if (selectedVideoUri != null) ViralEmerald else DarkSurfaceHighlight,
                        RoundedCornerShape(14.dp)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (selectedVideoUri != null) ViralEmerald.copy(alpha = 0.2f)
                                    else ElectricViolet.copy(alpha = 0.2f)
                                )
                                .clickable {
                                    SoundEffectManager.playButtonTap()
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedVideoUri != null) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                                contentDescription = "Upload",
                                tint = if (selectedVideoUri != null) ViralEmerald else ElectricViolet,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = selectedFileName ?: "Select MP4 or MOV file",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (selectedVideoUri != null) "Ready for AI viral detection • ${videoFileSizeLabel ?: "Device video"}"
                                   else "Pick any MP4/MOV from device storage or use a sample video",
                            fontSize = 11.sp,
                            color = if (selectedVideoUri != null) NeonCyan else TextMuted
                        )

                        // Action Buttons: System Video Picker + Presets
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Surface(
                                onClick = {
                                    SoundEffectManager.playButtonTap()
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = ElectricViolet,
                                modifier = Modifier.testTag("choose_video_file_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (selectedVideoUri != null) "Change Video File" else "Browse Device Videos",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                onClick = {
                                    SoundEffectManager.playButtonTap()
                                    selectedVideoUri = null
                                    selectedFileName = "founder_masterclass_raw_2026.mp4"
                                    videoFileSizeLabel = "842 MB"
                                    videoTitle = "The Solopreneur Playbook: Building Without Permission"
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = DarkSurfaceHighlight,
                                modifier = Modifier.border(0.5.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Sample Podcast",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Project Title Input
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PROJECT TITLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = videoTitle,
                        onValueChange = { videoTitle = it },
                        placeholder = { Text("e.g. Masterclass with Guest", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricViolet,
                            unfocusedBorderColor = DarkSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("input_project_title")
                    )
                }
            }

            // Advanced AI Pipeline Options
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Options",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "AI PIPELINE CONFIGURATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }

                    // Silence removal toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Auto-Cut Dead Silences (>0.4s)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(text = "Increases retention by removing awkward pauses", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = removeSilence,
                            onCheckedChange = {
                                SoundEffectManager.playButtonTap()
                                removeSilence = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricViolet)
                        )
                    }

                    // Filler words
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Clean Filler Words (\"um\", \"uh\", \"like\")", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(text = "Smooth speech rhythm using Whisper alignment", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = removeFillerWords,
                            onCheckedChange = {
                                SoundEffectManager.playButtonTap()
                                removeFillerWords = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricViolet)
                        )
                    }

                    // Speaker diarization
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Multi-Speaker Diarization (Pyannote)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(text = "Distinguishes host and guest for split-screen reframing", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = speakerDiarization,
                            onCheckedChange = {
                                SoundEffectManager.playButtonTap()
                                speakerDiarization = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricViolet)
                        )
                    }
                }
            }

            // Launch CTA Button
            Button(
                onClick = {
                    SoundEffectManager.playButtonTap()
                    val finalSourceType = if (selectedTab == 0) SourceType.YOUTUBE else SourceType.UPLOAD
                    val finalUrl = if (selectedTab == 0) youtubeUrl else (selectedFileName ?: "uploaded_video.mp4")
                    onStartPipeline(videoTitle, finalSourceType, finalUrl)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_viral_pipeline_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Forge Viral Moments (AI Pipeline)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AdMobBanner(
                adUnitId = AdMobConfig.BANNER_AD_UNIT_ID,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun PresetLinkChip(
    title: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = {
            SoundEffectManager.playButtonTap()
            onClick()
        },
        shape = RoundedCornerShape(8.dp),
        color = DarkSurfaceHighlight.copy(alpha = 0.6f),
        modifier = Modifier.border(0.5.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            color = NeonCyan,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun getFileNameFromUri(context: Context, uri: Uri): String {
    var fileName: String? = null
    if (uri.scheme == "content") {
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
        } catch (_: Exception) {}
    }
    if (fileName == null) {
        fileName = uri.lastPathSegment
    }
    return fileName ?: "device_video_${System.currentTimeMillis()}.mp4"
}

private fun getFileSizeFromUri(context: Context, uri: Uri): String? {
    if (uri.scheme == "content") {
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        val bytes = cursor.getLong(sizeIndex)
                        if (bytes > 0) {
                            val mb = bytes / (1024.0 * 1024.0)
                            return String.format(Locale.US, "%.1f MB", mb)
                        }
                    }
                }
            }
        } catch (_: Exception) {}
    }
    return null
}
