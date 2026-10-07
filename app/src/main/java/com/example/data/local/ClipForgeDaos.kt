package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClipEntity
import com.example.data.model.ExportEntity
import com.example.data.model.PlatformMetadataEntity
import com.example.data.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)
}

@Dao
interface ClipDao {
    @Query("SELECT * FROM clips WHERE projectId = :projectId ORDER BY viralityScore DESC")
    fun getClipsForProject(projectId: Long): Flow<List<ClipEntity>>

    @Query("SELECT * FROM clips ORDER BY viralityScore DESC")
    fun getAllClips(): Flow<List<ClipEntity>>

    @Query("SELECT * FROM clips WHERE id = :id")
    suspend fun getClipById(id: Long): ClipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(clip: ClipEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClips(clips: List<ClipEntity>)

    @Update
    suspend fun updateClip(clip: ClipEntity)

    @Query("DELETE FROM clips WHERE id = :id")
    suspend fun deleteClipById(id: Long)
}

@Dao
interface PlatformMetadataDao {
    @Query("SELECT * FROM platform_metadata WHERE clipId = :clipId LIMIT 1")
    fun getMetadataForClip(clipId: Long): Flow<PlatformMetadataEntity?>

    @Query("SELECT * FROM platform_metadata WHERE clipId = :clipId LIMIT 1")
    suspend fun getMetadataDirect(clipId: Long): PlatformMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: PlatformMetadataEntity): Long

    @Update
    suspend fun updateMetadata(metadata: PlatformMetadataEntity)
}

@Dao
interface ExportDao {
    @Query("SELECT * FROM exports ORDER BY createdAt DESC")
    fun getAllExports(): Flow<List<ExportEntity>>

    @Query("SELECT * FROM exports WHERE clipId = :clipId ORDER BY createdAt DESC")
    fun getExportsForClip(clipId: Long): Flow<List<ExportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExport(export: ExportEntity): Long

    @Update
    suspend fun updateExport(export: ExportEntity)
}
