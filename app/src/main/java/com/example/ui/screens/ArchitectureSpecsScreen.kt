package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ClipForgeTopAppBar
import com.example.ui.components.SoundEffectManager
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary

@Composable
fun ArchitectureSpecsScreen(
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0=Folder & DB, 1=API & Celery, 2=LLM Prompts, 3=Docker & Deploy

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
                title = "ClipForge Full-Stack Specs",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                onOpenDocs = {}
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
            Text(
                text = "Full-Stack System Architecture",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Next.js 14 App Router • FastAPI Video Pipeline • Celery + Redis • FFmpeg + MediaPipe • PostgreSQL Prisma",
                fontSize = 12.sp,
                color = NeonCyan
            )

            // Tabs
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
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, DarkSurfaceHighlight, RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        SoundEffectManager.playTabSelect()
                        selectedTab = 0
                    },
                    text = { Text("DB & Tree", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_specs_db")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        SoundEffectManager.playTabSelect()
                        selectedTab = 1
                    },
                    text = { Text("API & Celery", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_specs_api")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = {
                        SoundEffectManager.playTabSelect()
                        selectedTab = 2
                    },
                    text = { Text("LLM Prompts", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_specs_prompts")
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = {
                        SoundEffectManager.playTabSelect()
                        selectedTab = 3
                    },
                    text = { Text("Docker & Deploy", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_specs_docker")
                )
            }

            when (selectedTab) {
                0 -> {
                    CodeBlockCard(
                        title = "FOLDER STRUCTURE (MONOREPO)",
                        code = """
clipforge/
  apps/
    web/                     # Next.js 14 App Router
      app/
        (auth)/login/
        dashboard/
        projects/[id]/   # Video editor & clips grid
        api/
      components/ui/       # shadcn/ui + Tailwind CSS
      lib/prisma.ts
    pipeline/                # FastAPI + Celery
      main.py              # REST API endpoints
      tasks/               # Celery async jobs
        download.py      # yt-dlp & chunked upload
        audio.py         # FFmpeg audio extract & ducking
        transcribe.py    # Whisper word timestamps
        analyze.py       # AI virality scoring
        reframe.py       # MediaPipe face tracking 9:16
        captions.py      # ASS subtitle burning
        render.py        # FFmpeg hardware encode
      models/schemas.py
  packages/database/           # Prisma schema & migrations
    schema.prisma
  docker-compose.yml           # Redis, Postgres, API, Celery
  .env.example
                        """.trimIndent(),
                        onCopy = { copyToClipboard(it, "Folder Structure") }
                    )

                    CodeBlockCard(
                        title = "POSTGRESQL DATABASE SCHEMA (PRISMA)",
                        code = """
datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
}

generator client {
  provider = "prisma-client-js"
}

model User {
  id            String         @id @default(cuid())
  email         String         @unique
  plan          String         @default("free") // free | pro
  videosUsed    Int            @default(0)
  projects      Project[]
  subscriptions Subscription[]
  createdAt     DateTime       @default(now())
}

model Project {
  id              String      @id @default(cuid())
  userId          String
  user            User        @relation(fields: [userId], references: [id], onDelete: Cascade)
  title           String
  sourceType      String      // "UPLOAD" | "YOUTUBE"
  sourceUrl       String
  durationSeconds Int
  status          String      // "PROCESSING" | "READY" | "FAILED"
  clips           Clip[]
  createdAt       DateTime    @default(now())
}

model Clip {
  id              String             @id @default(cuid())
  projectId       String
  project         Project            @relation(fields: [projectId], references: [id], onDelete: Cascade)
  title           String
  hookText        String
  viralityScore   Int                // 0 - 100
  reasoning       String
  startTimeMs     Int
  endTimeMs       Int
  emotionalPeak   String
  reframeMode     String             @default("SPEAKER_TRACK")
  captionPreset   String             @default("HORMOZI")
  metadata        PlatformMetadata?
  exports         Export[]
  createdAt       DateTime           @default(now())
}

model PlatformMetadata {
  id              String   @id @default(cuid())
  clipId          String   @unique
  clip            Clip     @relation(fields: [clipId], references: [id], onDelete: Cascade)
  titles          Json     // 3 hook-driven options
  tiktokDesc      String
  instagramDesc   String
  youtubeDesc     String
  broadTags       Json     // 3 tags
  nicheTags       Json     // 5 tags
  trendingTags    Json     // 2-3 tags
  hookOverlay     String
  cta             String
}

model Export {
  id              String   @id @default(cuid())
  clipId          String
  clip            Clip     @relation(fields: [clipId], references: [id], onDelete: Cascade)
  quality         String   // "480p" | "720p" | "1080p" | "4K"
  format          String   // "MP4" | "MOV"
  fps             Int      @default(30)
  status          String   // "RENDERING" | "COMPLETED" | "FAILED"
  progress        Int      @default(0)
  fileUrl         String?
  sizeBytes       BigInt?
  createdAt       DateTime @default(now())
}
                        """.trimIndent(),
                        onCopy = { copyToClipboard(it, "Prisma Schema") }
                    )
                }
                1 -> {
                    CodeBlockCard(
                        title = "FASTAPI ENDPOINTS & CELERY WORKERS",
                        code = """
# POST /api/ingest/upload
# Ingest MP4/MOV with resumable chunks -> dispatches celery 'pipeline.process_video'

# POST /api/ingest/youtube
# Validates URL -> dispatches 'pipeline.download_and_process'

# GET /api/projects/{project_id}/status
# Returns SSE stream of live steps (Whisper -> Virality -> Reframe -> Captions)

# GET /api/projects/{project_id}/clips
# Returns ranked viral clips with virality scores & timestamps

# POST /api/clips/{id}/export
# Body: { quality: "1080p", format: "MP4", fps: 60, bitrate: "Balanced", captions: true, watermark: false }
# Returns: { job_id: "exp_1092a" }

# GET /api/exports/{job_id}
# Returns: { status: "COMPLETED", progress: 100, download_url: "https://r2.clipforge.app/signed_url..." }

# CELERY PIPELINE TASK CHAIN:
# chain(
#   download_source.s(url),
#   extract_audio.s(),
#   transcribe_whisper.s(),       # word timestamps & diarization
#   detect_viral_moments.s(),     # AI virality scoring
#   generate_metadata.s(),        # Platform titles & tags
#   smart_reframe_mediapipe.s(),  # 16:9 to 9:16 speaker crop
#   burn_karaoke_captions.s()     # ASS subtitles + emoji pop
# )
                        """.trimIndent(),
                        onCopy = { copyToClipboard(it, "FastAPI Endpoints") }
                    )
                }
                2 -> {
                    CodeBlockCard(
                        title = "VIRAL MOMENT DETECTION PROMPT",
                        code = """
SYSTEM PROMPT:
You are an expert short-form video strategist who has generated over 1 Billion views across TikTok, Instagram Reels, and YouTube Shorts.
Analyze this video transcript with word-level timestamps to identify the 5-10 most viral moments (30-90 seconds each).

SCORING CRITERIA (0-100 Virality Score):
1. Hook in first 3 seconds: Strong curiosity gap, polarizing take, or high-urgency statement.
2. Emotional Peak: Surprise, awe, laughter, controversial revelation, or mind-blowing paradigm shift.
3. Story Arc with Payoff: Self-contained context where the conclusion satisfies the opening hook.
4. Quotable One-Liner: Easy to replay, quote, or comment on.
5. Practical Value: Immediately actionable framework or insight.

STRICT CONSTRAINTS:
- Snap start_time and end_time strictly to sentence boundaries (never cut mid-word).
- Do not invent quotes; use strictly the words spoken in the transcript.
- Return ONLY valid JSON matching this schema:
[
  {
    "start_time_ms": 124000,
    "end_time_ms": 176000,
    "title": "Why 99% of Software Engineers Will Become Orchestrators",
    "hook_text": "Stop writing boilerplate code today.",
    "virality_score": 98,
    "reasoning": "High-urgency opening hook, addresses existential career fear, counter-intuitive prediction with high comment debate.",
    "emotional_peak": "Mind-Blowing Career Paradigm Shift"
  }
]
                        """.trimIndent(),
                        onCopy = { copyToClipboard(it, "Viral Moment Prompt") }
                    )

                    CodeBlockCard(
                        title = "AI METADATA GENERATOR PROMPT (PER PLATFORM)",
                        code = """
SYSTEM PROMPT:
Generate platform-specific viral metadata for this short-form clip transcript. Output strictly valid JSON.

JSON SCHEMA:
{
  "titles": [
    "3 options, max 60 chars, hook-driven, curiosity or counter-intuitive"
  ],
  "description": {
    "tiktok": "max 150 chars + CTA + 3-5 high-relevance hashtags",
    "instagram": "max 300 chars, readable line breaks, CTA, 8-12 targeted hashtags",
    "youtube_shorts": "title max 100 chars + 2-3 line description with keywords + #Shorts"
  },
  "hashtags": {
    "broad": ["3 high-volume tags"],
    "niche": ["5 topic-specific tags"],
    "trending_style": ["2-3 format tags like #podcastclips"]
  },
  "hook_text": "text overlay for first 3 seconds",
  "cta": "e.g. Follow for part 2"
}

RULES:
- Use only facts from transcript; no false clickbait.
- Match video's native language.
- Hashtags: lowercase, no spaces, no banned/spammy tags.
                        """.trimIndent(),
                        onCopy = { copyToClipboard(it, "Metadata Prompt") }
                    )
                }
                3 -> {
                    CodeBlockCard(
                        title = "DOCKER COMPOSE LOCAL SETUP",
                        code = """
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_USER: clipforge
      POSTGRES_PASSWORD: clipforge_secret
      POSTGRES_DB: clipforge_db
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  api:
    build: ./apps/pipeline
    command: uvicorn main:app --host 0.0.0.0 --port 8000 --reload
    ports:
      - "8000:8000"
    environment:
      - DATABASE_URL=postgresql://clipforge:clipforge_secret@postgres:5432/clipforge_db
      - REDIS_URL=redis://redis:6379/0
    depends_on:
      - postgres
      - redis

  celery_worker:
    build: ./apps/pipeline
    command: celery -A tasks.celery_app worker --loglevel=info --concurrency=4
    environment:
      - DATABASE_URL=postgresql://clipforge:clipforge_secret@postgres:5432/clipforge_db
      - REDIS_URL=redis://redis:6379/0
    depends_on:
      - redis
      - postgres

volumes:
  pgdata:
                        """.trimIndent(),
                        onCopy = { copyToClipboard(it, "Docker Compose") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CodeBlockCard(
    title: String,
    code: String,
    onCopy: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace
                )
                Surface(
                    onClick = { onCopy(code) },
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurfaceHighlight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Text("Copy", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Text(
                text = code,
                fontSize = 11.sp,
                color = Color(0xFFE2E8F0),
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            )
        }
    }
}
