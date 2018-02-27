package com.kingdom.service

import com.kingdom.model.LobbyChat
import com.kingdom.model.User

import java.util.Date
import java.util.LinkedList

class LobbyChats {

    val chats = LinkedList<LobbyChat>()

    fun addChat(user: User, message: String) {
        if (chats.size == MAX_LOBBY_CHATS) {
            chats.removeLast()
        }
        val chat = LobbyChat(user.username, message, Date())
        chats.addFirst(chat)
    }

    fun addPrivateChat(sender: User, receiver: User, message: String) {
        if (chats.size == MAX_LOBBY_CHATS) {
            chats.removeLast()
        }
        val chat = LobbyChat(sender.username, message, Date())
        chat.userId = receiver.userId
        chats.addFirst(chat)
    }

    companion object {

        const val MAX_LOBBY_CHATS = 50

        val instance = LobbyChats()
    }
}
