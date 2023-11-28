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

    @Query(
        "SELECT new pl.edu.agh.server.domain.dto.UserWithRoleDTO(" +
            "u.email," +
            " u.name," +
            " u.lastName," +
            " our.role," +
            " our.organization.id) " +
            "FROM User u LEFT JOIN OrganizationUserRole our on  our.organization.id = :organizationId" +
            " AND our.user.id = u.id " +
            " where our.organization.id = :organizationId OR our.organization.id IS NULL",
    )
    fun findAllUsersWithRoleForOrganization(
        pageable: Pageable,
        @Param("organizationId") organizationId: Long,
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
