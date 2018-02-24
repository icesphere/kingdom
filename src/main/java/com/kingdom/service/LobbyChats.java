package com.kingdom.service;

import com.kingdom.model.LobbyChat;
import com.kingdom.model.User;

import java.util.Date;
import java.util.LinkedList;

public class LobbyChats {

    public static final int MAX_LOBBY_CHATS = 50;

    private static LobbyChats ourInstance = new LobbyChats();

    private LinkedList<LobbyChat> chats = new LinkedList<LobbyChat>();

    public static LobbyChats getInstance() {
        return ourInstance;
    }

    public LinkedList<LobbyChat> getChats() {
        return chats;
    }

    public void addChat(User user, String message) {
        if (chats.size() == MAX_LOBBY_CHATS) {
            chats.removeLast();
        }
        LobbyChat chat = new LobbyChat();
        chat.setUsername(user.getUsername());
        chat.setMessage(message);
        chat.setTime(new Date());
        chats.addFirst(chat);
    }

    public void addPrivateChat(User sender, User receiver, String message) {
        if (chats.size() == MAX_LOBBY_CHATS) {
            chats.removeLast();
        }
        LobbyChat chat = new LobbyChat();
        chat.setUsername(sender.getUsername());
        chat.setMessage(message);
        chat.setTime(new Date());
        chat.setUserId(receiver.getUserId());
        chats.addFirst(chat);
    }
}
