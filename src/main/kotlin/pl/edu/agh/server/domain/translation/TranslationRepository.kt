package pl.edu.agh.server.domain.translation

import org.springframework.data.repository.query.Param
import pl.edu.agh.server.foundation.domain.BaseRepository
import java.util.*


interface TranslationRepository : BaseRepository<Translation> {
    fun findByTranslationIdInAndLanguage(
        @Param("translationIdList") translationIdList: List<UUID>,
        @Param("language") languageOption: LanguageOption
    ): List<Translation>
}