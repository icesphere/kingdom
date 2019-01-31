package com.kingdom.service

import com.kingdom.model.LobbyChat
import com.kingdom.model.User
import org.springframework.stereotype.Service

import java.util.Date
import java.util.LinkedList

@Service
class LobbyChats {

    val chats = LinkedList<LobbyChat>()

    fun addChat(user: User, message: String) {
        if (chats.size == MAX_LOBBY_CHATS) {
            chats.removeFirst()
        }
        val chat = LobbyChat(user.username, message, Date())
        chats.add(chat)
    }

    fun addPrivateChat(sender: User, receiver: User, message: String) {
        if (chats.size == MAX_LOBBY_CHATS) {
            chats.removeFirst()
        }
        val chat = LobbyChat(sender.username, message, Date())
        chat.userId = receiver.userId
        chats.add(chat)
    }

    companion object {

        const val MAX_LOBBY_CHATS = 50
    }
}
