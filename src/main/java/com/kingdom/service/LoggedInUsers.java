package com.kingdom.service;

import com.kingdom.model.User;

import java.util.*;

public class LoggedInUsers {
    private static Map<Integer, User> users = new HashMap<Integer, User>();

    private static LoggedInUsers ourInstance = new LoggedInUsers();

    public static LoggedInUsers getInstance() {
        return ourInstance;
    }

    public void userLoggedIn(User user) {
        updateUser(user, true, true);
    }

    public void userLoggedOut(User user) {
        users.remove(user.getUserId());
    }

    public void gameReset(int userId) {
        User user = users.get(userId);
        if (user != null) {
            user.setGameId(0);
            user.setLastActivity(new Date());
        }
    }

    public List<User> getUsers() {
        return new ArrayList<User>(users.values());
    }

    public User getUser(int userId) {
        return users.get(userId);
    }

    public void updateUser(User user) {
        updateUser(user, true, false);
    }

    public void refreshLobby(User user) {
        updateUser(user, false, false);
    }

    public void updateUserStatus(User user) {
        updateUser(user, true, true);
    }

    private void updateUser(User user, boolean refreshLastActivity, boolean updateStatus) {
        User loggedInUser = users.get(user.getUserId());
        if (loggedInUser == null) {
            loggedInUser = user;
        }
        loggedInUser.setGameId(user.getGameId());
        if (updateStatus) {
            loggedInUser.setStatus(user.getStatus());
        }
        if (refreshLastActivity) {
            loggedInUser.setLastActivity(new Date());
        }
        loggedInUser.setLastRefresh(new Date());
        users.put(loggedInUser.getUserId(), loggedInUser);
    }

    public void refreshLobbyPlayers() {
        for (User user : users.values()) {
            user.getRefreshLobby().setRefreshPlayers(true);
        }
    }

    public void refreshLobbyGameRooms() {
        for (User user : users.values()) {
            user.getRefreshLobby().setRefreshGameRooms(true);
        }
    }

    public void refreshLobbyChat() {
        for (User user : users.values()) {
            user.getRefreshLobby().setRefreshChat(true);
        }
    }
}
