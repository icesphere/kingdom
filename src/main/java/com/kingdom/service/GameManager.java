package com.kingdom.service;

import com.kingdom.model.*;
import com.kingdom.repository.*;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class GameManager {

    private GameDao dao;
    private GameErrorRepository gameErrorRepository;
    private GameLogRepository gameLogRepository;
    private AnnotatedGameRepository annotatedGameRepository;
    private RecommendedSetRepository recommendedSetRepository;
    private GameHistoryRepository gameHistoryRepository;
    private GameUserHistoryRepository gameUserHistoryRepository;
    private UserRepository userRepository;

    public GameManager(GameDao dao,
                       GameErrorRepository gameErrorRepository,
                       GameLogRepository gameLogRepository,
                       AnnotatedGameRepository annotatedGameRepository,
                       RecommendedSetRepository recommendedSetRepository,
                       GameHistoryRepository gameHistoryRepository,
                       GameUserHistoryRepository gameUserHistoryRepository,
                       UserRepository userRepository) {
        this.dao = dao;
        this.gameErrorRepository = gameErrorRepository;
        this.gameLogRepository = gameLogRepository;
        this.annotatedGameRepository = annotatedGameRepository;
        this.recommendedSetRepository = recommendedSetRepository;
        this.gameHistoryRepository = gameHistoryRepository;
        this.gameUserHistoryRepository = gameUserHistoryRepository;
        this.userRepository = userRepository;
    }

    public List<GameHistory> getGameHistoryList() {
        return gameHistoryRepository.findTop80ByOrderByGameIdDesc();
    }

    public List<GameHistory> getGameHistoryList(int userId) {
        return dao.getGameHistoryList(userId);
    }

    public List<GameHistory> getGameHistoryList(int userId, int limit) {
        return dao.getGameHistoryList(userId, limit);
    }

    public void saveGameHistory(GameHistory history) {
        gameHistoryRepository.save(history);
    }

    public void saveGameUserHistory(int gameId, Player player) {
        GameUserHistory gameUserHistory = new GameUserHistory(gameId, player);
        gameUserHistoryRepository.save(gameUserHistory);
        if (!player.isQuit()) {
            User user = userRepository.findOne(player.getUserId());
            if (!user.isActive()) {
                user.setActive(true);
                userRepository.save(user);
            }
        }
    }

    public List<GameUserHistory> getGamePlayersHistory(int gameId) {
        return dao.getGamePlayersHistory(gameId);
    }

    public void logError(GameError error) {
        if (error.getError().length() > 20000) {
            error.setError(error.getError().substring(0, 19990) + "...");
        }

        gameErrorRepository.save(error);
    }

    public List<GameError> getGameErrors() {
        return gameErrorRepository.findTop50ByOrderByErrorIdDesc();
    }

    public void deleteGameError(int errorId) {
        gameErrorRepository.delete(errorId);
    }

    public GameLog getGameLog(int logId) {
        return gameLogRepository.findOne(logId);
    }

    public GameLog getGameLogByGameId(int gameId) {
        return gameLogRepository.findByGameId(gameId);
    }

    public void saveGameLog(GameLog log) {
        gameLogRepository.save(log);
    }

    public OverallStats getOverallStats() {
        return dao.getOverallStats();
    }

    public OverallStats getOverallStatsForToday() {
        Calendar today = GregorianCalendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        return dao.getOverallStats(today.getTime(), null);
    }

    public OverallStats getOverallStatsForYesterday() {
        Calendar today = GregorianCalendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        return dao.getOverallStats(yesterday.getTime(), today.getTime());
    }

    public OverallStats getOverallStatsForPastWeek() {
        Calendar today = GregorianCalendar.getInstance();
        today.add(Calendar.WEEK_OF_YEAR, -1);

        return dao.getOverallStats(today.getTime(), null);
    }

    public OverallStats getOverallStatsForPastMonth() {
        Calendar today = GregorianCalendar.getInstance();
        today.add(Calendar.MONTH, -1);

        return dao.getOverallStats(today.getTime(), null);
    }

    public UserStats getUserStats() {
        return dao.getUserStats();
    }

    public void saveAnnotatedGame(AnnotatedGame game) {
        annotatedGameRepository.save(game);
    }

    public void deleteAnnotatedGame(AnnotatedGame game) {
        annotatedGameRepository.delete(game);
    }

    public AnnotatedGame getAnnotatedGame(int id) {
        return annotatedGameRepository.findOne(id);
    }

    public List<AnnotatedGame> getAnnotatedGames() {
        return annotatedGameRepository.findAllByOrderByGameIdDesc();
    }

    public void saveRecommendedSet(RecommendedSet set) {
        recommendedSetRepository.save(set);
    }

    public void deleteRecommendedSet(RecommendedSet set) {
        recommendedSetRepository.delete(set);
    }

    public RecommendedSet getRecommendedSet(int id) {
        return recommendedSetRepository.findOne(id);
    }

    public List<RecommendedSet> getRecommendedSets() {
        return recommendedSetRepository.findAllByOrderByIdAsc();
    }
}
