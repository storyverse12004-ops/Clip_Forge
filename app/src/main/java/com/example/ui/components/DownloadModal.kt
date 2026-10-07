package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.ClipEntity
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

@Composable
fun DownloadModal(
    clip: ClipEntity,
    quality: String,
    format: String,
    fps: Int,
    bitrate: String,
    burnCaptions: Boolean,
    removeWatermark: Boolean,
    includeSrt: Boolean,
    isExporting: Boolean,
    exportProgress: Int,
    downloadUrl: String?,
    onQualitySelected: (String) -> Unit,
    onFormatSelected: (String) -> Unit,
    onFpsSelected: (Int) -> Unit,
    onBitrateSelected: (String) -> Unit,
    onToggleBurnCaptions: (Boolean) -> Unit,
    onToggleRemoveWatermark: (Boolean) -> Unit,
    onToggleIncludeSrt: (Boolean) -> Unit,
    onStartExport: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { if (!isExporting) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(20.dp))
                .testTag("download_modal_dialog"),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Export Short Clip",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Rendered with GPU-accelerated FFmpeg",
                            fontSize = 11.sp,
                            color = NeonCyan
                        )
                    }
                    IconButton(
                        onClick = {
                            SoundEffectManager.playButtonTap()
                            onDismiss()
                        },
                        enabled = !isExporting,
                        modifier = Modifier.size(32.dp).testTag("close_download_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close modal",
                            tint = TextMuted
                        )
                    }
                }

                // Platform preset buttons
                Text(
                    text = "PLATFORM SHORTS PRESETS (9:16 OPTIMIZED)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PlatformPresetChip(
                        name = "YouTube",
                        ratio = "9:16",
                        iconRes = R.drawable.ic_platform_youtube,
                        active = quality == "4K" || (quality == "1080p" && fps == 60),
                        onClick = {
                            onQualitySelected("4K")
                            onFpsSelected(60)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PlatformPresetChip(
                        name = "TikTok",
                        ratio = "9:16",
                        iconRes = R.drawable.ic_platform_tiktok,
                        active = quality == "1080p" && fps == 60,
                        onClick = {
                            onQualitySelected("1080p")
                            onFpsSelected(60)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PlatformPresetChip(
                        name = "Instagram",
                        ratio = "9:16",
                        iconRes = R.drawable.ic_platform_instagram,
                        active = quality == "1080p" && fps == 30,
                        onClick = {
                            onQualitySelected("1080p")
                            onFpsSelected(30)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PlatformPresetChip(
                        name = "Facebook",
                        ratio = "9:16",
                        iconRes = R.drawable.ic_platform_facebook,
                        active = quality == "1080p" && format == "MP4",
                        onClick = {
                            onQualitySelected("1080p")
                            onFpsSelected(30)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Resolution / Quality Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RESOLUTION & QUALITY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = "Up to 1080p Full HD Free",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }

                    // Free Tier Highlight Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonCyan.copy(alpha = 0.12f))
                            .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Full HD 1080p, 720p & 480p are 100% Free with zero watermark!",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeonCyan
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QualityOptionCard(
                            label = "480p",
                            resolution = "480x854",
                            fileSize = "~14 MB",
                            selected = quality == "480p",
                            badge = "FREE",
                            isLocked = false,
                            modifier = Modifier.weight(1f),
                            onClick = { onQualitySelected("480p") }
                        )
                        QualityOptionCard(
                            label = "720p",
                            resolution = "720x1280",
                            fileSize = "~28 MB",
                            selected = quality == "720p",
                            badge = "FREE HD",
                            isLocked = false,
                            modifier = Modifier.weight(1f),
                            onClick = { onQualitySelected("720p") }
                        )
                        QualityOptionCard(
                            label = "1080p",
                            resolution = "1080x1920",
                            fileSize = "~54 MB",
                            selected = quality == "1080p",
                            badge = "FREE FHD",
                            isLocked = false,
                            modifier = Modifier.weight(1f),
                            onClick = { onQualitySelected("1080p") }
                        )
                        QualityOptionCard(
                            label = "4K",
                            resolution = "2160x3840",
                            fileSize = "~180 MB",
                            selected = quality == "4K",
                            badge = "ULTRA",
                            isLocked = false,
                            modifier = Modifier.weight(1f),
                            onClick = { onQualitySelected("4K") }
                        )
                    }
                }

                // Format & FPS Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "FORMAT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            SelectableChip(label = "MP4 (H.264)", selected = format == "MP4", onClick = { onFormatSelected("MP4") })
                            SelectableChip(label = "MOV", selected = format == "MOV", onClick = { onFormatSelected("MOV") })
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "FRAMERATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            SelectableChip(label = "30 FPS", selected = fps == 30, onClick = { onFpsSelected(30) })
                            SelectableChip(label = "60 FPS", selected = fps == 60, locked = false, onClick = { onFpsSelected(60) })
                        }
                    }
                }

                // Bitrate profile
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "BITRATE PROFILE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SelectableChip(label = "Compressed (8 Mbps)", selected = bitrate == "Compressed", onClick = { onBitrateSelected("Compressed") })
                        SelectableChip(label = "Balanced (16 Mbps)", selected = bitrate == "Balanced", onClick = { onBitrateSelected("Balanced") })
                        SelectableChip(label = "High Quality (30 Mbps)", selected = bitrate == "High", onClick = { onBitrateSelected("High") })
                    }
                }

                // Toggles: Watermark, Burned Captions, SRT
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Burn-in Animated Captions", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(text = "Renders chosen style into video pixels", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = burnCaptions,
                            onCheckedChange = {
                                SoundEffectManager.playButtonTap()
                                onToggleBurnCaptions(it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricViolet)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Remove ClipForge Watermark", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(text = "Clean export without badge", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = removeWatermark,
                            onCheckedChange = {
                                SoundEffectManager.playButtonTap()
                                onToggleRemoveWatermark(it)
                            },
                            enabled = true,
                            colors = SwitchDefaults.colors(checkedThumbColor = ViralAmber, checkedTrackColor = Color(0xFF78350F))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Include Separate SRT Subtitles", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(text = "Word-level timestamped .srt file", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = includeSrt,
                            onCheckedChange = {
                                SoundEffectManager.playButtonTap()
                                onToggleIncludeSrt(it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = ElectricViolet)
                        )
                    }
                }

                // Export Progress & Render Status
                if (isExporting || downloadUrl != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (downloadUrl != null) "✓ Render Complete" else "Rendering $quality $format ($exportProgress%)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (downloadUrl != null) ViralEmerald else NeonCyan
                            )
                            Text(
                                text = if (downloadUrl != null) "Ready for download" else "FFmpeg Task...",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        LinearProgressIndicator(
                            progress = { exportProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (downloadUrl != null) ViralEmerald else NeonCyan,
                            trackColor = DarkSurfaceHighlight
                        )
                    }
                }

                // Action Buttons
                if (downloadUrl != null) {
                    Button(
                        onClick = {
                            SoundEffectManager.playButtonTap()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("download_completed_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ViralEmerald)
                    ) {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = "Download")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download Video ($quality)", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            SoundEffectManager.playButtonTap()
                            onStartExport()
                        },
                        enabled = !isExporting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("start_export_render_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Export")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isExporting) "Rendering with FFmpeg..." else "Render & Export ($quality)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlatformPresetChip(
    name: String,
    ratio: String,
    iconRes: Int,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = {
            SoundEffectManager.playButtonTap()
            onClick()
        },
        shape = RoundedCornerShape(10.dp),
        color = if (active) ElectricViolet.copy(alpha = 0.25f) else DarkSurfaceElevated,
        modifier = modifier.border(
            1.dp,
            if (active) ElectricViolet else DarkSurfaceHighlight,
            RoundedCornerShape(10.dp)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = name,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = name,
                fontSize = 10.sp,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                color = if (active) Color.White else TextPrimary,
                maxLines = 1
            )
            Text(
                text = ratio,
                fontSize = 9.sp,
                color = if (active) NeonCyan else TextMuted
            )
        }
    }
}

