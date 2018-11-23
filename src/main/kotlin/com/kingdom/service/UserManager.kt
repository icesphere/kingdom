package com.kingdom.service

import com.kingdom.model.User
import com.kingdom.repository.UserDao
import com.kingdom.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserManager(private val dao: UserDao,
                  private val userRepository: UserRepository) {

    val users: List<User>
        get() = userRepository.findAllByOrderByLastLogin()

    val errorCount: Int
        get() = dao.errorCount

    fun getUsers(stat: String, value: Int?): List<User> {
        return dao.getUsers(stat, value)
    }

    fun getUser(userId: Int): User {
        return userRepository.findById(userId).get()
    }

    fun getUser(username: String, password: String): User? {
        return userRepository.findByUsernameAndPassword(username, password)
    }

    fun getUser(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun usernameExists(username: String): Boolean {
        return userRepository.findByUsername(username) != null
    }

    fun saveUser(user: User) {
        userRepository.save(user)
    }

    fun deleteUser(user: User) {
        userRepository.delete(user)
    }

    fun calculateGameStats(user: User) {
        if (user.stats == null) {
            dao.calculateGameStats(user)
        }
    }
}
