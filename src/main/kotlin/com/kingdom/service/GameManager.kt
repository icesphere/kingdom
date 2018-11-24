package com.kingdom.service

import com.kingdom.model.*
import com.kingdom.model.players.Player
import com.kingdom.repository.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameManager(private val gameLogRepository: GameLogRepository,
                  private val gameUserHistoryRepository: GameUserHistoryRepository,
                  private val userRepository: UserRepository) {

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
        //todo show error somewhere
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
}
