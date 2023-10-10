package pl.edu.agh.server.domain.user

import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository, val userDetailsRepository: UserDetailsRepository) {

    fun saveUserDetails(userDetails: UserDetails): UserDetails {
        userDetailsRepository.save(userDetails)
        userDetails.user.id?.let {
            userRepository.findById(it).ifPresent { user ->
                user.userDetails = userDetails
                userRepository.save(user)
            }
        }
        return userDetails
    }
}
