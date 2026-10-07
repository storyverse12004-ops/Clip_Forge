package com.example.data.repository

import com.example.data.local.ClipDao
import com.example.data.local.ExportDao
import com.example.data.local.PlatformMetadataDao
import com.example.data.local.ProjectDao
import com.example.data.model.CaptionPreset
import com.example.data.model.ClipEntity
import com.example.data.model.ExportEntity
import com.example.data.model.PlatformMetadataEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectStatus
import com.example.data.model.ReframeMode
import com.example.data.model.SourceType
import com.example.data.model.WordTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClipForgeRepository(
    private val projectDao: ProjectDao,
    private val clipDao: ClipDao,
    private val metadataDao: PlatformMetadataDao,
    private val exportDao: ExportDao
) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val allExports: Flow<List<ExportEntity>> = exportDao.getAllExports()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDefaultDataIfEmpty()
        }
    }

    private suspend fun seedDefaultDataIfEmpty() {
        val existing = projectDao.getAllProjects().first()
        if (existing.isNotEmpty()) return

        // Seed Project 1: AI & Tech Podcast
        val projectId1 = projectDao.insertProject(
            ProjectEntity(
                title = "The Future of AGI & Autonomous Agents (Lex Interview)",
                sourceType = SourceType.YOUTUBE,
                sourceUrl = "https://youtube.com/watch?v=ai_future_clipforge",
                durationSeconds = 4820,
                videoSizeMb = 1420.5f,
                status = ProjectStatus.READY,
                clipCount = 3,
                thumbnailResName = "thumb_tech_podcast"
            )
        )

        val clip1Id = clipDao.insertClip(
            ClipEntity(
                projectId = projectId1,
                title = "Why 99% of Software Engineers Will Become Orchestrators",
                hookText = "Stop writing boilerplate code today.",
                viralityScore = 98,
                reasoning = "High-urgency hook in first 2s, addresses broad existential anxiety about career shifts, counter-intuitive tech prediction.",
                startTimeMs = 124000,
                endTimeMs = 176000,
                emotionalPeak = "Mind-Blowing Career Insight",
                reframeMode = ReframeMode.SPEAKER_TRACK,
                captionPreset = CaptionPreset.HORMOZI,
                captionFontSize = 24,
                captionPosition = "BOTTOM_THIRD",
                backgroundMusic = "Upbeat Tech Ambient",
                duckingPercent = 85,
                thumbnailResName = "thumb_viral_clip"
            )
        )

        metadataDao.insertMetadata(
            PlatformMetadataEntity(
                clipId = clip1Id,
                titlesJson = "[\"Coding Is Dead: The New 100x Engineer\", \"Why Writing Code Is The Wrong Skill In 2026\", \"The Brutal Truth About Software Engineering\"]",
                tiktokDescription = "Coding is dead. Here's what 100x engineers are actually doing instead.\nFollow for more tech truth!\n#softwareengineer #aiagents #coding #techjobs #techlife",
                instagramDescription = "If you're still spending 40 hours a week typing syntax, you're competing against automated intelligence that never sleeps.\n\nThe real leverage now? Architecture and prompt systems.\n\nSave this for your next career move!\n\n#techfounder #softwaredevelopment #artificialintelligence #codinglife #stemcareers #futureofwork #startuptips",
                youtubeShortsDescription = "Why Software Engineers Must Stop Writing Code Now | Lex Podcast Insight #Shorts #Tech #AI #Coding",
                facebookDescription = "The software engineering landscape is transforming faster than ever. If you are building with AI agents in 2026, here is why system orchestration beats typing syntax every time. Share your experience in the comments!",
                broadHashtagsJson = "[\"#tech\", \"#career\", \"#business\"]",
                nicheHashtagsJson = "[\"#softwareengineer\", \"#promptengineering\", \"#aiagents\", \"#developerlife\", \"#codingbootcamp\"]",
                trendingHashtagsJson = "[\"#podcastclips\", \"#viraltips\", \"#technews\"]",
                hookText = "Stop writing boilerplate code today.",
                cta = "Save this & follow for part 2"
            )
        )

        val clip2Id = clipDao.insertClip(
            ClipEntity(
                projectId = projectId1,
                title = "The Single Secret Behind OpenAI's Breakthrough",
                hookText = "Nobody talks about this one architectural shift.",
                viralityScore = 94,
                reasoning = "Insider revelation, creates intense curiosity gap, clear concise explanation with high replayability.",
                startTimeMs = 380000,
                endTimeMs = 432000,
                emotionalPeak = "Behind-The-Scenes Secret",
                reframeMode = ReframeMode.SPLIT_SCREEN,
                captionPreset = CaptionPreset.NEON,
                captionFontSize = 22,
                captionPosition = "BOTTOM_THIRD",
                backgroundMusic = "Dramatic Cinematic",
                duckingPercent = 90,
                thumbnailResName = "thumb_viral_clip"
            )
        )

        metadataDao.insertMetadata(
            PlatformMetadataEntity(
                clipId = clip2Id,
                titlesJson = "[\"The Hidden Breakthrough Behind Modern AI\", \"What Silicon Valley Keeps Secret About Models\", \"How AI Really Crossed The Human Threshold\"]",
                tiktokDescription = "The secret that changed machine learning forever! Drop your thoughts below!\n#openai #techtok #datascience #deeplearning #podcast",
                instagramDescription = "This single architectural leap is what made models actually reason instead of just repeating words.\n\nShare with a developer friend who needs to hear this!\n\n#artificialintelligence #techinsights #machinelearning #neuralnetworks #podcastcommunity",
                youtubeShortsDescription = "The Breakthrough That Changed Everything in AI #Shorts #Technology #Innovation",
                facebookDescription = "What really unlocked reasoning in modern AI models? It comes down to this core architectural shift. Watch until the end for the full breakdown and share with colleagues!",
                broadHashtagsJson = "[\"#ai\", \"#technology\", \"#science\"]",
                nicheHashtagsJson = "[\"#machinelearning\", \"#neuralnetworks\", \"#deeplearning\", \"#siliconvalley\", \"#research\"]",
                trendingHashtagsJson = "[\"#shortsfeed\", \"#podcastclips\"]",
                hookText = "Nobody talks about this one shift.",
                cta = "Tag a founder who needs to see this"
            )
        )

        val clip3Id = clipDao.insertClip(
            ClipEntity(
                projectId = projectId1,
                title = "The $10M Solopreneur Playbook For 2026",
                hookText = "You don't need 50 employees anymore.",
                viralityScore = 91,
                reasoning = "Aspirational business story, actionable frameworks, strong takeaway about leverage and autonomous tools.",
                startTimeMs = 740000,
                endTimeMs = 798000,
                emotionalPeak = "Empowering Framework",
                reframeMode = ReframeMode.BLURRED_BG,
                captionPreset = CaptionPreset.BOLD_YELLOW,
                captionFontSize = 24,
                captionPosition = "BOTTOM_THIRD",
                backgroundMusic = "Lo-Fi Beats",
                duckingPercent = 80,
                thumbnailResName = "thumb_viral_clip"
            )
        )

        metadataDao.insertMetadata(
            PlatformMetadataEntity(
                clipId = clip3Id,
                titlesJson = "[\"How 1 Person Can Run A $10M Company\", \"The End Of Traditional Startup Hiring\", \"Solopreneur Leverage In The AI Era\"]",
                tiktokDescription = "You don't need 50 employees to build an empire anymore! Link in bio for full tool stack!\n#entrepreneur #solopreneur #businesstips #sidehustle #startuplife",
                instagramDescription = "The cost of building scalable software dropped by 99%.\n\nHere is how small agile 1-person teams are out-executing 50-person venture funded agencies.\n\nDouble tap if you're building solo in 2026!\n\n#entrepreneurship #businessgrowth #leanstartup #founderstories #automationtools",
                youtubeShortsDescription = "How Solo Founders Are Building $10M Startups | Business Masterclass #Shorts #Business #Entrepreneur",
                facebookDescription = "You don't need 50 employees to build a multi-million dollar software enterprise anymore. Here is how modern leverage changes the game for creators and entrepreneurs. Follow for more!",
                broadHashtagsJson = "[\"#money\", \"#business\", \"#success\"]",
                nicheHashtagsJson = "[\"#solopreneur\", \"#startuptips\", \"#passiveincome\", \"#bootstrap\", \"#indiehacker\"]",
                trendingHashtagsJson = "[\"#shorts\", \"#growthhacks\"]",
                hookText = "You don't need 50 employees anymore.",
                cta = "Follow for daily founder tactics"
            )
        )

        // Seed an export history item
        exportDao.insertExport(
            ExportEntity(
                clipId = clip1Id,
                quality = "1080p",
                format = "MP4",
                fps = 60,
                bitratePreset = "Balanced",
                burnedCaptions = true,
                removeWatermark = true,
                includeSrt = true,
                status = "COMPLETED",
                progress = 100,
                estimatedSizeMb = 34.8f,
                downloadUrl = "https://clipforge.app/download/export_c1_1080p.mp4"
            )
        )
    }

    fun getClipsForProject(projectId: Long): Flow<List<ClipEntity>> = clipDao.getClipsForProject(projectId)

    suspend fun getClipById(clipId: Long): ClipEntity? = clipDao.getClipById(clipId)

    fun getMetadataForClip(clipId: Long): Flow<PlatformMetadataEntity?> = metadataDao.getMetadataForClip(clipId)

    suspend fun updateClip(clip: ClipEntity) = clipDao.updateClip(clip)

    suspend fun updateMetadata(metadata: PlatformMetadataEntity) = metadataDao.updateMetadata(metadata)

    suspend fun deleteProject(projectId: Long) = projectDao.deleteProjectById(projectId)

    // Pipeline runner with realistic callback stages
    suspend fun runVideoPipeline(
        title: String,
        sourceType: SourceType,
        sourceUrl: String,
        onProgress: suspend (stepIndex: Int, stepName: String, progress: Float) -> Unit
    ): Long = withContext(Dispatchers.IO) {
        // Step 1: Ingestion & Audio Extraction
        onProgress(0, "Ingesting source video & extracting high-fidelity audio (FFmpeg)...", 0.15f)
        delay(600)

        // Step 2: Transcription with word-level timestamps
        onProgress(1, "Running Whisper AI model with word-level timestamps & speaker diarization...", 0.35f)
        delay(700)

        // Step 3: AI Viral Moment Detection
        onProgress(2, "Analyzing emotional hooks, controversy, and virality scoring...", 0.60f)
        delay(700)

        // Step 4: Smart Reframing (16:9 -> 9:16)
        onProgress(3, "Detecting face tracking & reframing 16:9 to vertical 9:16...", 0.80f)
        delay(600)

        // Step 5: Caption generation & Metadata
        onProgress(4, "Generating animated karaoke subtitles & multi-platform metadata...", 0.95f)
        delay(500)

        // Create Project in Database
        val projectId = projectDao.insertProject(
            ProjectEntity(
                title = title.ifBlank { "Viral Clip Compilation #${System.currentTimeMillis() % 1000}" },
                sourceType = sourceType,
                sourceUrl = sourceUrl,
                durationSeconds = 3420,
                videoSizeMb = 840.0f,
                status = ProjectStatus.READY,
                clipCount = 2,
                thumbnailResName = "thumb_tech_podcast"
            )
        )

        // Create Top Viral Clips for the newly processed project
        val clipA = clipDao.insertClip(
            ClipEntity(
                projectId = projectId,
                title = "The Harsh Reality Nobody Tells You About Success",
                hookText = "If you don't hear this, you'll waste 5 years.",
                viralityScore = 96,
                reasoning = "Direct confronting hook, rapid pacing in opening 4 seconds, clear transformation arc with emotional payoff.",
                startTimeMs = 60000,
                endTimeMs = 115000,
                emotionalPeak = "High Urgency Wake-Up Call",
                reframeMode = ReframeMode.SPEAKER_TRACK,
                captionPreset = CaptionPreset.HORMOZI,
                captionFontSize = 24,
                captionPosition = "BOTTOM_THIRD",
                backgroundMusic = "Upbeat Viral",
                duckingPercent = 85,
                thumbnailResName = "thumb_viral_clip"
            )
        )

        metadataDao.insertMetadata(
            PlatformMetadataEntity(
                clipId = clipA,
                titlesJson = "[\"The Advice I Wish I Got 5 Years Ago\", \"Stop Doing This If You Want To Win\", \"The 1 Mistake 90% of People Make\"]",
                tiktokDescription = "Listen carefully to this before you take your next step! What do you think?\n#mindset #motivation #success #advice #growth",
                instagramDescription = "Most people stay stuck not because they lack talent, but because they focus on the wrong 80%.\n\nSave this for when you need a reminder!\n\n#entrepreneurmindset #selfgrowth #lifeadvice #leadership #focus",
                youtubeShortsDescription = "The Advice That Saves You 5 Years of Struggle #Shorts #Mindset #Success",
                facebookDescription = "This single mindset shift could save you 5 years of trial and error. Watch closely and let us know your perspective in the comments!",
                broadHashtagsJson = "[\"#motivation\", \"#success\", \"#life\"]",
                nicheHashtagsJson = "[\"#discipline\", \"#habits\", \"#growthmindset\", \"#productivity\", \"#advice\"]",
                trendingHashtagsJson = "[\"#shortsclip\", \"#dailyadvice\"]",
                hookText = "If you don't hear this, you'll waste 5 years.",
                cta = "Follow for daily unfiltered insights"
            )
        )

        val clipB = clipDao.insertClip(
            ClipEntity(
                projectId = projectId,
                title = "Why Being Consistent Is A Trap (Do This Instead)",
                hookText = "Consistency without this is pure delusion.",
                viralityScore = 92,
                reasoning = "Pattern interrupt: attacks conventional wisdom ('consistency is key'), high debate and comment velocity trigger.",
                startTimeMs = 240000,
                endTimeMs = 295000,
                emotionalPeak = "Controversial Contrarian Take",
                reframeMode = ReframeMode.SPLIT_SCREEN,
                captionPreset = CaptionPreset.NEON,
                captionFontSize = 22,
                captionPosition = "BOTTOM_THIRD",
                backgroundMusic = "Lo-Fi Beats",
                duckingPercent = 80,
                thumbnailResName = "thumb_viral_clip"
            )
        )

        metadataDao.insertMetadata(
            PlatformMetadataEntity(
                clipId = clipB,
                titlesJson = "[\"Why Consistency Is Overrated\", \"The Dark Side of Just Showing Up\", \"Quality vs Consistency: The Truth\"]",
                tiktokDescription = "Are you being consistent with the wrong thing? Let's debate in the comments!\n#opinion #truth #productivitytips #mindsetmatters",
                instagramDescription = "If you run in the wrong direction with 100% consistency, you just arrive at failure faster.\n\nShare this with someone who needs a reality check!\n\n#deepwork #productivity #businesshacks #criticalthinking",
                youtubeShortsDescription = "Why 'Just Being Consistent' Keeps You Broke #Shorts #Debate #Productivity",
                facebookDescription = "Is consistency actually keeping you stuck? Here is why direction and leverage matter infinitely more than just showing up. Share with a colleague who needs to see this!",
                broadHashtagsJson = "[\"#focus\", \"#strategy\", \"#tips\"]",
                nicheHashtagsJson = "[\"#highperformance\", \"#strategicthinking\", \"#deepwork\", \"#clarity\", \"#systems\"]",
                trendingHashtagsJson = "[\"#viralshorts\", \"#podcastclips\"]",
                hookText = "Consistency without this is pure delusion.",
                cta = "Double tap if you agree"
            )
        )

        val clipC = clipDao.insertClip(
            ClipEntity(
                projectId = projectId,
                title = "The Unspoken Rule of High-Leverage Systems",
                hookText = "Your output depends on your systems, not your hours.",
                viralityScore = 90,
                reasoning = "Actionable framework on leverage, resonates with founders and engineers.",
                startTimeMs = 410000,
                endTimeMs = 460000,
                emotionalPeak = "Actionable Framework",
                reframeMode = ReframeMode.BLURRED_BG,
                captionPreset = CaptionPreset.BOLD_YELLOW,
                captionFontSize = 24,
                captionPosition = "BOTTOM_THIRD",
                backgroundMusic = "Tech Ambient",
                duckingPercent = 85,
                thumbnailResName = "thumb_viral_clip"
            )
        )

        metadataDao.insertMetadata(
            PlatformMetadataEntity(
                clipId = clipC,
                titlesJson = "[\"Systems vs Hours: How Leverage Works\", \"Stop Trading Time For Output\", \"The High-Leverage Playbook\"]",
                tiktokDescription = "Your output depends on your systems, not your hours! Save this for later.\n#leverage #systems #productivity #founder #tech",
                instagramDescription = "Stop measuring productivity by hours worked. Build systems that scale while you rest.\n\nBookmark this framework!\n\n#systems #entrepreneurship #founderlife #techstartup #productivityhacks",
                youtubeShortsDescription = "Systems vs Hours: The High-Leverage Playbook #Shorts #Productivity #Business",
                facebookDescription = "Why working more hours is rarely the answer in 2026. Here is how high-leverage systems multiply your output.",
                broadHashtagsJson = "[\"#business\", \"#productivity\", \"#tech\"]",
                nicheHashtagsJson = "[\"#systems\", \"#leverage\", \"#automation\", \"#workflows\", \"#founders\"]",
                trendingHashtagsJson = "[\"#shorts\", \"#podcastclips\"]",
                hookText = "Your output depends on your systems, not your hours.",
                cta = "Save this framework for your workflow"
            )
        )

        projectDao.updateProject(
            ProjectEntity(
                id = projectId,
                title = title.ifBlank { "Viral Clip Compilation #${System.currentTimeMillis() % 1000}" },
                sourceType = sourceType,
                sourceUrl = sourceUrl,
                durationSeconds = 3420,
                videoSizeMb = 840.0f,
                status = ProjectStatus.READY,
                clipCount = 3,
                thumbnailResName = "thumb_tech_podcast"
            )
        )

        onProgress(5, "Ready! 3 viral clips extracted.", 1.0f)
        projectId
    }

    // Interactive Transcript Words for Editor
    fun getTranscriptWordsForClip(clipId: Long): List<WordTimestamp> {
        val baseText = "If you want to build massive leverage in today's digital economy you have to realize that typing code line by line is completely obsolete. The real 100x engineers are architects who orchestrate autonomous AI agents and verify systems with laser precision. Stop wasting time on boilerplate and focus on deep problem solving."
        val words = baseText.split(" ")
        var currentMs = 0L
        return words.mapIndexed { idx, word ->
            val duration = (word.length * 55L) + 120L
            val item = WordTimestamp(
                word = word,
                startMs = currentMs,
                endMs = currentMs + duration,
                speaker = if (idx % 12 > 6) "Guest" else "Host",
                isCut = false
            )
            currentMs += duration + 40L
            item
        }
    }

    // Export Job Simulation
    suspend fun createExportJob(
        clipId: Long,
        quality: String,
        format: String,
        fps: Int,
        bitratePreset: String,
        burnedCaptions: Boolean,
        removeWatermark: Boolean,
        includeSrt: Boolean,
        onProgress: suspend (progress: Int, status: String) -> Unit
    ): Long = withContext(Dispatchers.IO) {
        val estimatedSizeMb = when (quality) {
            "480p" -> 14.2f
            "720p" -> 28.5f
            "1080p" -> 54.0f
            "4K" -> 180.5f
            else -> 42.0f
        }
        val exportId = exportDao.insertExport(
            ExportEntity(
                clipId = clipId,
                quality = quality,
                format = format,
                fps = fps,
                bitratePreset = bitratePreset,
                burnedCaptions = burnedCaptions,
                removeWatermark = removeWatermark,
                includeSrt = includeSrt,
                status = "RENDERING",
                progress = 10,
                estimatedSizeMb = estimatedSizeMb,
                downloadUrl = ""
            )
        )

        for (p in listOf(25, 50, 75, 90, 100)) {
            delay(400)
            onProgress(p, if (p < 100) "Encoding $quality $format ($p%)..." else "Complete!")
        }

        val finalUrl = "https://clipforge.app/downloads/clip_${clipId}_${quality}_${fps}fps.${format.lowercase()}"
        exportDao.updateExport(
            ExportEntity(
                id = exportId,
                clipId = clipId,
                quality = quality,
                format = format,
                fps = fps,
                bitratePreset = bitratePreset,
                burnedCaptions = burnedCaptions,
                removeWatermark = removeWatermark,
                includeSrt = includeSrt,
                status = "COMPLETED",
                progress = 100,
                estimatedSizeMb = estimatedSizeMb,
                downloadUrl = finalUrl
            )
        )
        exportId
    }
}
