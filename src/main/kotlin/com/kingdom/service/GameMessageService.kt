package com.kingdom.service

import com.kingdom.model.Game
import com.kingdom.model.players.Player
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

private const val REFRESH_GAME_QUEUE = "refresh-game"
private const val REFRESH_HAND_AREA_QUEUE = "refresh-hand-area"
private const val REFRESH_CARDS_PLAYED_QUEUE = "refresh-cards-played"
private const val REFRESH_CARDS_BOUGHT_QUEUE = "refresh-cards-bought"
private const val REFRESH_PREVIOUS_PLAYER_CARDS_BOUGHT_QUEUE = "refresh-previous-player-cards-bought"
private const val REFRESH_SUPPLY_QUEUE = "refresh-supply"
private const val REFRESH_CARD_ACTION_QUEUE = "refresh-card-action"
private const val REFRESH_CHAT_QUEUE = "refresh-chat"
private const val REFRESH_HISTORY_QUEUE = "refresh-history"
private const val SHOW_INFO_MESSAGE_QUEUE = "show-info-message"

@Service
class GameMessageService(private val messagingTemplate: SimpMessagingTemplate) {

    fun refreshGame(game: Game) {
        game.humanPlayers.forEach { refreshGame(it) }
    }

    fun refreshGame(player: Player) {
        refreshPlayerQueue(REFRESH_GAME_QUEUE, player)
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

    fun refreshCardsPlayed(player: Player) {
        refreshPlayerQueue(REFRESH_CARDS_PLAYED_QUEUE, player)
    }

    fun refreshCardsBought(game: Game) {
        game.humanPlayers.forEach { refreshPlayerQueue(REFRESH_CARDS_BOUGHT_QUEUE, it) }
        if (game.currentPlayer.isBot) {
            Thread.sleep(1000)
        }
    }

    fun refreshCardsBought(player: Player) {
        refreshPlayerQueue(REFRESH_CARDS_BOUGHT_QUEUE, player)
    }

    fun refreshPreviousPlayerCardsBought(player: Player) {
        refreshPlayerQueue(REFRESH_PREVIOUS_PLAYER_CARDS_BOUGHT_QUEUE, player)
    }

    fun refreshSupply(game: Game) {
        game.humanPlayers.forEach { refreshSupply(it) }
    }

    fun refreshSupply(player: Player) {
        refreshPlayerQueue(REFRESH_SUPPLY_QUEUE, player)
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

    fun showInfoMessage(player: Player, message: String) {
        refreshPlayerQueue(SHOW_INFO_MESSAGE_QUEUE, player, message)
    }

    fun showInfoMessageForUserId(userId: String, message: String) {
        messagingTemplate.convertAndSend("/queue/$SHOW_INFO_MESSAGE_QUEUE/$userId", message)
    }

    private fun refreshPlayerQueue(queueName: String, player: Player, data: Any = "refresh") {
        if (!player.isBot) {
            messagingTemplate.convertAndSend("/queue/$queueName/${player.userId}", data)
        }
    }
}