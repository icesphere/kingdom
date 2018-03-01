package com.kingdom.service

import com.kingdom.model.User

import java.util.*

class LoggedInUsers {

    fun userLoggedIn(user: User) {
        updateUser(user, true, true)
    }

    fun userLoggedOut(user: User) {
        users.remove(user.userId)
    }

    fun gameReset(userId: Int) {
        val user = users[userId]
        if (user != null) {
            user.gameId = 0
            user.lastActivity = Date()
        }
    }

    fun getUsers(): List<User> {
        return ArrayList(users.values)
    }

    fun getUser(userId: Int): User? {
        return users[userId]
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

    companion object {
        private val users = HashMap<Int, User>()

        val instance = LoggedInUsers()
    }
}
