package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.CaptionPreset
import com.example.data.model.ClipEntity
import com.example.data.model.ExportEntity
import com.example.data.model.PlatformMetadataEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectStatus
import com.example.data.model.ReframeMode
import com.example.data.model.SourceType

class Converters {
    @TypeConverter
    fun fromSourceType(value: SourceType): String = value.name

    @TypeConverter
    fun toSourceType(value: String): SourceType = try {
        SourceType.valueOf(value)
    } catch (_: Exception) {
        SourceType.UPLOAD
    }

    @TypeConverter
    fun fromProjectStatus(value: ProjectStatus): String = value.name

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus = try {
        ProjectStatus.valueOf(value)
    } catch (_: Exception) {
        ProjectStatus.READY
    }

    @TypeConverter
    fun fromReframeMode(value: ReframeMode): String = value.name

    @TypeConverter
    fun toReframeMode(value: String): ReframeMode = try {
        ReframeMode.valueOf(value)
    } catch (_: Exception) {
        ReframeMode.SPEAKER_TRACK
    }

    @TypeConverter
    fun fromCaptionPreset(value: CaptionPreset): String = value.name

    @TypeConverter
    fun toCaptionPreset(value: String): CaptionPreset = try {
        CaptionPreset.valueOf(value)
    } catch (_: Exception) {
        CaptionPreset.HORMOZI
    }
}

@Database(
    entities = [
        ProjectEntity::class,
        ClipEntity::class,
        PlatformMetadataEntity::class,
        ExportEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ClipForgeDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun clipDao(): ClipDao
    abstract fun platformMetadataDao(): PlatformMetadataDao
    abstract fun exportDao(): ExportDao

    companion object {
        @Volatile
        private var INSTANCE: ClipForgeDatabase? = null

        fun getDatabase(context: Context): ClipForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClipForgeDatabase::class.java,
                    "clipforge_db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
