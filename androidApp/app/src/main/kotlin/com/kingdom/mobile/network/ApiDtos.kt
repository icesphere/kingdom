package com.kingdom.mobile.network

import kotlinx.serialization.Serializable

@Serializable
data class AccessRequest(val password: String)

@Serializable
data class SessionRequest(val username: String)

@Serializable
data class ChatRequest(val message: String, val receivingUserId: String? = null)

@Serializable
data class CreateGameRequest(
    val title: String? = null,
    val deckNames: List<String> = listOf("Base"),
    val numPlayers: Int = 2,
    val numEasyBots: Int = 1,
    val numMediumBots: Int = 0,
    val numHardBots: Int = 0,
    val numBigMoneyBots: Int = 0,
    val showVictoryPoints: Boolean = true,
    val identicalStartingHands: Boolean = false,
    val privateGame: Boolean = false,
    val password: String? = null,
    val includeColonyAndPlatinum: Boolean? = null,
    val includeShelters: Boolean? = null,
    val customCardNames: List<String> = emptyList(),
    val excludedCardNames: List<String> = emptyList(),
    val customEventNames: List<String> = emptyList(),
    val customLandmarkNames: List<String> = emptyList(),
    val customProjectNames: List<String> = emptyList(),
    val customWayNames: List<String> = emptyList(),
    val numEventsAndLandmarksAndProjectsAndWays: Int = 2
)

@Serializable
data class JoinGameRequest(val password: String? = null)

@Serializable
data class GameCommandRequest(
    val type: String,
    val location: String? = null,
    val cardId: String? = null,
    val cardName: String? = null,
    val choice: Int? = null,
    val amount: Int? = null
)

@Serializable
data class UserDto(
    val userId: String,
    val username: String,
    val admin: Boolean,
    val gameId: String? = null,
    val status: String,
    val idle: Boolean
)

@Serializable
data class CardDto(
    val id: String,
    val name: String,
    val pileName: String,
    val deck: String,
    val type: String,
    val cost: Int,
    val adjustedCost: Int,
    val debtCost: Int,
    val text: String,
    val highlighted: Boolean,
    val selected: Boolean,
    val pileCount: Int? = null,
    val victoryPointsOnPile: Int? = null,
    val debtOnPile: Int? = null,
    val embargoTokens: Int? = null,
    val tradeRouteToken: Boolean = false
)

@Serializable
data class ChoiceDto(val choiceNumber: Int, val text: String)

@Serializable
data class ActionPromptDto(
    val text: String? = null,
    val choices: List<ChoiceDto> = emptyList(),
    val cardChoices: List<CardDto> = emptyList(),
    val showDone: Boolean = false,
    val showDoNotUse: Boolean = false
)

@Serializable
data class PlayerDto(
    val userId: String,
    val username: String,
    val bot: Boolean,
    val currentTurn: Boolean,
    val waitingForComputer: Boolean,
    val quit: Boolean,
    val actions: Int,
    val buys: Int,
    val coins: Int,
    val debt: Int,
    val coffers: Int,
    val villagers: Int,
    val victoryPoints: Int,
    val victoryCoins: Int,
    val deckCount: Int,
    val discardCount: Int,
    val hand: List<CardDto> = emptyList(),
    val inPlay: List<CardDto> = emptyList(),
    val durationCards: List<CardDto> = emptyList(),
    val discard: List<CardDto> = emptyList(),
    val cardsBought: List<CardDto> = emptyList(),
    val tavernCardsCount: Int,
    val islandCardsCount: Int,
    val exileCardsCount: Int,
    val nativeVillageCardsCount: Int,
    val pirateShipCoins: Int,
    val journeyTokenFaceUp: Boolean,
    val projects: List<String> = emptyList()
)

@Serializable
data class GameSnapshotDto(
    val gameId: String,
    val title: String,
    val status: String,
    val currentPlayerId: String? = null,
    val currentPlayerName: String? = null,
    val viewer: PlayerDto,
    val players: List<PlayerDto>,
    val kingdomCards: List<CardDto>,
    val supplyCards: List<CardDto>,
    val eventsAndLandmarksAndProjectsAndWays: List<CardDto>,
    val actionPrompt: ActionPromptDto? = null,
    val chats: List<ChatMessageDto>,
    val recentHistory: List<String>,
    val turnHistory: List<TurnHistoryDto>,
    val lastTurnSummaries: List<TurnSummaryDto>,
    val canPlayTreasures: Boolean,
    val showVictoryPoints: Boolean,
    val showDuration: Boolean,
    val showTavern: Boolean,
    val showIslandCards: Boolean,
    val showExileCards: Boolean,
    val showNativeVillage: Boolean,
    val showJourneyToken: Boolean,
    val showPirateShipCoins: Boolean,
    val showTradeRouteTokens: Boolean,
    val tradeRouteTokensOnMat: Int,
    val trashedCards: String,
    val prizeCards: String,
    val gameEndReason: String,
    val winnerString: String
)

@Serializable
data class GameRoomDto(
    val name: String,
    val gameId: String,
    val title: String,
    val status: String,
    val privateGame: Boolean,
    val creatorName: String,
    val players: List<String>,
    val numPlayers: Int
)

@Serializable
data class LobbySnapshotDto(
    val user: UserDto,
    val users: List<UserDto>,
    val gameRooms: List<GameRoomDto>,
    val chats: List<LobbyChatDto>,
    val maxGameRoomLimitReached: Boolean,
    val numGamesInProgress: Int,
    val currentGameId: String? = null,
    val startGame: Boolean
)

@Serializable
data class LobbyChatDto(val username: String, val message: String, val time: Long, val userId: String? = null)

@Serializable
data class ChatMessageDto(val message: String, val color: String, val userId: String? = null)

@Serializable
data class TurnHistoryDto(val userId: String, val username: String, val logs: List<String>)

@Serializable
data class TurnSummaryDto(
    val username: String,
    val gameTurn: Int,
    val cardsGained: String,
    val cardsBought: String,
    val eventsBought: String,
    val projectsBought: String,
    val trashedCards: String,
    val cardsPlayed: String
)

@Serializable
data class CatalogDto(
    val decks: List<DeckCatalogDto>,
    val events: List<CardDto>,
    val landmarks: List<CardDto>,
    val projects: List<CardDto>,
    val ways: List<CardDto>
)

@Serializable
data class DeckCatalogDto(val name: String, val cards: List<CardDto>)
