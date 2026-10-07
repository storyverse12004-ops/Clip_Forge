# ClipForge — AI Viral Video Clipper & Shorts Studio

**Repository:** `storyverse12004-ops/Clip_Forge`  
**Package ID:** `com.aistudio.clipforge.qxvrk`

Converts long videos (podcasts, interviews, lectures, streams) into viral 9:16 short-form clips for **YouTube Shorts**, **TikTok**, **Instagram Reels**, and **Facebook Reels**.

## Features
- **Ingestion Pipeline:** File upload (`MP4`/`MOV`) via zero-permission Android Photo/Video Picker or YouTube link import.
- **AI Viral Moment Scoring (0–100):** Ranks top segments by opening hook retention, emotional peak, and story payoff.
- **Smart 9:16 Reframing:** Active speaker tracking, 2-speaker split screen, and blurred background modes with safe-zone overlays.
- **6 Animated Karaoke Caption Styles:** Hormozi Punch, Minimal Clean, Neon Glow, Punchy Yellow, Red Urgency, and Emoji Pop.
- **Multi-Platform AI Metadata:** 3 hook titles, platform-tailored descriptions, and broad/niche/trending hashtag sets with 1-tap copy.
- **Tactile Studio Audio:** Zero-latency 44.1 kHz PCM button and tab-selection sound effects.
- **Google AdMob Integrated:**
  - **App ID:** `ca-app-pub-1374968715711516~1495134749`
  - **App Open Ad:** `ca-app-pub-1374968715711516/5276782879`
  - **Interstitial Ad:** `ca-app-pub-1374968715711516/1826307230`
  - **Banner Ad:** `ca-app-pub-1374968715711516/7584980500`

## Pushing to `storyverse12004-ops/Clip_Forge` & Building APK
1. In the Google AI Studio top toolbar, click the **GitHub / Export** icon and push directly to **`storyverse12004-ops/Clip_Forge`** (or export as ZIP).
2. A GitHub Actions workflow (`.github/workflows/android-build.yml`) is pre-configured to build and upload the `ClipForge-debug-apk` artifact automatically on every push or manual workflow dispatch.
3. You can also download the APK/AAB directly from AI Studio via **Settings -> Generate APK / AAB**.
