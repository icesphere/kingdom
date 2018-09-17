package com.kingdom.service

import com.kingdom.model.Game
import com.kingdom.model.GameStatus
import com.kingdom.model.InfoDialog
import com.kingdom.model.players.Player
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class RefreshGameManager(private val messagingTemplate: SimpMessagingTemplate) {

    fun refreshGame(game: Game) {
        game.players.forEach { refreshPlayer(it) }
    }

    fun refreshPlayer(player: Player) {
        if (!player.isBot) {

            val game = player.game

            val data = getRefreshGameData(player)

            data.infoDialog = player.infoDialog

            data.title = when {
                game.status == GameStatus.Finished -> "Game Over"
                player.isYourTurn -> "Your Turn"
                else -> game.currentPlayer.username + "'s Turn"
            }

            messagingTemplate.convertAndSend("/queue/refresh-game/" + player.userId, data)
        }
    }

    fun refreshChat(game: Game) {
        game.players.forEach { refreshPlayerChat(it) }
    }

    fun refreshPlayerChat(player: Player) {
        //todo
    }

    fun getRefreshGameData(player: Player): RefreshGameData {
        return RefreshGameData(player.game.status, player.isYourTurn)
    }
}

class RefreshGameData(val gameStatus: GameStatus,
                      val isCurrentPlayer: Boolean) {

    var infoDialog: InfoDialog? = null
    var title: String? = null
}