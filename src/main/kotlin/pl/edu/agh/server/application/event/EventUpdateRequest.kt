package pl.edu.agh.server.application.event

import org.springframework.web.multipart.MultipartFile

data class EventUpdateRequest(
    val backgroundImage: MultipartFile?,
    val name: String,
    val description: String,
    val location: String,
    val startDateTimestamp: Long,
    val endDateTimestamp: Long,
)
