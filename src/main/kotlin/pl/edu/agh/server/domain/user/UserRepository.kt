package pl.edu.agh.server.domain.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pl.edu.agh.server.domain.dto.UserDTO
import pl.edu.agh.server.domain.dto.UserWithRoleDTO
import pl.edu.agh.server.foundation.domain.BaseRepository
import java.util.*

@Repository
interface UserRepository : BaseRepository<User> {
    fun findByEmail(email: String): Optional<User>

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    fun findIdByEmail(email: String): Optional<Long>

    fun existsByEmail(email: String): Boolean

    @Query(
        "SELECT new pl.edu.agh.server.domain.dto.UserWithRoleDTO(" +
            "u.email," +
            " u.name," +
            " u.lastName," +
            " our.role," +
            " our.organization.id) " +
            "FROM User u LEFT JOIN OrganizationUserRole our ON  our.organization.id = :organizationId " +
            "AND our.user.id = u.id " +
            "WHERE our.organization.id = :organizationId OR our.organization.id IS NULL " +
            "ORDER BY u.name ASC",
    )
    fun findAllUsersWithRoleForOrganization(
        pageable: Pageable,
        @Param("organizationId") organizationId: Long,
    ): Page<UserWithRoleDTO>

    @Query(
        "SELECT new pl.edu.agh.server.domain.dto.UserWithRoleDTO(" +
            "u.email," +
            " u.name," +
            " u.lastName," +
            " our.role," +
            " our.organization.id) " +
            "FROM User u " +
            "LEFT JOIN OrganizationUserRole our ON our.organization.id = :organizationId AND our.user.id = u.id " +
            "WHERE (our.organization.id = :organizationId OR our.organization.id IS NULL) " +
            "AND (u.name LIKE %:search% OR u.lastName LIKE %:search% OR u.email LIKE %:search% " +
            "OR CONCAT(u.name, ' ', u.lastName) LIKE %:search%) " +
            "ORDER BY u.name ASC",
    )
    fun findAllUsersWithRoleForOrganizationFiltered(
        pageable: Pageable,
        @Param("organizationId") organizationId: Long,
        @Param("search") search: String,
    ): Page<UserWithRoleDTO>

    @Query(
        "SELECT new pl.edu.agh.server.domain.dto.UserDTO(" +
            "u.email, " +
            "u.name, " +
            "u.lastName) " +
            "FROM User u",
    )
    fun findAllUsers(pageable: Pageable): Page<UserDTO>
}
