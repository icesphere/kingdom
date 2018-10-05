package com.kingdom.service

import com.kingdom.model.Game
import com.kingdom.model.GameStatus
import com.kingdom.model.InfoDialog
import com.kingdom.model.players.Player
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

private const val REFRESH_GAME_QUEUE = "refresh-game"
private const val REFRESH_HAND_AREA = "refresh-hand-area"
private const val REFRESH_CARDS_PLAYED = "refresh-cards-played"
private const val REFRESH_CARDS_BOUGHT = "refresh-cards-bought"
private const val REFRESH_SUPPLY = "refresh-supply"
private const val REFRESH_CARD_ACTION = "refresh-card-action"
private const val REFRESH_CHAT = "refresh-chat"

//todo refresh by section

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

            messagingTemplate.convertAndSend("/queue/$REFRESH_GAME_QUEUE/" + player.userId, data)
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