package com.kingdom.service

import com.kingdom.model.Game
import com.kingdom.model.GameRoom
import com.kingdom.util.GameRoomComparator

import java.util.*

class GameRoomManager private constructor() {

    private val games = HashMap<Int, Game>()

    var isUpdatingWebsite: Boolean = false
    var updatingMessage: String? = null

    var isShowNews: Boolean = false
    var news = ""

    val nextAvailableGame: Game?
        get() {
            if (games.size >= MAX_GAME_ROOMS) {
                return null
            }
            var i = 1
            while (games[i] != null) {
                i++
            }

            return Game(i).apply {
                status = Game.STATUS_GAME_BEING_CONFIGURED
                games[i] = this
            }
        }

    val lobbyGameRooms: List<GameRoom>
        get() = getGameRooms(true)

    val gamesInProgress: List<GameRoom>
        get() = getGameRooms(false)

    fun getGame(gameId: Int): Game {
        return games[gameId]!!
    }

    private fun getGameRooms(lobbyGameRooms: Boolean): List<GameRoom> {
        val gameRooms = ArrayList<GameRoom>()

        val currentGames = ArrayList(games.values)

        for (game in currentGames) {
            checkLastActivity(game)

            var addGame = false
            if (lobbyGameRooms && (game.status == Game.STATUS_GAME_BEING_CONFIGURED || game.status == Game.STATUS_GAME_WAITING_FOR_PLAYERS)) {
                addGame = true
            } else if (!lobbyGameRooms && game.status == Game.STATUS_GAME_IN_PROGRESS) {
                addGame = true
            }

            if (addGame) {
                val gameRoom = GameRoom("Game Room " + game.gameId, game.gameId, game)
                gameRooms.add(gameRoom)
            }

            if (game.status == Game.STATUS_NO_GAMES) {
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
            Game.STATUS_GAME_BEING_CONFIGURED -> if (now - 15 * minute > game.lastActivity!!.time) {
                resetGame = true
            }
            Game.STATUS_GAME_WAITING_FOR_PLAYERS -> if (now - 15 * minute > game.lastActivity!!.time) {
                game.addGameChat("This game was reset due to inactivity.")
                resetGame = true
            }
            Game.STATUS_GAME_IN_PROGRESS -> if (now - 30 * minute > game.lastActivity!!.time) {
                game.addGameChat("This game was reset due to inactivity.")
                resetGame = true
            }
            Game.STATUS_GAME_FINISHED -> if (now - 2 * minute > game.lastActivity!!.time) {
                game.addGameChat("This game was reset due to inactivity.")
                resetGame = true
            }
        }

        if (resetGame) {
            if (game.status == Game.STATUS_GAME_IN_PROGRESS) {
                game.gameEndReason = "Game Abandoned"
                game.isAbandonedGame = true
                game.saveGameHistory()
            }
            game.reset()
        }
    }

    fun maxGameRoomLimitReached(): Boolean {
        return games.size >= MAX_GAME_ROOMS
    }

    companion object {

        private const val MAX_GAME_ROOMS = 20

        val instance = GameRoomManager()
    }
}
