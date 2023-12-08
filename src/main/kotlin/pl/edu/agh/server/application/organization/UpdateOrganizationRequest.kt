package pl.edu.agh.server.application.organization

import org.springframework.web.multipart.MultipartFile

data class UpdateOrganizationRequest(
    val backgroundImage: MultipartFile?,
    val logoImage: MultipartFile?,
    val name: String,
    val description: String,
)
