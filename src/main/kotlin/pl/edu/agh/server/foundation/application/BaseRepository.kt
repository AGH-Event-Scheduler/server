package pl.edu.agh.server.foundation.application

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BaseRepository<T> : JpaRepository<T, Long>, JpaSpecificationExecutor<T>