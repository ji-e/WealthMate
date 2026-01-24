@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.database.eneity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@ProvidedTypeConverter
class CategoryConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromTagList(value: List<CategoryTagEntity>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toTagList(value: String): List<CategoryTagEntity> {
        return json.decodeFromString(value)
    }
}

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val icon: String,
    val largeCategory: String,
    val middleLabel: String,
    val sort: Long,
    val isFixed: Boolean,
    val tags: List<CategoryTagEntity>,
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isDeleted: Boolean = false,
)

// CategoryTagEntity은 별도 Entity가 아닌 임베디드 데이터로 사용
@Serializable
data class CategoryTagEntity(
    val id: String,
    val tagLabel: String,
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isDeleted: Boolean = false,
)


