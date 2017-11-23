package com.kingdom.model;

import com.kingdom.util.KingdomUtil;

import javax.persistence.*;

@Entity
public class GameUserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "gameuserid")
    private int gameUserId;

    private int gameId;

    private int userId;

    private int points;

    private boolean winner;

    private boolean quit;

    private String cards = "";

    @Column(name = "victory_coins")
    private int victoryCoins;

    private int sins;

    private int turns;

    @Column(name = "margin_of_victory")
    private int marginOfVictory;

    private transient String username = "";

    public GameUserHistory() {
    }

    public GameUserHistory(int gameId, Player player) {
        this.gameId = gameId;
        this.userId = player.getUserId();
        this.points = player.getVictoryPoints();
        this.winner = player.isWinner();
        this.quit = player.isQuit();
        this.cards = KingdomUtil.groupCards(player.getAllCards(), false);
        this.victoryCoins = player.getVictoryCoins();
        this.sins = player.getSins();
        this.turns = player.getTurns();
        this.marginOfVictory = player.getMarginOfVictory();
    }

    public int getGameUserId() {
        return gameUserId;
    }

    public void setGameUserId(int gameUserId) {
        this.gameUserId = gameUserId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public boolean isQuit() {
        return quit;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public int getVictoryCoins() {
        return victoryCoins;
    }

    public void setVictoryCoins(int victoryCoins) {
        this.victoryCoins = victoryCoins;
    }

    public int getSins() {
        return sins;
    }

    public void setSins(int sins) {
        this.sins = sins;
    }

    public int getTurns() {
        return turns;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public int getMarginOfVictory() {
        return marginOfVictory;
    }

    public void setMarginOfVictory(int marginOfVictory) {
        this.marginOfVictory = marginOfVictory;
    }
}
