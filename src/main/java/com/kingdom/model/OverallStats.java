package com.kingdom.model;

public class OverallStats {
    private int gamesAgainstComputersPlayed;
    private int gamesAgainstHumansPlayed;
    private int gamesAgainstComputersWon;

    private int gamesAgainstHardComputerPlayed;
    private int gamesWonByHardComputer;

    private int gamesAgainstBMUComputerPlayed;
    private int gamesWonByBMUComputer;

    private int gamesQuit;
    private int numUsers;
    private int newAccountsCreated;
    private int newUsersWithGamePlayed;
    private int testGames;
    private int gamesAbandoned;
    private int showVictoryPointsGames;
    private int identicalStartingHandsGames;
    private int repeatedGames;
    private int mobileGames;
    private int gamesWithLeaders;
    private int annotatedGames;
    private int recentGames;
    private int recommendedSets;

    public int getGamesPlayed() {
        return gamesAgainstComputersPlayed + gamesAgainstHumansPlayed;
    }

    public int getGamesAgainstComputersPlayed() {
        return gamesAgainstComputersPlayed;
    }

    public void setGamesAgainstComputersPlayed(int gamesAgainstComputersPlayed) {
        this.gamesAgainstComputersPlayed = gamesAgainstComputersPlayed;
    }

    public int getGamesAgainstHumansPlayed() {
        return gamesAgainstHumansPlayed;
    }

    public void setGamesAgainstHumansPlayed(int gamesAgainstHumansPlayed) {
        this.gamesAgainstHumansPlayed = gamesAgainstHumansPlayed;
    }

    public int getGamesAgainstComputersWon() {
        return gamesAgainstComputersWon;
    }

    public void setGamesAgainstComputersWon(int gamesAgainstComputersWon) {
        this.gamesAgainstComputersWon = gamesAgainstComputersWon;
    }

    public int getGamesAgainstHardComputerPlayed() {
        return gamesAgainstHardComputerPlayed;
    }

    public void setGamesAgainstHardComputerPlayed(int gamesAgainstHardComputerPlayed) {
        this.gamesAgainstHardComputerPlayed = gamesAgainstHardComputerPlayed;
    }

    public int getGamesWonByHardComputer() {
        return gamesWonByHardComputer;
    }

    public void setGamesWonByHardComputer(int gamesWonByHardComputer) {
        this.gamesWonByHardComputer = gamesWonByHardComputer;
    }

    public int getGamesAgainstBMUComputerPlayed() {
        return gamesAgainstBMUComputerPlayed;
    }

    public void setGamesAgainstBMUComputerPlayed(int gamesAgainstBMUComputerPlayed) {
        this.gamesAgainstBMUComputerPlayed = gamesAgainstBMUComputerPlayed;
    }

    public int getGamesWonByBMUComputer() {
        return gamesWonByBMUComputer;
    }

    public void setGamesWonByBMUComputer(int gamesWonByBMUComputer) {
        this.gamesWonByBMUComputer = gamesWonByBMUComputer;
    }

    public int getGamesQuit() {
        return gamesQuit;
    }

    public void setGamesQuit(int gamesQuit) {
        this.gamesQuit = gamesQuit;
    }

    public int getNumUsers() {
        return numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public int getNewAccountsCreated() {
        return newAccountsCreated;
    }

    public void setNewAccountsCreated(int newAccountsCreated) {
        this.newAccountsCreated = newAccountsCreated;
    }

    public int getTestGames() {
        return testGames;
    }

    public void setTestGames(int testGames) {
        this.testGames = testGames;
    }

    public int getNewUsersWithGamePlayed() {
        return newUsersWithGamePlayed;
    }

    public void setNewUsersWithGamePlayed(int newUsersWithGamePlayed) {
        this.newUsersWithGamePlayed = newUsersWithGamePlayed;
    }

    public int getGamesAbandoned() {
        return gamesAbandoned;
    }

    public void setGamesAbandoned(int gamesAbandoned) {
        this.gamesAbandoned = gamesAbandoned;
    }

    public int getShowVictoryPointsGames() {
        return showVictoryPointsGames;
    }

    public void setShowVictoryPointsGames(int showVictoryPointsGames) {
        this.showVictoryPointsGames = showVictoryPointsGames;
    }

    public int getIdenticalStartingHandsGames() {
        return identicalStartingHandsGames;
    }

    public void setIdenticalStartingHandsGames(int identicalStartingHandsGames) {
        this.identicalStartingHandsGames = identicalStartingHandsGames;
    }

    public int getRepeatedGames() {
        return repeatedGames;
    }

    public void setRepeatedGames(int repeatedGames) {
        this.repeatedGames = repeatedGames;
    }

    public int getMobileGames() {
        return mobileGames;
    }

    public void setMobileGames(int mobileGames) {
        this.mobileGames = mobileGames;
    }

    public int getGamesWithLeaders() {
        return gamesWithLeaders;
    }

    public void setGamesWithLeaders(int gamesWithLeaders) {
        this.gamesWithLeaders = gamesWithLeaders;
    }

    public int getAnnotatedGames() {
        return annotatedGames;
    }

    public void setAnnotatedGames(int annotatedGames) {
        this.annotatedGames = annotatedGames;
    }

    public int getRecentGames() {
        return recentGames;
    }

    public void setRecentGames(int recentGames) {
        this.recentGames = recentGames;
    }

    public int getRecommendedSets() {
        return recommendedSets;
    }

    public void setRecommendedSets(int recommendedSets) {
        this.recommendedSets = recommendedSets;
    }
}
