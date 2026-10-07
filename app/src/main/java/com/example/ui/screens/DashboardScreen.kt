package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.ProjectEntity
import com.example.data.model.SourceType
import com.example.ui.components.AdMobBanner
import com.example.ui.components.AdMobConfig
import com.example.ui.components.AdMobManager
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
import com.example.ui.theme.ViralAmber
import com.example.ui.theme.ViralEmerald

@Composable
fun DashboardScreen(
    projects: List<ProjectEntity>,
    onOpenDocs: () -> Unit,
    onSelectProject: (Long) -> Unit,
    onStartNewIngestion: () -> Unit,
    onShowInterstitial: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ClipForgeTopAppBar(
                title = "ClipForge",
                canNavigateBack = false,
                onOpenDocs = onOpenDocs
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    SoundEffectManager.playButtonTap()
                    onStartNewIngestion()
                },
                containerColor = ElectricViolet,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("dashboard_fab_new_clip")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Clip Project"
                )
            }
        },
        containerColor = DarkBg,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                HeroStudioBanner(onStartNewIngestion = onStartNewIngestion)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Projects & Repurposed Videos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${projects.size} Videos",
                        fontSize = 12.sp,
                        color = NeonCyan
                    )
                }
            }

            if (projects.isEmpty()) {
                item {
                    EmptyProjectsState(onStartNewIngestion = onStartNewIngestion)
                }
            } else {
                items(projects, key = { it.id }) { project ->
                    ProjectCard(
                        project = project,
                        onClick = { onSelectProject(project.id) }
                    )
                }
            }

            // AdMob Live Banner section
            item {
                AdMobBanner(
                    adUnitId = AdMobConfig.BANNER_AD_UNIT_ID,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun AdMobLiveAdsCard(
    onShowInterstitial: () -> Unit
) {
    val context = LocalContext.current
    val telemetry by AdMobManager.telemetry.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ElectricViolet.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "ADMOB",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Google Mobile Ads Diagnostics",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = telemetry.sdkStatusMessage,
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (telemetry.sdkInitialized) ViralEmerald.copy(alpha = 0.2f) else ViralAmber.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (telemetry.sdkInitialized) "SDK Ready" else "Initializing",
                        fontSize = 10.sp,
                        color = if (telemetry.sdkInitialized) ViralEmerald else ViralAmber,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Live Ad Unit IDs & Real-Time Status
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A0F1D))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "App ID", fontSize = 10.sp, color = TextMuted)
                    Text(text = AdMobConfig.APP_ID, fontSize = 10.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Banner Ad Unit", fontSize = 10.sp, color = TextMuted)
                    Text(text = AdMobConfig.BANNER_AD_UNIT_ID, fontSize = 10.sp, color = NeonCyan, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Banner Status", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = "${telemetry.bannerState} • ${telemetry.bannerDetail}",
                        fontSize = 10.sp,
                        color = if (telemetry.bannerState == "LOADED" || telemetry.bannerState == "FALLBACK_TEST") ViralEmerald else NeonCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Interstitial Ad Unit", fontSize = 10.sp, color = TextMuted)
                    Text(text = AdMobConfig.INTERSTITIAL_AD_UNIT_ID, fontSize = 10.sp, color = ViralAmber, fontFamily = FontFamily.Monospace)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Interstitial Status", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = "${telemetry.interstitialState} • ${telemetry.interstitialDetail}",
                        fontSize = 10.sp,
                        color = if (telemetry.interstitialState == "READY" || telemetry.interstitialState == "FALLBACK_TEST") ViralEmerald else ViralAmber,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        SoundEffectManager.playButtonTap()
                        onShowInterstitial()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_trigger_interstitial_ad")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Show Interstitial Ad",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (AdMobManager.isAdLoaded()) "Show Interstitial Ad (Ready)" else "Test Interstitial Ad",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        SoundEffectManager.playButtonTap()
                        AdMobManager.loadInterstitial(
                            context = context,
                            adUnitId = AdMobConfig.TEST_INTERSTITIAL_AD_UNIT_ID,
                            forceReload = true
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_reload_interstitial_ad")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload Interstitial",
                        tint = NeonCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reload", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun HeroStudioBanner(
    onStartNewIngestion: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF3B0764),
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            )
            .border(1.dp, ElectricViolet.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ViralAmber,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "AI VIRAL MOMENT ENGINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ViralAmber
                )
            }

            Text(
                text = "Turn 1 Long Video Into 10 Viral Shorts in 60s",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 24.sp
            )

            Text(
                text = "Whisper word timestamps • MediaPipe 9:16 speaker reframing • Hormozi karaoke captions • Platform metadata.",
                fontSize = 12.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 16.sp
            )

            // 4 Supported Platforms
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Ratios for:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                listOf(
                    Triple("Shorts", R.drawable.ic_platform_youtube, 0xFFFF0000),
                    Triple("TikTok", R.drawable.ic_platform_tiktok, 0xFF00F2FE),
                    Triple("Reels", R.drawable.ic_platform_instagram, 0xFFE1306C),
                    Triple("Facebook", R.drawable.ic_platform_facebook, 0xFF1877F2)
                ).forEach { (name, iconRes, colorHex) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(colorHex).copy(alpha = 0.18f),
                        modifier = Modifier.border(0.5.dp, Color(colorHex).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Image(painter = painterResource(id = iconRes), contentDescription = name, modifier = Modifier.size(11.dp))
                            Text(text = name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        SoundEffectManager.playButtonTap()
                        onStartNewIngestion()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("hero_forge_clips_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Forge Clips", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceHighlight.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f).clickable {
                        SoundEffectManager.playButtonTap()
                        onStartNewIngestion()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "YouTube Link",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Paste Link",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                SoundEffectManager.playButtonTap()
                onClick()
            }
            .testTag("project_card_${project.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with duration overlay
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.thumb_tech_podcast),
                    contentDescription = project.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${project.durationSeconds / 60}m",
                        fontSize = 9.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Info column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = project.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Viral Clips Count",
                            tint = ViralAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${project.clipCount} Viral Clips",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ViralAmber
                        )
                    }
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = if (project.sourceType == SourceType.YOUTUBE) "YouTube" else "Uploaded MP4",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF0F172A)
                    ) {
                        Text(
                            text = "98 Top Score",
                            fontSize = 10.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "Tap to review & export →",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyProjectsState(onStartNewIngestion: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Movie,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "No projects yet",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Upload a podcast, interview, or lecture to generate viral clips.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Button(
            onClick = {
                SoundEffectManager.playButtonTap()
                onStartNewIngestion()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
        ) {
            Text("Create First Clip")
        }
    }
}
