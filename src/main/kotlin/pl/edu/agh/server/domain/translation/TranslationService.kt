package pl.edu.agh.server.domain.translation

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class TranslationService(private val translationRepository: TranslationRepository) {
    @Transactional
    fun createTranslation(
        contentLanguageMap: Map<LanguageOption, String>,
    ): UUID {
        val translationId = generateTranslationId()

        LanguageOption.values().forEach {
            translationRepository.save(Translation(translationId, contentLanguageMap[it].toString(), it))
        }

        return translationId
    }

    fun getTranslations(
        translationIdList: List<UUID>,
        language: LanguageOption,
    ): List<Translation> {
        return translationRepository.findByTranslationIdInAndLanguage(translationIdList, language)
    }

    private fun generateTranslationId(): UUID {
        return UUID.randomUUID()
    }
}
