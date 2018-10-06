package com.kingdom.service

import com.kingdom.model.Game
import com.kingdom.model.GameStatus
import com.kingdom.model.InfoDialog
import com.kingdom.model.players.Player
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

private const val REFRESH_GAME_QUEUE = "refresh-game"
private const val REFRESH_HAND_AREA_QUEUE = "refresh-hand-area"
private const val REFRESH_CARDS_PLAYED_QUEUE = "refresh-cards-played"
private const val REFRESH_CARDS_BOUGHT_QUEUE = "refresh-cards-bought"
private const val REFRESH_SUPPLY_QUEUE = "refresh-supply"
private const val REFRESH_CARD_ACTION_QUEUE = "refresh-card-action"
private const val REFRESH_CHAT_QUEUE = "refresh-chat"
private const val REFRESH_HISTORY_QUEUE = "refresh-history"

//todo refresh info dialog

@Service
class RefreshGameManager(private val messagingTemplate: SimpMessagingTemplate) {

    fun refreshGame(game: Game) {
        game.humanPlayers.forEach { refreshPlayerGame(it) }
    }

    fun refreshHandArea(player: Player) {
        refreshPlayerQueue(REFRESH_HAND_AREA_QUEUE, player)
    }

    fun refreshCardsPlayed(game: Game) {
        game.humanPlayers.forEach { refreshPlayerQueue(REFRESH_CARDS_PLAYED_QUEUE, it) }
        if (game.currentPlayer.isBot) {
            Thread.sleep(1000)
        }
    }

    fun refreshCardsBought(game: Game) {
        game.humanPlayers.forEach { refreshPlayerQueue(REFRESH_CARDS_BOUGHT_QUEUE, it) }
        if (game.currentPlayer.isBot) {
            Thread.sleep(1000)
        }
    }

    fun refreshSupply(game: Game) {
        game.humanPlayers.forEach { refreshPlayerQueue(REFRESH_SUPPLY_QUEUE, it) }
    }

    fun refreshCardAction(player: Player) {
        refreshPlayerQueue(REFRESH_CARD_ACTION_QUEUE, player)
    }

    fun refreshChat(game: Game) {
        game.humanPlayers.forEach { refreshPlayerQueue(REFRESH_CHAT_QUEUE, it) }
    }

    fun refreshHistory(game: Game) {
        game.humanPlayers.forEach { refreshPlayerQueue(REFRESH_HISTORY_QUEUE, it) }
    }

    fun refreshPlayerGame(player: Player) {
        val game = player.game

        val data = getRefreshGameData(player)

        data.infoDialog = player.infoDialog

        data.title = when {
            game.status == GameStatus.Finished -> "Game Over"
            player.isYourTurn -> "Your Turn"
            else -> game.currentPlayer.username + "'s Turn"
        }

        messagingTemplate.convertAndSend("/queue/$REFRESH_GAME_QUEUE/${player.userId}", data)
    }

    private fun refreshPlayerQueue(queueName: String, player: Player) {
        if (!player.isBot) {
            messagingTemplate.convertAndSend("/queue/$queueName/${player.userId}", "refresh")
        }
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