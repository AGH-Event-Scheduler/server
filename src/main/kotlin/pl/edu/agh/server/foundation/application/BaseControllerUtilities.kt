package pl.edu.agh.server.foundation.application

import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import pl.edu.agh.server.config.JwtService
import pl.edu.agh.server.foundation.domain.BaseIdentifiableEntity

abstract class BaseControllerUtilities<T : BaseIdentifiableEntity>(
    private val jwtService: JwtService,
) {

    protected fun createPageRequest(
        page: Int = 0,
        size: Int = Integer.MAX_VALUE,
        sort: String = "id,asc",
    ): PageRequest {
        val sortParams = sort.split(",")
        val sortBy = sortParams[0]
        val sortDirection = if (sortParams.size > 1) Sort.Direction.fromString(sortParams[1]) else Sort.Direction.ASC
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
    }

    protected fun getUserName(request: HttpServletRequest): String {
        return jwtService.extractUsername(request.getHeader("Authorization")!!.substring(7))
    }
}
