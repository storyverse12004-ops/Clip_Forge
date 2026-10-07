"""
ClipForge FastAPI Backend
Video ingestion, project management, and clip export endpoints.
"""
from fastapi import FastAPI, UploadFile, File
from pydantic import BaseModel
import uuid

app = FastAPI(
    title="ClipForge Video Pipeline API",
    version="1.0.0",
    description="Converts long-form videos into viral vertical short clips."
)

class IngestYouTubeRequest(BaseModel):
    youtube_url: str
    user_id: str
    remove_silence: bool = True
    filler_words_clean: bool = True
    speaker_diarization: bool = True
    target_language: str = "en"

class ExportClipRequest(BaseModel):
    clip_id: str
    quality: str = "1080p"
    format: str = "MP4"
    fps: int = 60
    bitrate: str = "Balanced"
    burn_captions: bool = True
    remove_watermark: bool = True
    include_srt: bool = True

@app.get("/health")
def health_check():
    return {"status": "ok", "service": "ClipForge API"}

@app.post("/api/ingest/youtube")
def ingest_youtube(req: IngestYouTubeRequest):
    project_id = f"proj_{uuid.uuid4().hex[:10]}"
    return {
        "status": "QUEUED",
        "project_id": project_id,
        "message": "Video downloaded and audio extraction scheduled"
    }

@app.post("/api/ingest/upload")
async def upload_video(file: UploadFile = File(...)):
    project_id = f"proj_{uuid.uuid4().hex[:10]}"
    return {
        "status": "QUEUED",
        "project_id": project_id,
        "filename": file.filename,
        "message": "File received, processing pipeline queued"
    }

@app.get("/api/projects/{project_id}/status")
def get_project_status(project_id: str):
    return {
        "project_id": project_id,
        "stage": "FINDING_VIRAL_MOMENTS",
        "progress": 0.65,
        "details": "Analyzing hook retention"
    }

@app.get("/api/projects/{project_id}/clips")
def get_project_clips(project_id: str):
    return {
        "project_id": project_id,
        "clips": [
            {
                "id": "clip_01",
                "title": "Why 99% of Software Engineers Will Become Orchestrators",
                "virality_score": 98,
                "hook_text": "Stop writing boilerplate code today.",
                "duration_seconds": 52,
                "reframe_mode": "SPEAKER_TRACK",
                "caption_preset": "HORMOZI"
            }
        ]
    }

@app.post("/api/clips/{clip_id}/export")
def create_export(clip_id: str, req: ExportClipRequest):
    job_id = f"exp_{uuid.uuid4().hex[:8]}"
    return {
        "job_id": job_id,
        "clip_id": clip_id,
        "status": "RENDERING",
        "quality": req.quality,
        "fps": req.fps,
        "estimated_seconds": 15
    }

@app.get("/api/exports/{job_id}")
def get_export_status(job_id: str):
    return {
        "job_id": job_id,
        "status": "COMPLETED",
        "progress": 100,
        "download_url": f"https://r2.clipforge.app/exports/{job_id}.mp4",
        "expires_in_seconds": 3600
    }
