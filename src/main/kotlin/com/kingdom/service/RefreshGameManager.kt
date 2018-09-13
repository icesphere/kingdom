package com.kingdom.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.kingdom.model.Game
import com.kingdom.model.GameStatus
import com.kingdom.model.players.Player
import com.kingdom.web.GameController
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class RefreshGameManager(private val messagingTemplate: SimpMessagingTemplate) {

    fun refreshGame(game: Game) {
        game.players.forEach { refreshPlayer(it) }
    }

    fun refreshPlayer(player: Player) {
        val game = player.game

        val refresh = player.game.needsRefresh[player.userId]!!

        val data = GameController.RefreshGameData(refresh, game.status, player.isYourTurn)

        var divsToLoad = 0
        if (refresh.isRefreshPlayers) {
            divsToLoad++
        }
        if (refresh.isRefreshSupply) {
            divsToLoad++
        }
        if (refresh.isRefreshPlayingArea) {
            divsToLoad++
        }
        if (refresh.isRefreshCardsPlayedDiv) {
            divsToLoad++
        }
        if (refresh.isRefreshCardsBoughtDiv) {
            divsToLoad++
        }
        if (refresh.isRefreshHistory) {
            divsToLoad++
        }
        if (refresh.isRefreshHandArea) {
            divsToLoad++
        }
        if (refresh.isRefreshHand) {
            divsToLoad++
        }
        if (refresh.isRefreshDiscard) {
            divsToLoad++
        }
        if (refresh.isRefreshChat) {
            divsToLoad++
        }
        if (refresh.isRefreshInfoDialog) {
            data.infoDialog = player.infoDialog
            divsToLoad++
        }
        if (refresh.isRefreshTitle) {
            data.title = when {
                game.status == GameStatus.Finished -> "Game Over"
                player.isYourTurn -> "Your Turn"
                else -> game.currentPlayer.username + "'s Turn"
            }
        }

        data.divsToLoad = divsToLoad

        messagingTemplate.convertAndSend("/queue/refresh-game/" + player.userId, ObjectMapper().writeValueAsString(data))
    }
}