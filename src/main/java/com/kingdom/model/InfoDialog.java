package com.kingdom.model;

import java.util.ArrayList;
import java.util.List;

public class InfoDialog {
    private String message;
    private List<Card> cards = new ArrayList<Card>();
    private String hideMethod;
    private int width;
    private int timeout;
    private int messageFontSize = 12;
    private String height = "auto";
    private String messageAlign = "left";
    private boolean error;

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHideMethod() {
        return hideMethod;
    }

    public void setHideMethod(String hideMethod) {
        this.hideMethod = hideMethod;
    }

    public int getWidth() {
        if (width == 0) {
            return 300;
        }
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMessageFontSize() {
        return messageFontSize;
    }

    public void setMessageFontSize(int messageFontSize) {
        this.messageFontSize = messageFontSize;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getMessageAlign() {
        return messageAlign;
    }

    public void setMessageAlign(String messageAlign) {
        this.messageAlign = messageAlign;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public static InfoDialog getYourTurnInfoDialog() {
        InfoDialog dialog = new InfoDialog();
        dialog.setMessage("Your Turn");
        dialog.setHideMethod("puff");
        dialog.setWidth(200);
        dialog.setTimeout(1000);
        dialog.setMessageFontSize(20);
        dialog.setHeight("110");
        dialog.setMessageAlign("center");
        return dialog;
    }

    public static InfoDialog getErrorDialog(String message) {
        return getInfoDialog(message, true);
    }

    public static InfoDialog getInfoDialog(String message) {
        return getInfoDialog(message, false);
    }

    private static InfoDialog getInfoDialog(String message, boolean error) {
        InfoDialog dialog = new InfoDialog();
        dialog.setMessage(message);
        dialog.setHideMethod("slide");
        dialog.setWidth(400);
        dialog.setTimeout(1500);
        dialog.setMessageFontSize(16);
        dialog.setMessageAlign("center");
        dialog.setError(error);
        return dialog;
    }
}
