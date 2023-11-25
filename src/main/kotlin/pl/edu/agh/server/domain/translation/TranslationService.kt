package pl.edu.agh.server.domain.translation

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class TranslationService(private val translationRepository: TranslationRepository) {
    @Transactional
    fun createTranslation(
        contentLanguageMap: Map<LanguageOption, String>,
    ): MutableSet<Translation> {
        return LanguageOption.entries.map {
            translationRepository.save(Translation(contentLanguageMap[it].toString(), it))
        }.toMutableSet()
    }
}
