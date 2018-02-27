package com.kingdom.repository

import com.kingdom.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Int> {

    fun findAllByOrderByLastLogin(): List<User>

    fun findByEmail(email: String): User

    fun findByUsername(username: String): User

    fun findByUsernameAndPassword(username: String, password: String): User
}
