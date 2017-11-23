package com.kingdom.service;

import com.kingdom.model.*;
import com.kingdom.repository.GameDao;

import java.util.List;

public class GameManager {

    GameDao dao = new GameDao();

    public GameManager() {
    }

    public List<GameHistory> getGameHistoryList() {
        return dao.getGameHistoryList();
    }

    public List<GameHistory> getGameHistoryList(int userId) {
        return dao.getGameHistoryList(userId);
    }

    public List<GameHistory> getGameHistoryList(int userId, int limit) {
        return dao.getGameHistoryList(userId, limit);
    }

    public void saveGameHistory(GameHistory history) {
        dao.saveGameHistory(history);        
    }

    public void saveGameUserHistory(int gameId, Player player) {
        dao.saveGameUserHistory(gameId, player);
    }

    public List<GameUserHistory> getGamePlayersHistory(int gameId) {
        return dao.getGamePlayersHistory(gameId);
    }

    public void logError(GameError error) {
        dao.logError(error);
    }

    public void setGameDao(GameDao dao) {
        this.dao = dao;
    }

    public List<GameError> getGameErrors() {
        return dao.getGameErrors();    
    }

    public void deleteGameError(int errorId) {
        dao.deleteGameError(errorId);
    }

    public GameLog getGameLog(int logId) {
        return dao.getGameLog(logId);    
    }

    public GameLog getGameLogByGameId(int gameId) {
        return dao.getGameLogByGameId(gameId);
    }

    public void saveGameLog(GameLog log) {
       dao.saveGameLog(log);
    }

    public OverallStats getOverallStats() {
        return dao.getOverallStats();
    }

    public OverallStats getOverallStatsForToday() {
        return dao.getOverallStatsForToday();
    }

    public OverallStats getOverallStatsForYesterday() {
        return dao.getOverallStatsForYesterday();
    }

    public OverallStats getOverallStatsForPastWeek() {
        return dao.getOverallStatsForPastWeek();
    }

    public OverallStats getOverallStatsForPastMonth() {
        return dao.getOverallStatsForPastMonth();
    }

    public UserStats getUserStats() {
        return dao.getUserStats();
    }

    public void saveAnnotatedGame(AnnotatedGame game) {
        dao.saveAnnotatedGame(game);
    }

    public void deleteAnnotatedGame(AnnotatedGame game) {
        dao.deleteAnnotatedGame(game);
    }

    public AnnotatedGame getAnnotatedGame(int id) {
        return dao.getAnnotatedGame(id);
    }

    public List<AnnotatedGame> getAnnotatedGames() {
        return dao.getAnnotatedGames();
    }  

    public void saveRecommendedSet(RecommendedSet set) {
        dao.saveRecommendedSet(set);
    }

    public void deleteRecommendedSet(RecommendedSet set) {
        dao.deleteRecommendedSet(set);
    }

    public RecommendedSet getRecommendedSet(int id) {
        return dao.getRecommendedSet(id);
    }

    public List<RecommendedSet> getRecommendedSets() {
        return dao.getRecommendedSets();
    }
}
