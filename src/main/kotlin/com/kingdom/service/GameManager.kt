package com.kingdom.service

import com.kingdom.model.*
import com.kingdom.model.players.Player
import com.kingdom.repository.*
import org.springframework.stereotype.Service

import java.util.ArrayList
import java.util.Calendar
import java.util.GregorianCalendar

@Service
class GameManager(private val gameErrorRepository: GameErrorRepository,
                  private val gameLogRepository: GameLogRepository,
                  private val annotatedGameRepository: AnnotatedGameRepository,
                  private val recommendedSetRepository: RecommendedSetRepository,
                  private val gameHistoryRepository: GameHistoryRepository,
                  private val gameUserHistoryRepository: GameUserHistoryRepository,
                  private val userRepository: UserRepository) {

    val gameHistoryList: List<GameHistory>
        get() = gameHistoryRepository.findTop80ByOrderByGameIdDesc()

    val gameErrors: List<GameError>
        get() = gameErrorRepository.findTop50ByOrderByErrorIdDesc()

    //todo
    //return dao.getOverallStats();
    val overallStats: OverallStats
        get() = OverallStats()

    //todo
    //return dao.getOverallStats(today.getTime(), null);
    val overallStatsForToday: OverallStats
        get() {
            val today = GregorianCalendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            return OverallStats()
        }

    //todo
    //return dao.getOverallStats(yesterday.getTime(), today.getTime());
    val overallStatsForYesterday: OverallStats
        get() {
            val today = GregorianCalendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)

            val yesterday = today.clone() as Calendar
            yesterday.add(Calendar.DAY_OF_MONTH, -1)
            return OverallStats()
        }

    //todo
    //return dao.getOverallStats(today.getTime(), null);
    val overallStatsForPastWeek: OverallStats
        get() {
            val today = GregorianCalendar.getInstance()
            today.add(Calendar.WEEK_OF_YEAR, -1)
            return OverallStats()
        }

    //todo
    //return dao.getOverallStats(today.getTime(), null);
    val overallStatsForPastMonth: OverallStats
        get() {
            val today = GregorianCalendar.getInstance()
            today.add(Calendar.MONTH, -1)
            return OverallStats()
        }

    //todo
    val userStats: UserStats
        get() = UserStats()

    val annotatedGames: List<AnnotatedGame>
        get() = annotatedGameRepository.findAllByOrderByGameIdDesc()

    val recommendedSets: List<RecommendedSet>
        get() = recommendedSetRepository.findAllByOrderByIdAsc()

    fun getGameHistoryList(userId: Int): List<GameHistory> {
        //todo
        return ArrayList()
        //return gameHistoryRepository.getTopGameHistoriesByUserId(userId);
    }

    fun saveGameHistory(history: GameHistory) {
        gameHistoryRepository.save(history)
    }

    fun saveGameUserHistory(gameId: String, player: Player) {
        val gameUserHistory = GameUserHistory(gameId, player)
        gameUserHistoryRepository.save(gameUserHistory)
        if (!player.isQuit) {
            val user = userRepository.findById(player.userId).get()
            if (!user.active) {
                user.active = true
                userRepository.save(user)
            }
        }
    }

    fun getGamePlayersHistory(gameId: String): List<GameUserHistory> {
        return gameUserHistoryRepository.findByGameId(gameId)
    }

    fun logError(error: GameError) {
        if (error.error!!.length > 20000) {
            error.error = error.error!!.substring(0, 19990) + "..."
        }

        gameErrorRepository.save(error)
    }

    fun deleteGameError(errorId: Int) {
        gameErrorRepository.deleteById(errorId)
    }

    fun getGameLog(logId: Int): GameLog {
        return gameLogRepository.findById(logId).get()
    }

    fun getGameLogByGameId(gameId: String): GameLog {
        return gameLogRepository.findByGameId(gameId)
    }

    fun saveGameLog(log: GameLog) {
        gameLogRepository.save(log)
    }

    fun saveAnnotatedGame(game: AnnotatedGame) {
        annotatedGameRepository.save(game)
    }

    fun deleteAnnotatedGame(game: AnnotatedGame) {
        annotatedGameRepository.delete(game)
    }

    fun getAnnotatedGame(id: Int): AnnotatedGame {
        return annotatedGameRepository.findById(id).get()
    }

    fun saveRecommendedSet(set: RecommendedSet) {
        recommendedSetRepository.save(set)
    }

    fun deleteRecommendedSet(set: RecommendedSet) {
        recommendedSetRepository.delete(set)
    }

    fun getRecommendedSet(id: Int): RecommendedSet {
        return recommendedSetRepository.findById(id).get()
    }
}
