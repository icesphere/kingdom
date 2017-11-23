package com.kingdom.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/3/11
 * Time: 8:16 PM
 */
public class PlayerTurn {
    private int userId;
    private String username = "";
    private List<String> history = new ArrayList<String>();

    public PlayerTurn(Player player) {
        this.userId = player.getUserId();
        this.username = player.getUsername();
        StringBuilder sb = new StringBuilder();
        sb.append("<span style='font-weight:bold'>");
        sb.append(username).append("'s Turn ").append(player.getTurns() + 1);
        history.add(sb.toString());
    }

    public void addHistory(String message) {
        history.add(message);
    }

    public List<String> getHistory() {
        return history;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
