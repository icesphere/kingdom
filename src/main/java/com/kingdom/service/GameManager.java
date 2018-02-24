package com.kingdom.service;

import com.kingdom.model.*;
import com.kingdom.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class GameManager {

    private GameErrorRepository gameErrorRepository;
    private GameLogRepository gameLogRepository;
    private AnnotatedGameRepository annotatedGameRepository;
    private RecommendedSetRepository recommendedSetRepository;
    private GameHistoryRepository gameHistoryRepository;
    private GameUserHistoryRepository gameUserHistoryRepository;
    private UserRepository userRepository;

    public GameManager(GameErrorRepository gameErrorRepository,
                       GameLogRepository gameLogRepository,
                       AnnotatedGameRepository annotatedGameRepository,
                       RecommendedSetRepository recommendedSetRepository,
                       GameHistoryRepository gameHistoryRepository,
                       GameUserHistoryRepository gameUserHistoryRepository,
                       UserRepository userRepository) {
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
        //todo
        return new ArrayList<>();
        //return gameHistoryRepository.getTopGameHistoriesByUserId(userId);
    }

    public void saveGameHistory(GameHistory history) {
        gameHistoryRepository.save(history);
    }

    public void saveGameUserHistory(int gameId, Player player) {
        GameUserHistory gameUserHistory = new GameUserHistory(gameId, player);
        gameUserHistoryRepository.save(gameUserHistory);
        if (!player.isQuit()) {
            User user = userRepository.findById(player.getUserId()).get();
            if (!user.getActive()) {
                user.setActive(true);
                userRepository.save(user);
            }
        }
    }

    public List<GameUserHistory> getGamePlayersHistory(int gameId) {
        return gameUserHistoryRepository.findByGameId(gameId);
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
        gameErrorRepository.deleteById(errorId);
    }

    public GameLog getGameLog(int logId) {
        return gameLogRepository.findById(logId).get();
    }

    public GameLog getGameLogByGameId(int gameId) {
        return gameLogRepository.findByGameId(gameId);
    }

    public void saveGameLog(GameLog log) {
        gameLogRepository.save(log);
    }

    public OverallStats getOverallStats() {
        //todo
        return new OverallStats();
        //return dao.getOverallStats();
    }

    public OverallStats getOverallStatsForToday() {
        Calendar today = GregorianCalendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        //todo
        return new OverallStats();
        //return dao.getOverallStats(today.getTime(), null);
    }

    public OverallStats getOverallStatsForYesterday() {
        Calendar today = GregorianCalendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        //todo
        return new OverallStats();
        //return dao.getOverallStats(yesterday.getTime(), today.getTime());
    }

    public OverallStats getOverallStatsForPastWeek() {
        Calendar today = GregorianCalendar.getInstance();
        today.add(Calendar.WEEK_OF_YEAR, -1);

        //todo
        return new OverallStats();
        //return dao.getOverallStats(today.getTime(), null);
    }

    public OverallStats getOverallStatsForPastMonth() {
        Calendar today = GregorianCalendar.getInstance();
        today.add(Calendar.MONTH, -1);

        //todo
        return new OverallStats();
        //return dao.getOverallStats(today.getTime(), null);
    }

    public UserStats getUserStats() {
        //todo
        return new UserStats();
    }

    public void saveAnnotatedGame(AnnotatedGame game) {
        annotatedGameRepository.save(game);
    }

    public void deleteAnnotatedGame(AnnotatedGame game) {
        annotatedGameRepository.delete(game);
    }

    public AnnotatedGame getAnnotatedGame(int id) {
        return annotatedGameRepository.findById(id).get();
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
        return recommendedSetRepository.findById(id).get();
    }

    public List<RecommendedSet> getRecommendedSets() {
        return recommendedSetRepository.findAllByOrderByIdAsc();
    }
}
