import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleDriveFileEntity(
    val files: List<DriveFileEntity>,
)

@Serializable
data class DriveFileEntity(
    val id: String,
    val name: String,
    val modifiedTime: String? = null,
)
