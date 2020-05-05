package com.kingdom.service

import com.kingdom.model.User
import com.kingdom.util.removeSpaces
import java.util.*

object LoggedInUsers {

    private val users = HashMap<String, User>()

    fun userLoggedIn(user: User) {
        updateUser(user, true, true)
    }

    fun userLoggedOut(user: User) {
        users.remove(user.userId)
    }

    fun gameReset(userId: String) {
        val user = users[userId]
        if (user != null) {
            user.gameId = null
            user.lastActivity = Date()
        }
    }

    fun getUsers(): List<User> {
        return ArrayList(users.values)
    }

    fun getUser(userId: String): User? {
        return users[userId]
    }

    fun usernameBeingUsed(username: String): Boolean {
        return users.values.any { it.username.removeSpaces().toLowerCase() == username.removeSpaces().toLowerCase() }
    }

    fun getUserByUsername(username: String): User? {
        return users.values.firstOrNull { it.username == username }
    }

    fun updateUser(user: User) {
        updateUser(user, true, false)
    }

    fun refreshLobby(user: User) {
        updateUser(user, false, false)
    }

    fun updateUserStatus(user: User) {
        updateUser(user, true, true)
    }

    private fun updateUser(user: User, refreshLastActivity: Boolean, updateStatus: Boolean) {
        var loggedInUser: User? = users[user.userId]
        if (loggedInUser == null) {
            loggedInUser = user
        }
        loggedInUser.gameId = user.gameId
        if (updateStatus) {
            loggedInUser.status = user.status
        }
        if (refreshLastActivity) {
            loggedInUser.lastActivity = Date()
        }
        loggedInUser.lastRefresh = Date()
        users[loggedInUser.userId] = loggedInUser
    }

    fun refreshLobbyPlayers() {
        for (user in users.values) {
            user.refreshLobby.isRefreshPlayers = true
        }
    }

    fun refreshLobbyGameRooms() {
        for (user in users.values) {
            user.refreshLobby.isRefreshGameRooms = true
        }
    }

    fun refreshLobbyChat() {
        for (user in users.values) {
            user.refreshLobby.isRefreshChat = true
        }
    }
}
