package com.kingdom.api

import com.kingdom.model.Game
import com.kingdom.model.GameRoom
import com.kingdom.model.LobbyChat
import com.kingdom.model.PlayerTurn
import com.kingdom.model.TurnSummary
import com.kingdom.model.User
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardLocation
import com.kingdom.model.cards.Deck
import com.kingdom.model.players.Player
import com.kingdom.service.CardManager
import com.kingdom.service.GameRoomManager
import com.kingdom.service.LoggedInUsers
import com.kingdom.util.groupedString
import com.kingdom.web.GameController
import org.springframework.stereotype.Component

@Component
class ApiSnapshotMapper(
        private val cardManager: CardManager,
        private val gameRoomManager: GameRoomManager,
        private val gameController: GameController
) {

    fun catalog(): CatalogDto {
        val decks = Deck.values()
                .filterNot { it == Deck.None }
                .map { deck ->
                    DeckCatalogDto(deck.name, cardManager.getCards(deck, false).map { cardDto(it, null, null, false, false) })
                }

        return CatalogDto(
                decks,
                cardManager.allEvents.sortedBy { it.name }.map { cardDto(it, null, null, false, false) },
                cardManager.allLandmarks.sortedBy { it.name }.map { cardDto(it, null, null, false, false) },
                cardManager.allProjects.sortedBy { it.name }.map { cardDto(it, null, null, false, false) },
                cardManager.allWays.sortedBy { it.name }.map { cardDto(it, null, null, false, false) }
        )
    }

    fun lobby(user: User, startGame: Boolean): LobbySnapshotDto {
        LoggedInUsers.refreshLobby(user)
        return LobbySnapshotDto(
                userDto(user),
                LoggedInUsers.getUsers().map { userDto(it) }.sortedBy { it.username.toLowerCase() },
                gameRoomManager.lobbyGameRooms.map { roomDto(it) },
                emptyList(),
                gameRoomManager.maxGameRoomLimitReached(),
                gameRoomManager.gamesInProgress.size,
                user.gameId,
                startGame
        )
    }

    fun lobby(user: User, chats: List<LobbyChat>, startGame: Boolean): LobbySnapshotDto {
        val base = lobby(user, startGame)
        return base.copy(chats = chats.map { LobbyChatDto(it.username, it.message, it.time.time, it.userId) })
    }

    fun snapshot(game: Game, viewer: Player): GameSnapshotDto {
        val action = viewer.currentAction
        val prompt = action?.let {
            ActionPromptDto(
                    it.text,
                    it.choices?.map { choice -> ChoiceDto(choice.choiceNumber, choice.text) } ?: emptyList(),
                    it.cardChoices?.map { card ->
                        cardDto(card, game, viewer, gameController.highlightCard(viewer, card, CardLocation.CardAction), it.isCardSelected(card))
                    } ?: emptyList(),
                    it.isShowDone,
                    it.isShowDoNotUse
            )
        }

        val kingdom = game.topKingdomCards.map { card ->
            cardDto(card, game, viewer, gameController.highlightCard(viewer, card, CardLocation.Supply), false)
        }
        val supply = game.cardsInSupply.map { card ->
            cardDto(card, game, viewer, gameController.highlightCard(viewer, card, CardLocation.Supply), false)
        }
        val extras = (game.events + game.landmarks + game.projects + game.ways).map { card ->
            val location = when {
                card.isEvent -> CardLocation.Event
                card.isLandmark -> CardLocation.Landmark
                card.isProject -> CardLocation.Project
                card.isWay -> CardLocation.Way
                else -> CardLocation.Unknown
            }
            cardDto(card, game, viewer, gameController.highlightCard(viewer, card, location), false)
        }

        return GameSnapshotDto(
                game.gameId,
                game.title,
                game.status.name,
                if (game.players.isEmpty()) null else game.currentPlayerId,
                if (game.players.isEmpty()) null else game.currentPlayer.username,
                playerDto(viewer, game, true),
                game.players.map { playerDto(it, game, it.userId == viewer.userId) },
                kingdom,
                supply,
                extras,
                prompt,
                game.chats.map { ChatMessageDto(it.message, it.color, it.userId) },
                game.recentHistory,
                game.recentTurnHistory.map { turnDto(it) },
                game.lastTurnSummaries.map { summaryDto(it) },
                viewer.isTreasuresPlayable,
                game.isShowVictoryPoints,
                game.isShowDuration,
                game.isShowTavern,
                game.isShowIslandCards,
                game.isShowExileCards,
                game.isShowNativeVillage,
                game.isShowJourneyToken,
                game.isShowPirateShipCoins,
                game.isTrackTradeRouteTokens,
                game.tradeRouteTokensOnMat,
                game.trashedCards.groupedString,
                game.prizeCardsString,
                game.gameEndReason,
                game.winnerString
        )
    }

    fun playerDto(player: Player, game: Game, includePrivateCards: Boolean): PlayerDto {
        val hand = if (includePrivateCards) {
            player.hand.map { cardDto(it, game, player, gameController.highlightCard(player, it, CardLocation.Hand), player.currentAction?.isCardSelected(it) ?: false) }
        } else {
            emptyList()
        }

        return PlayerDto(
                player.userId,
                player.username,
                player.isBot,
                player.isYourTurn,
                player.isWaitingForComputer,
                player.isQuit,
                player.actions,
                player.buys,
                player.availableCoins,
                player.debt,
                player.coffers,
                player.villagers,
                player.victoryPoints,
                player.victoryCoins,
                player.deck.size,
                player.cardsInDiscard.size,
                hand,
                player.inPlay.map { cardDto(it, game, player, gameController.highlightCard(player, it, CardLocation.PlayArea), false) },
                player.durationCards.map { cardDto(it, game, player, false, false) },
                if (includePrivateCards) player.cardsInDiscard.map { cardDto(it, game, player, gameController.highlightCard(player, it, CardLocation.Discard), false) } else emptyList(),
                player.cardsBought.map { cardDto(it, game, player, false, false) },
                player.tavernCards.size,
                player.islandCards.size,
                player.exileCards.size,
                player.nativeVillageCards.size,
                player.pirateShipCoins,
                player.isJourneyTokenFaceUp,
                player.projectsBought.map { it.name }
        )
    }

    fun cardDto(card: Card, game: Game?, player: Player?, highlighted: Boolean, selected: Boolean): CardDto {
        val adjustedCost = if (game != null && player != null) {
            player.getCardCostWithModifiers(card)
        } else {
            card.cost
        }
        val pileCount = game?.numInPileMap?.get(card.pileName)
        return CardDto(
                card.id,
                card.name,
                card.pileName,
                card.deck.name,
                card.typeAsString,
                card.cost,
                adjustedCost,
                card.debtCost,
                card.fullCardText,
                highlighted,
                selected,
                pileCount,
                game?.victoryPointsOnSupplyPile?.get(card.pileName),
                game?.debtOnSupplyPile?.get(card.pileName),
                game?.embargoTokens?.get(card.pileName),
                game?.tradeRouteTokenMap?.get(card.pileName) ?: false
        )
    }

    private fun userDto(user: User): UserDto =
            UserDto(user.userId, user.username, user.admin, user.gameId, user.status, user.isIdle)

    private fun roomDto(room: GameRoom): GameRoomDto =
            GameRoomDto(
                    room.name,
                    room.gameId,
                    room.game.title,
                    room.game.status.name,
                    room.game.isPrivateGame,
                    room.game.creatorName,
                    room.game.players.map { it.username },
                    room.game.numPlayers
            )

    private fun turnDto(turn: PlayerTurn): TurnHistoryDto =
            TurnHistoryDto(turn.userId, turn.username, turn.allLogs)

    private fun summaryDto(summary: TurnSummary): TurnSummaryDto =
            TurnSummaryDto(
                    summary.username,
                    summary.gameTurn,
                    summary.cardsGainedString,
                    summary.cardsBought.groupedString,
                    summary.eventsBoughtString,
                    summary.projectsBoughtString,
                    summary.cardsTrashedString,
                    summary.cardsPlayedString
            )
}
