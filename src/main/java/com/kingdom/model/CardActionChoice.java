package com.kingdom.model;

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
