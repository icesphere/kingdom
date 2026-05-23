package com.kingdom.api

data class AccessRequest(val password: String? = null)

data class SessionRequest(val username: String? = null)

data class ChatRequest(val message: String? = null, val receivingUserId: String? = null)

data class CreateGameRequest(
        val title: String? = null,
        val deckNames: List<String>? = null,
        val numPlayers: Int? = null,
        val numEasyBots: Int? = null,
        val numMediumBots: Int? = null,
        val numHardBots: Int? = null,
        val numBigMoneyBots: Int? = null,
        val showVictoryPoints: Boolean? = null,
        val identicalStartingHands: Boolean? = null,
        val privateGame: Boolean? = null,
        val password: String? = null,
        val includeColonyAndPlatinum: Boolean? = null,
        val includeShelters: Boolean? = null,
        val customCardNames: List<String>? = null,
        val excludedCardNames: List<String>? = null,
        val customEventNames: List<String>? = null,
        val customLandmarkNames: List<String>? = null,
        val customProjectNames: List<String>? = null,
        val customWayNames: List<String>? = null,
        val numEventsAndLandmarksAndProjectsAndWays: Int? = null
)

data class JoinGameRequest(val password: String? = null)

data class GameCommandRequest(
        val type: String? = null,
        val location: String? = null,
        val cardId: String? = null,
        val cardName: String? = null,
        val choice: Int? = null,
        val amount: Int? = null
)

data class ApiMessageDto(val message: String)

data class UserDto(
        val userId: String,
        val username: String,
        val admin: Boolean,
        val gameId: String?,
        val status: String,
        val idle: Boolean
)

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
        val pileCount: Int?,
        val victoryPointsOnPile: Int?,
        val debtOnPile: Int?,
        val embargoTokens: Int?,
        val tradeRouteToken: Boolean
)

data class ChoiceDto(val choiceNumber: Int, val text: String)

data class ActionPromptDto(
        val text: String?,
        val choices: List<ChoiceDto>,
        val cardChoices: List<CardDto>,
        val showDone: Boolean,
        val showDoNotUse: Boolean
)

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
        val hand: List<CardDto>,
        val inPlay: List<CardDto>,
        val durationCards: List<CardDto>,
        val discard: List<CardDto>,
        val cardsBought: List<CardDto>,
        val tavernCardsCount: Int,
        val islandCardsCount: Int,
        val exileCardsCount: Int,
        val nativeVillageCardsCount: Int,
        val pirateShipCoins: Int,
        val journeyTokenFaceUp: Boolean,
        val projects: List<String>
)

data class GameSnapshotDto(
        val gameId: String,
        val title: String,
        val status: String,
        val currentPlayerId: String?,
        val currentPlayerName: String?,
        val viewer: PlayerDto,
        val players: List<PlayerDto>,
        val kingdomCards: List<CardDto>,
        val supplyCards: List<CardDto>,
        val eventsAndLandmarksAndProjectsAndWays: List<CardDto>,
        val actionPrompt: ActionPromptDto?,
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

data class LobbySnapshotDto(
        val user: UserDto,
        val users: List<UserDto>,
        val gameRooms: List<GameRoomDto>,
        val chats: List<LobbyChatDto>,
        val maxGameRoomLimitReached: Boolean,
        val numGamesInProgress: Int,
        val currentGameId: String?,
        val startGame: Boolean
)

data class LobbyChatDto(val username: String, val message: String, val time: Long, val userId: String?)

data class ChatMessageDto(val message: String, val color: String, val userId: String?)

data class TurnHistoryDto(val userId: String, val username: String, val logs: List<String>)

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

data class CatalogDto(
        val decks: List<DeckCatalogDto>,
        val events: List<CardDto>,
        val landmarks: List<CardDto>,
        val projects: List<CardDto>,
        val ways: List<CardDto>
)

data class DeckCatalogDto(val name: String, val cards: List<CardDto>)
