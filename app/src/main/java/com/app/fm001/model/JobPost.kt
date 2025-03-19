import com.app.fm001.model.EventType
import java.util.Date

data class JobPost(
    val id: String,
    val title: String,
    val description: String,
    val budget: Double, // Change from Int to Double
    val location: String,
    val eventDate: Date,
    val eventType: EventType, // Use EventType enum
    val postedBy: String, // Add this field
    val postedDate: Date,
    val clientId: String,
    val clientName: String,
    val requirements: List<String>,
    val status: Status
) {
    enum class Status {
        OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    }
}