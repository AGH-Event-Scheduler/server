package pl.edu.agh.server.domain.file

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

@Service
class ImageStorage(@Value("\${file.upload-dir}") private val uploadDir: String) {

    fun createImageDirectory(imageId: UUID) {
        val path = "$uploadDir/$imageId"
        println(path)
        val folder = File(path)
        if (folder.exists()) {
            throw ImageStorageException("ImageID is already in use")
        }
        if (!folder.mkdir()) {
            throw ImageStorageException("Directory creation failed")
        }
    }

    fun saveFile(bufferedImage: BufferedImage, imageId: UUID, filename: String) {
        try {
            val targetPath: Path = Paths.get(uploadDir, imageId.toString(), filename)
            ImageIO.write(bufferedImage, getFileExtension(filename), targetPath.toFile())
        } catch (e: IOException) {
            throw ImageStorageException("Failed to save image: $filename")
        }
    }

    fun checkIfImageWithProperExtensions(multipartFile: MultipartFile): Boolean {
        return multipartFile.contentType?.startsWith("image/") == true && (multipartFile.originalFilename?.endsWith("png") == true || multipartFile.originalFilename?.endsWith("jpg") == true || multipartFile.originalFilename?.endsWith("jpeg") == true)
    }

    fun getFileExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        if (lastDotIndex == -1) {
            return "" // No file extension found
        }
        return fileName.substring(lastDotIndex + 1).lowercase(Locale.getDefault())
    }

    fun deleteImage(imageId: UUID) {}

    fun getFile(imageId: UUID, filename: String): Resource {
        return FileSystemResource("$uploadDir") // TODO
    }

    fun generateImageId(): UUID {
        return UUID.randomUUID()
    }

    class ImageStorageException(s: String) : RuntimeException(s)
}
