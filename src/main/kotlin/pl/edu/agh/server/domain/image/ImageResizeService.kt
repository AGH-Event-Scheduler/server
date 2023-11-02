package pl.edu.agh.server.domain.image

import org.springframework.stereotype.Service
import java.awt.Image
import java.awt.Image.SCALE_FAST
import java.awt.image.BufferedImage

@Service
class ImageResizeService {
    fun resize(bufferedImage: BufferedImage, newWidth: Int, newHeight: Int): Image {
        return bufferedImage.getScaledInstance(newWidth, newHeight, SCALE_FAST)
    }
}
