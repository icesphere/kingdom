package com.kingdom.service

import com.kingdom.model.Game
import com.kingdom.model.GameRoom
import com.kingdom.model.GameStatus
import com.kingdom.util.GameRoomComparator
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameRoomManager(val gameManager: GameManager, val gameMessageService: GameMessageService) {

    private val games = HashMap<String, Game>()

    var isUpdatingWebsite: Boolean = false
    var updatingMessage: String? = null

    var isShowNews: Boolean = false
    var news = ""

    val nextAvailableGame: Game?
        get() {
            if (games.size >= MAX_GAME_ROOMS) {
                return null
            }

            return Game(gameManager, gameMessageService).apply {
                status = GameStatus.BeingConfigured
                games[gameId] = this
            }
        }

    val lobbyGameRooms: List<GameRoom>
        get() = getGameRooms(true)

    val gamesInProgress: List<GameRoom>
        get() = getGameRooms(false)

    fun getGame(gameId: String): Game? {
        return games[gameId]
    }

    private fun getGameRooms(lobbyGameRooms: Boolean): List<GameRoom> {
        val gameRooms = ArrayList<GameRoom>()

        val currentGames = ArrayList(games.values)

        for ((index, game) in currentGames.withIndex()) {
            checkLastActivity(game)

            var addGame = false
            if (lobbyGameRooms && (game.status == GameStatus.BeingConfigured || game.status == GameStatus.WaitingForPlayers)) {
                addGame = true
            } else if (!lobbyGameRooms && game.status == GameStatus.InProgress) {
                addGame = true
            }

            if (addGame) {
                val gameRoom = GameRoom("Game Room " + index + 1, game.gameId, game)
                gameRooms.add(gameRoom)
            }

            if (game.status == GameStatus.None) {
                games.remove(game.gameId)
            }
        }

        val grc = GameRoomComparator()
        gameRooms.sortWith(grc)

        return gameRooms
    }

    private fun checkLastActivity(game: Game) {
        val minute = 60000

        val now = System.currentTimeMillis()

        var resetGame = false

        when(game.status) {
            GameStatus.BeingConfigured -> if (now - 15 * minute > game.lastActivity.time) {
                resetGame = true
            }
            GameStatus.WaitingForPlayers -> if (now - 15 * minute > game.lastActivity.time) {
                game.addGameChat("This game was reset due to inactivity.")
                resetGame = true
            }
            GameStatus.InProgress -> if (now - 30 * minute > game.lastActivity.time) {
                game.addGameChat("This game was reset due to inactivity.")
                resetGame = true
            }
            GameStatus.Finished -> if (now - 2 * minute > game.lastActivity.time) {
                game.addGameChat("This game was reset due to inactivity.")
                resetGame = true
            }
            else -> {}
        }

        if (resetGame) {
            if (game.status == GameStatus.InProgress) {
                game.gameEndReason = "Game Abandoned"
                game.isAbandonedGame = true
            }
            game.reset()
        }
    }

    fun maxGameRoomLimitReached(): Boolean {
        return games.size >= MAX_GAME_ROOMS
    }

    companion object {

        private const val MAX_GAME_ROOMS = 20
    }
}
