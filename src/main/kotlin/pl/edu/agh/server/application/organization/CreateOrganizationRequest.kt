package pl.edu.agh.server.application.organization

import org.springframework.web.multipart.MultipartFile

data class CreateOrganizationRequest(
    val backgroundImage: MultipartFile,
    val logoImage: MultipartFile,
    val name: String,
    val description: String,
    val leaderEmail: String
)
