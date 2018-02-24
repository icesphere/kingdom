package com.kingdom.model;

public class PlayerStats {

    private int gamesWon;
    private int gamesLost;
    private int gamesQuit;
    private double averageMarginOfVictory;

    private int gamesAgainstComputerWon;
    private int gamesAgainstComputerLost;
    private int gamesAgainstComputerQuit;
    private int testGames;
    private double averageMarginOfVictoryAgainstComputer;

    public int getGamesPlayed() {
        return gamesWon + gamesLost + gamesQuit;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
    }

    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }

    public int getGamesQuit() {
        return gamesQuit;
    }

    public void setGamesQuit(int gamesQuit) {
        this.gamesQuit = gamesQuit;
    }

    public double getAverageMarginOfVictory() {
        return averageMarginOfVictory;
    }

    public void setAverageMarginOfVictory(double averageMarginOfVictory) {
        this.averageMarginOfVictory = averageMarginOfVictory;
    }

    public int getGamesAgainstComputerPlayed() {
        return gamesAgainstComputerWon + gamesAgainstComputerLost + gamesAgainstComputerQuit;
    }

    public int getGamesAgainstComputerWon() {
        return gamesAgainstComputerWon;
    }

    public void setGamesAgainstComputerWon(int gamesAgainstComputerWon) {
        this.gamesAgainstComputerWon = gamesAgainstComputerWon;
    }

    public int getGamesAgainstComputerLost() {
        return gamesAgainstComputerLost;
    }

    public void setGamesAgainstComputerLost(int gamesAgainstComputerLost) {
        this.gamesAgainstComputerLost = gamesAgainstComputerLost;
    }

    public int getGamesAgainstComputerQuit() {
        return gamesAgainstComputerQuit;
    }

    public void setGamesAgainstComputerQuit(int gamesAgainstComputerQuit) {
        this.gamesAgainstComputerQuit = gamesAgainstComputerQuit;
    }

    public double getAverageMarginOfVictoryAgainstComputer() {
        return averageMarginOfVictoryAgainstComputer;
    }

    public void setAverageMarginOfVictoryAgainstComputer(double averageMarginOfVictoryAgainstComputer) {
        this.averageMarginOfVictoryAgainstComputer = averageMarginOfVictoryAgainstComputer;
    }

    public int getTestGames() {
        return testGames;
    }

    public void setTestGames(int testGames) {
        this.testGames = testGames;
    }
}
