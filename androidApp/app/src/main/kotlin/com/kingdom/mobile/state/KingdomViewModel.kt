package com.kingdom.mobile.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingdom.mobile.network.CardDto
import com.kingdom.mobile.network.CatalogDto
import com.kingdom.mobile.network.CreateGameRequest
import com.kingdom.mobile.network.GameCommandRequest
import com.kingdom.mobile.network.GameSnapshotDto
import com.kingdom.mobile.network.LobbySnapshotDto
import com.kingdom.mobile.network.UserDto
import com.kingdom.mobile.repository.KingdomRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class KingdomUiState(
    val baseUrl: String = "http://10.0.2.2:8080",
    val user: UserDto? = null,
    val catalog: CatalogDto? = null,
    val lobby: LobbySnapshotDto? = null,
    val game: GameSnapshotDto? = null,
    val selectedCard: CardDto? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val screen: Screen = Screen.Login
)

enum class Screen {
    Login,
    Lobby,
    CreateGame,
    Game
}

class KingdomViewModel(private val repository: KingdomRepository) : ViewModel() {
    private val _state = MutableStateFlow(KingdomUiState())
    val state: StateFlow<KingdomUiState> = _state.asStateFlow()
    private var pollingJob: Job? = null

    fun updateBaseUrl(baseUrl: String) {
        _state.update { it.copy(baseUrl = baseUrl) }
    }

    fun accessAndLogin(password: String, username: String) = launchLoading {
        repository.access(password)
        val user = repository.login(username)
        val catalog = repository.catalog()
        val lobby = repository.lobby()
        _state.update { it.copy(user = user, catalog = catalog, lobby = lobby, screen = Screen.Lobby) }
        stopPolling()
    }

    fun refreshLobby() = launchLoading {
        val lobby = repository.lobby()
        _state.update { it.copy(lobby = lobby, screen = Screen.Lobby) }
    }

    fun sendLobbyChat(message: String) = launchLoading {
        val lobby = repository.sendLobbyChat(message)
        _state.update { it.copy(lobby = lobby, screen = Screen.Lobby) }
    }

    fun openCreateGame() {
        _state.update { it.copy(screen = Screen.CreateGame) }
    }

    fun createGame(title: String, decks: List<String>, easyBots: Int) = launchLoading {
        val game = repository.createGame(
            CreateGameRequest(
                title = title.ifBlank { null },
                deckNames = decks.ifEmpty { listOf("Base") },
                numPlayers = easyBots + 1,
                numEasyBots = easyBots
            )
        )
        _state.update { it.copy(game = game, screen = Screen.Game) }
        startGamePolling(game.gameId)
    }

    fun joinGame(gameId: String, password: String? = null) = launchLoading {
        val game = repository.joinGame(gameId, password)
        _state.update { it.copy(game = game, screen = Screen.Game) }
        startGamePolling(game.gameId)
    }

    fun refreshGame() {
        val gameId = _state.value.game?.gameId ?: return
        viewModelScope.launch {
            runCatching { repository.snapshot(gameId) }
                .onSuccess { snapshot -> _state.update { it.copy(game = snapshot, error = null) } }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Refresh failed") } }
        }
    }

    fun clickCard(card: CardDto, location: String) = sendCommand(
        GameCommandRequest(
            type = "clickCard",
            location = location,
            cardId = card.id,
            cardName = card.name
        )
    )

    fun playAllTreasures() = sendCommand(GameCommandRequest(type = "playAllTreasures"))
    fun endTurn() = sendCommand(GameCommandRequest(type = "endTurn"))
    fun submitChoice(choice: Int) = sendCommand(GameCommandRequest(type = "choice", choice = choice))
    fun doNotUse() = sendCommand(GameCommandRequest(type = "doNotUse"))
    fun done() = sendCommand(GameCommandRequest(type = "done"))
    fun payDebt() = sendCommand(GameCommandRequest(type = "payDebt"))
    fun useCoffers(amount: Int) = sendCommand(GameCommandRequest(type = "useCoffers", amount = amount))
    fun useVillagers(amount: Int) = sendCommand(GameCommandRequest(type = "useVillagers", amount = amount))

    fun leaveWaitingGame() = launchLoading {
        val gameId = _state.value.game?.gameId ?: return@launchLoading
        stopPolling()
        val lobby = repository.leaveGame(gameId)
        _state.update { it.copy(game = null, lobby = lobby, screen = Screen.Lobby) }
    }

    fun quitGame() = exitCommand("quit")

    fun exitGame() = exitCommand("exit")

    fun sendGameChat(message: String) = launchLoading {
        val gameId = _state.value.game?.gameId ?: return@launchLoading
        val game = repository.sendGameChat(gameId, message)
        _state.update { it.copy(game = game) }
    }

    fun selectCard(card: CardDto?) {
        _state.update { it.copy(selectedCard = card) }
    }

    private fun sendCommand(request: GameCommandRequest) {
        val gameId = _state.value.game?.gameId ?: return
        launchLoading {
            val game = repository.command(gameId, request)
            _state.update { it.copy(game = game, screen = Screen.Game) }
        }
    }

    private fun exitCommand(type: String) {
        val gameId = _state.value.game?.gameId ?: return
        launchLoading {
            repository.command(gameId, GameCommandRequest(type = type))
            stopPolling()
            val lobby = repository.lobby()
            _state.update { it.copy(game = null, lobby = lobby, screen = Screen.Lobby) }
        }
    }

    private fun startGamePolling(gameId: String) {
        stopPolling()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(3_000)
                runCatching { repository.snapshot(gameId) }
                    .onSuccess { snapshot -> _state.update { it.copy(game = snapshot, error = null) } }
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun launchLoading(block: suspend () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { block() }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Request failed") } }
            _state.update { it.copy(loading = false) }
        }
    }
}
