package com.kingdom.model;

public class ChatMessage {
    private String message;
    private String color;
    private int userId;

    public ChatMessage(String message, String color) {
        this.message = message;
        this.color = color;
    }

    public ChatMessage(String message, String color, int userId) {
        this.message = message;
        this.color = color;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
