package pl.edu.agh.server.domain.image

import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.Image
import java.awt.Image.SCALE_REPLICATE
import java.awt.image.BufferedImage
import java.util.UUID
import javax.imageio.ImageIO

@Service
class ImageService(private val imageStorage: ImageStorage) {
    fun resize(bufferedImage: BufferedImage, newWidth: Int, newHeight: Int): Image {
        return bufferedImage.getScaledInstance(newWidth, newHeight, SCALE_REPLICATE)
    }

    fun createBackgroundImage(backgroundImage: MultipartFile): BackgroundImage {
        if (!imageStorage.checkIfImageWithProperExtensions(backgroundImage)) {
            throw IncorrectFileUploadException("Uploaded file type is not supported")
        }

        val imageId = imageStorage.generateImageId()
        val extension = backgroundImage.originalFilename?.let { imageStorage.getFileExtension(it) }
        if (extension === "") {
            throw IncorrectFileUploadException("Could not resolve file extension")
        }
        val smallName = "small.$extension"
        val mediumName = "medium.$extension"
        val bigName = "big.$extension"

        imageStorage.createImageDirectory(imageId)

        val originalBufferedImage: BufferedImage = ImageIO.read(backgroundImage.inputStream)

        val smallBackgroundImage: Image = resize(originalBufferedImage, BackgroundImage.SMALL_SIZE[0], BackgroundImage.SMALL_SIZE[1])
        imageStorage.saveFile(smallBackgroundImage, imageId, smallName)

        val mediumBackgroundImage: Image = resize(originalBufferedImage, BackgroundImage.MEDIUM_SIZE[0], BackgroundImage.MEDIUM_SIZE[1])
        imageStorage.saveFile(mediumBackgroundImage, imageId, mediumName)

        val bigBackgroundImage: Image = resize(originalBufferedImage, BackgroundImage.BIG_SIZE[0], BackgroundImage.BIG_SIZE[1])
        imageStorage.saveFile(bigBackgroundImage, imageId, bigName)

        return BackgroundImage(
            imageId = imageId,
            smallFilename = smallName,
            mediumFilename = mediumName,
            bigFilename = bigName,
        )
    }

    fun removeBackgroundImage(imageId: UUID) {
        imageStorage.deleteImage(imageId)
    }

    fun getFile(imageId: UUID, filename: String): Resource {
        val regex = Regex("^(big|medium|small)[.](png|jpg|jpeg)$")
        if (!regex.matches(filename)) {
            throw IncorrectFileAccessException("Incorrect filename provided")
        }
        return imageStorage.getFile(imageId, filename)
    }

    class IncorrectFileUploadException(s: String) : RuntimeException(s)

    class IncorrectFileAccessException(s: String) : RuntimeException(s)
}
