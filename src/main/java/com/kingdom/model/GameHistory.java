package com.kingdom.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class GameHistory {

    @Id
    @Column(name = "gameid")
    private int gameId;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "num_players")
    private int numPlayers;

    @Column(name = "num_computer_players")
    private int numComputerPlayers;

    private String cards;

    private boolean custom;

    @Column(name = "game_end_reason")
    private String gameEndReason;

    @Column(name = "annotated_game")
    private boolean annotatedGame;

    @Column(name = "test_game")
    private boolean testGame;

    @Column(name = "abandoned_game")
    private boolean abandonedGame;

    private String winner = "";

    @Column(name = "show_victory_points")
    private boolean showVictoryPoints;

    @Column(name = "identical_starting_hands")
    private boolean identicalStartingHands;

    private boolean repeated;

    private boolean mobile;

    private boolean leaders;

    @Column(name = "recent_game")
    private boolean recentGame;

    @Column(name = "recommended_set")
    private boolean recommendedSet;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getNumComputerPlayers() {
        return numComputerPlayers;
    }

    public void setNumComputerPlayers(int numComputerPlayers) {
        this.numComputerPlayers = numComputerPlayers;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public String getGameEndReason() {
        return gameEndReason;
    }

    public void setGameEndReason(String gameEndReason) {
        this.gameEndReason = gameEndReason;
    }

    public boolean isAnnotatedGame() {
        return annotatedGame;
    }

    public void setAnnotatedGame(boolean annotatedGame) {
        this.annotatedGame = annotatedGame;
    }

    public boolean isTestGame() {
        return testGame;
    }

    public void setTestGame(boolean testGame) {
        this.testGame = testGame;
    }

    public boolean isAbandonedGame() {
        return abandonedGame;
    }

    public void setAbandonedGame(boolean abandonedGame) {
        this.abandonedGame = abandonedGame;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public boolean isShowVictoryPoints() {
        return showVictoryPoints;
    }

    public void setShowVictoryPoints(boolean showVictoryPoints) {
        this.showVictoryPoints = showVictoryPoints;
    }

    public boolean isIdenticalStartingHands() {
        return identicalStartingHands;
    }

    public void setIdenticalStartingHands(boolean identicalStartingHands) {
        this.identicalStartingHands = identicalStartingHands;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public boolean isLeaders() {
        return leaders;
    }

    public void setLeaders(boolean leaders) {
        this.leaders = leaders;
    }

    public boolean isRecentGame() {
        return recentGame;
    }

    public void setRecentGame(boolean recentGame) {
        this.recentGame = recentGame;
    }

    public boolean isRecommendedSet() {
        return recommendedSet;
    }

    public void setRecommendedSet(boolean recommendedSet) {
        this.recommendedSet = recommendedSet;
    }
}
