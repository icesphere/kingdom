package com.kingdom.model;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/14/11
 * Time: 6:31 AM
 */
public class RefreshLobby {
    private boolean refreshPlayers;
    private boolean refreshGameRooms;
    private boolean refreshChat;
    private boolean startGame;
    private boolean redirectToLogin;

    public boolean isRefreshPlayers() {
        return refreshPlayers;
    }

    public void setRefreshPlayers(boolean refreshPlayers) {
        this.refreshPlayers = refreshPlayers;
    }

    public boolean isRefreshGameRooms() {
        return refreshGameRooms;
    }

    public void setRefreshGameRooms(boolean refreshGameRooms) {
        this.refreshGameRooms = refreshGameRooms;
    }

    public boolean isRefreshChat() {
        return refreshChat;
    }

    public void setRefreshChat(boolean refreshChat) {
        this.refreshChat = refreshChat;
    }

    public boolean isStartGame() {
        return startGame;
    }

    public void setStartGame(boolean startGame) {
        this.startGame = startGame;
    }

    public boolean isRedirectToLogin() {
        return redirectToLogin;
    }

    public void setRedirectToLogin(boolean redirectToLogin) {
        this.redirectToLogin = redirectToLogin;
    }
}