@Composable
private fun QualityOptionCard(
    label: String,
    resolution: String,
    fileSize: String,
    selected: Boolean,
    isLocked: Boolean,
    badge: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) ElectricViolet.copy(alpha = 0.25f) else DarkSurfaceElevated)
            .border(
                1.dp,
                if (selected) ElectricViolet else DarkSurfaceHighlight,
                RoundedCornerShape(10.dp)
            )
            .clickable {
                SoundEffectManager.playButtonTap()
                onClick()
            }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (badge.startsWith("FREE")) NeonCyan.copy(alpha = 0.2f)
                            else ElectricViolet.copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (badge.startsWith("FREE")) NeonCyan else Color.White
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) Color.White else TextPrimary
                )
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = ViralAmber,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
            Text(text = resolution, fontSize = 8.sp, color = TextMuted)
            Text(text = fileSize, fontSize = 9.sp, color = if (selected) NeonCyan else TextSecondary)
        }
    }
}

@Composable
private fun SelectableChip(
    label: String,
    selected: Boolean,
    locked: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = {
            SoundEffectManager.playButtonTap()
            onClick()
        },
        shape = RoundedCornerShape(8.dp),
        color = if (selected) NeonCyan.copy(alpha = 0.2f) else DarkSurfaceElevated,
        modifier = Modifier.border(
            1.dp,
            if (selected) NeonCyan else DarkSurfaceHighlight,
            RoundedCornerShape(8.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.White else TextSecondary
            )
            if (locked) {
                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = ViralAmber, modifier = Modifier.size(10.dp))
            }
        }
    }
}
