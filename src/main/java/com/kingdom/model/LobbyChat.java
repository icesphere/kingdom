package com.kingdom.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: John
 * Date: Oct 23, 2010
 * Time: 10:01:11 AM
 */
public class LobbyChat {
    private String username;
    private String message;
    private Date time;
    private int userId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getChat() {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("<span class='lobbyChat'>");
        sb.append(sdf.format(time));
        if (userId > 0) {
            sb.append(" Private message from");
        }
        sb.append(" ").append(username).append(": ");
        sb.append("</span>");
        sb.append(message);
        return sb.toString();
    }

    public boolean isExpired() {
        long minute = 60 * 1000;
        return time.getTime() + (15 * minute) < System.currentTimeMillis();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
