package com.kingdom.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "annotated_games")
@Entity
public class AnnotatedGame {

    @Id
    @Column(name = "gameid")
    private int gameId;

    private String title = "";

    private String cards = "";

    @Column(name = "include_colony_and_platinum")
    private boolean includeColonyAndPlatinum;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public boolean isIncludeColonyAndPlatinum() {
        return includeColonyAndPlatinum;
    }

    public void setIncludeColonyAndPlatinum(boolean includeColonyAndPlatinum) {
        this.includeColonyAndPlatinum = includeColonyAndPlatinum;
    }
}
