package pl.edu.agh.server.domain.annotation

/**
 * Annotation to specify access control for a method.
 *
 * @param allowedRoles The roles allowed to access the method. (passed as array of strings)
 *
 * annotation requires request: HttpServletRequest, @PathVariable organizationId: Long to be passed as first to arguments
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class AuthorizeAccess(
    val allowedRoles: Array<String> = [], // TODO  to be mapped to enum
)