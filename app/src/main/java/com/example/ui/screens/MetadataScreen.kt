package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PlatformMetadataEntity
import com.example.ui.components.ClipForgeTopAppBar
import com.example.ui.components.SoundEffectManager
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ViralAmber

private data class PlatformTabItem(
    val title: String,
    val iconResId: Int,
    val colorHex: Long,
    val testTag: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MetadataScreen(
    metadata: PlatformMetadataEntity?,
    onOpenDocs: () -> Unit,
    onNavigateBack: () -> Unit,
    onRegenerate: () -> Unit,
    onSaveField: (PlatformMetadataEntity) -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (metadata == null) return

    val context = LocalContext.current
    var selectedPlatformTab by remember { mutableIntStateOf(0) } // 0=YouTube Shorts, 1=TikTok, 2=Instagram Reels, 3=Facebook Reels

    var currentYtDesc by remember(metadata.id) { mutableStateOf(metadata.youtubeShortsDescription) }
    var currentTiktokDesc by remember(metadata.id) { mutableStateOf(metadata.tiktokDescription) }
    var currentIgDesc by remember(metadata.id) { mutableStateOf(metadata.instagramDescription) }
    var currentFbDesc by remember(metadata.id) {
        mutableStateOf(
            if (metadata.facebookDescription.isNotBlank()) metadata.facebookDescription
            else "The software development industry is shifting faster than ever. If you are building with AI agents in 2026, here is why system orchestration beats writing boilerplate. Let us know your thoughts in the comments!"
        )
    }
    var currentHook by remember(metadata.id, metadata.hookText) { mutableStateOf(metadata.hookText) }

    fun copyToClipboard(text: String, label: String) {
        SoundEffectManager.playButtonTap()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        onShowToast("Copied $label to clipboard!")
    }

    Scaffold(
        topBar = {
            ClipForgeTopAppBar(
                title = "AI Viral Metadata",
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
            // Header with Regenerate CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Multi-Platform AI Metadata",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Separate optimization for Shorts, TikTok, Reels & Facebook",
                        fontSize = 12.sp,
                        color = NeonCyan
                    )
                }
                Button(
                    onClick = {
                        SoundEffectManager.playButtonTap()
                        onRegenerate()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("metadata_btn_regenerate")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Regenerate",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Regenerate", fontSize = 12.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            }

            // Hook Text Overlay (First 3 seconds)
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HOOK TEXT OVERLAY (FIRST 3 SECONDS)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ViralAmber
                        )
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Hook",
                            tint = TextMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { copyToClipboard(currentHook, "Hook Text") }
                        )
                    }
                    OutlinedTextField(
                        value = currentHook,
                        onValueChange = {
                            currentHook = it
                            onSaveField(metadata.copy(hookText = it))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ViralAmber,
                            unfocusedBorderColor = DarkSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("input_hook_overlay")
                    )
                }
            }

            // 3 AI Hook Titles Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "3 AI HOOK TITLES (MAX 60 CHARS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    val titles = listOf(
                        "Why 99% of Software Engineers Will Become Orchestrators",
                        "The Brutal Truth About Writing Code In 2026",
                        "Coding Is Dead: Here's What 100x Engineers Do Instead"
                    )

                    titles.forEachIndexed { index, titleText ->
                        Surface(
                            onClick = { copyToClipboard(titleText, "Title Option ${index + 1}") },
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${index + 1}. $titleText",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${titleText.length} chars • High-curiosity hook",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Title",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4 Separate Platform Tabs with Official Logos
            val platformTabs = listOf(
                PlatformTabItem("YouTube", R.drawable.ic_platform_youtube, 0xFFFF0000, "tab_meta_youtube"),
                PlatformTabItem("TikTok", R.drawable.ic_platform_tiktok, 0xFF00F2FE, "tab_meta_tiktok"),
                PlatformTabItem("Instagram", R.drawable.ic_platform_instagram, 0xFFE1306C, "tab_meta_instagram"),
                PlatformTabItem("Facebook", R.drawable.ic_platform_facebook, 0xFF1877F2, "tab_meta_facebook")
            )

            TabRow(
                selectedTabIndex = selectedPlatformTab,
                containerColor = DarkSurfaceElevated,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    val color = Color(platformTabs[selectedPlatformTab].colorHex)
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedPlatformTab]),
                        color = color
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(10.dp))
            ) {
                platformTabs.forEachIndexed { index, item ->
                    Tab(
                        selected = selectedPlatformTab == index,
                        onClick = {
                            SoundEffectManager.playTabSelect()
                            selectedPlatformTab = index
                        },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = item.iconResId),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = item.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedPlatformTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }

            // Platform Description & 1-Click Copy
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val (currentText, label, brandColor) = when (selectedPlatformTab) {
                        0 -> Triple(currentYtDesc, "YouTube Shorts (Keywords + #Shorts tag • 9:16)", Color(0xFFFF0000))
                        1 -> Triple(currentTiktokDesc, "TikTok (Max 150 chars + CTA + 3-5 tags • 9:16)", Color(0xFF00F2FE))
                        2 -> Triple(currentIgDesc, "Instagram Reels (Line breaks + 8-12 tags • 9:16 / 4:5)", Color(0xFFE1306C))
                        else -> Triple(currentFbDesc, "Facebook Reels (Page discussion + 1:1 cross-post • 9:16)", Color(0xFF1877F2))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = brandColor
                        )
                        Button(
                            onClick = { copyToClipboard(currentText, label) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("btn_copy_platform_desc")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = currentText,
                        onValueChange = { newVal ->
                            when (selectedPlatformTab) {
                                0 -> {
                                    currentYtDesc = newVal
                                    onSaveField(metadata.copy(youtubeShortsDescription = newVal))
                                }
                                1 -> {
                                    currentTiktokDesc = newVal
                                    onSaveField(metadata.copy(tiktokDescription = newVal))
                                }
                                2 -> {
                                    currentIgDesc = newVal
                                    onSaveField(metadata.copy(instagramDescription = newVal))
                                }
                                else -> {
                                    currentFbDesc = newVal
                                    onSaveField(metadata.copy(facebookDescription = newVal))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = brandColor,
                            unfocusedBorderColor = DarkSurfaceHighlight,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("input_platform_desc")
                    )
                }
            }

            // Platform Hashtag Strategy
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "HASHTAG STRATEGY (BROAD • NICHE • TRENDING)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val tags = when (selectedPlatformTab) {
                            0 -> listOf("#Shorts", "#Tech", "#Coding", "#SoftwareEngineer", "#AI", "#PodcastClips", "#Innovation")
                            1 -> listOf("#techtok", "#softwareengineer", "#fyp", "#coding", "#solopreneur", "#developerlife", "#aiagents")
                            2 -> listOf("#reelsinstagram", "#entrepreneurmindset", "#techfounder", "#futureofwork", "#productivity", "#startup")
                            else -> listOf("#FacebookReels", "#Technology", "#FutureOfWork", "#Programming", "#ProductivityTips", "#BusinessStrategy")
                        }

                        tags.forEach { tag ->
                            Surface(
                                onClick = { copyToClipboard(tag, "Hashtag $tag") },
                                shape = RoundedCornerShape(6.dp),
                                color = DarkSurface,
                                modifier = Modifier.border(0.5.dp, DarkSurfaceHighlight, RoundedCornerShape(6.dp))
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
