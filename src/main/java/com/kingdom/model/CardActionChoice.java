package com.kingdom.model;

/**
 * Created by IntelliJ IDEA.
 * User: John
 * Date: May 26, 2010
 * Time: 9:43:07 PM
 */
public class CardActionChoice {
    private String button;
    private String value;

    public CardActionChoice(String button, String value) {
        this.button = button;
        this.value = value;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
