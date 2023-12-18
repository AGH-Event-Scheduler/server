package pl.edu.agh.server.domain.annotation

/**
 * Annotation to specify access control for a method.
 *
 * annotation requires request: HttpServletRequest
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class AdminRestricted
