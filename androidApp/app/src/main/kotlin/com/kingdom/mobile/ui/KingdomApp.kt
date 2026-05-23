package com.kingdom.mobile.ui

import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.kingdom.mobile.network.CardDto
import com.kingdom.mobile.network.GameRoomDto
import com.kingdom.mobile.network.GameSnapshotDto
import com.kingdom.mobile.state.KingdomUiState
import com.kingdom.mobile.state.KingdomViewModel
import com.kingdom.mobile.state.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KingdomApp(viewModel: KingdomViewModel) {
    val state by viewModel.state.collectAsState()
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (state.screen) {
            Screen.Login -> LoginScreen(state, viewModel)
            Screen.Lobby -> LobbyScreen(state, viewModel)
            Screen.CreateGame -> CreateGameScreen(state, viewModel)
            Screen.Game -> GameScreen(state, viewModel)
        }
        state.selectedCard?.let { card ->
            ModalBottomSheet(onDismissRequest = { viewModel.selectCard(null) }) {
                CardDetail(card)
            }
        }
    }
}

@Composable
private fun LoginScreen(state: KingdomUiState, viewModel: KingdomViewModel) {
    var password by remember { mutableStateOf("winner") }
    var username by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Kingdom", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") },
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Access password") },
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { viewModel.accessAndLogin(password, username) },
                enabled = username.isNotBlank() && !state.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enter")
            }
            StatusLine(state)
        }
    }
}

@Composable
private fun LobbyScreen(state: KingdomUiState, viewModel: KingdomViewModel) {
    val lobby = state.lobby
    var chat by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar("Lobby", state.loading) {
                TextButton(onClick = { viewModel.refreshLobby() }) { Text("Refresh") }
            }
        },
        floatingActionButton = {
            Button(onClick = { viewModel.openCreateGame() }) { Text("New Game") }
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                StatusLine(state)
                Text("Rooms", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            if (lobby != null) {
                items(lobby.gameRooms, key = { it.gameId }) { room ->
                    RoomRow(room, onJoin = { viewModel.joinGame(room.gameId) })
                }
                item {
                    HorizontalDivider()
                    Text("Players", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(lobby.users.joinToString(", ") { it.username })
                }
                item {
                    Text("Lobby Chat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    lobby.chats.takeLast(8).forEach {
                        Text("${it.username}: ${it.message}", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = chat,
                            onValueChange = { chat = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Message") },
                            singleLine = true
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.sendLobbyChat(chat)
                                chat = ""
                            },
                            enabled = chat.isNotBlank()
                        ) { Text("Send") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateGameScreen(state: KingdomUiState, viewModel: KingdomViewModel) {
    var title by remember { mutableStateOf("") }
    var easyBots by remember { mutableIntStateOf(1) }
    val selectedDecks = remember { mutableStateListOf("Base") }
    val decks = state.catalog?.decks?.map { it.name }.orEmpty()

    Scaffold(topBar = { TopBar("Create Game", state.loading) }) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    singleLine = true
                )
            }
            item {
                Text("Decks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    decks.forEach { deck ->
                        FilterChip(
                            selected = selectedDecks.contains(deck),
                            onClick = {
                                if (selectedDecks.contains(deck)) selectedDecks.remove(deck) else selectedDecks.add(deck)
                            },
                            label = { Text(deck) }
                        )
                    }
                }
            }
            item {
                Text("Easy Bots", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = { easyBots = maxOf(0, easyBots - 1) }) { Text("-") }
                    Text(easyBots.toString(), Modifier.padding(horizontal = 20.dp), style = MaterialTheme.typography.titleLarge)
                    OutlinedButton(onClick = { easyBots = minOf(5, easyBots + 1) }) { Text("+") }
                }
            }
            item {
                Button(
                    onClick = { viewModel.createGame(title, selectedDecks.toList(), easyBots) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedDecks.isNotEmpty()
                ) { Text("Create") }
                StatusLine(state)
            }
        }
    }
}

@Composable
private fun GameScreen(state: KingdomUiState, viewModel: KingdomViewModel) {
    val game = state.game ?: return
    var tab by remember { mutableIntStateOf(0) }
    var showQuitConfirm by remember { mutableStateOf(false) }
    val tabs = listOf("Supply", "Hand", "Play", "Players", "Chat", "History")

    Scaffold(
        topBar = {
            TopBar(game.title.ifBlank { "Game" }, state.loading) {
                TextButton(onClick = { viewModel.refreshGame() }) { Text("Refresh") }
                GameMenu(
                    gameStatus = game.status,
                    onLeave = { viewModel.leaveWaitingGame() },
                    onQuit = { showQuitConfirm = true },
                    onExit = { viewModel.exitGame() }
                )
            }
        },
        bottomBar = { GameActions(game, viewModel) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            GameHeader(game)
            game.actionPrompt?.let { prompt ->
                ActionPrompt(
                    promptText = prompt.text.orEmpty(),
                    choices = prompt.choices.map { it.choiceNumber to it.text },
                    cardChoices = prompt.cardChoices,
                    viewModel = viewModel
                )
            }
            ScrollableTabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = tab == index, onClick = { tab = index }, text = { Text(title) })
                }
            }
            when (tab) {
                0 -> CardGrid(
                    title = "Supply",
                    cards = game.kingdomCards + game.supplyCards + game.eventsAndLandmarksAndProjectsAndWays,
                    location = "Supply",
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
                1 -> CardGrid("Hand", game.viewer.hand, "Hand", viewModel, Modifier.weight(1f))
                2 -> CardGrid("In Play", game.viewer.inPlay + game.viewer.durationCards + game.viewer.cardsBought, "PlayArea", viewModel, Modifier.weight(1f))
                3 -> PlayersList(game, Modifier.weight(1f))
                4 -> ChatPanel(game, viewModel, Modifier.weight(1f))
                5 -> HistoryPanel(game, Modifier.weight(1f))
            }
        }
    }
    if (showQuitConfirm) {
        AlertDialog(
            onDismissRequest = { showQuitConfirm = false },
            title = { Text("Quit game?") },
            text = { Text("This ends the current game for everyone.") },
            confirmButton = {
                Button(onClick = {
                    showQuitConfirm = false
                    viewModel.quitGame()
                }) { Text("Quit") }
            },
            dismissButton = {
                TextButton(onClick = { showQuitConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun TopBar(title: String, loading: Boolean, actions: @Composable () -> Unit = {}) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (loading) CircularProgressIndicator(Modifier.width(24.dp).height(24.dp), strokeWidth = 2.dp)
        actions()
    }
}

@Composable
private fun GameMenu(gameStatus: String, onLeave: () -> Unit, onQuit: () -> Unit, onExit: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Game")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (gameStatus == "WaitingForPlayers") {
                DropdownMenuItem(
                    text = { Text("Leave game") },
                    onClick = {
                        expanded = false
                        onLeave()
                    }
                )
            }
            if (gameStatus == "InProgress") {
                DropdownMenuItem(
                    text = { Text("Quit game") },
                    onClick = {
                        expanded = false
                        onQuit()
                    }
                )
            }
            DropdownMenuItem(
                text = { Text(if (gameStatus == "Finished") "Return to lobby" else "Exit to lobby") },
                onClick = {
                    expanded = false
                    onExit()
                }
            )
        }
    }
}

@Composable
private fun GameHeader(game: GameSnapshotDto) {
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Current: ${game.currentPlayerName ?: "-"}", fontWeight = FontWeight.SemiBold)
            Text("Status: ${game.status}")
            Text("Actions ${game.viewer.actions}  Buys ${game.viewer.buys}  Coins ${game.viewer.coins}  Debt ${game.viewer.debt}")
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ActionPrompt(promptText: String, choices: List<Pair<Int, String>>, cardChoices: List<CardDto>, viewModel: KingdomViewModel) {
    OutlinedCard(Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
        Column(Modifier.padding(12.dp)) {
            HtmlText(promptText, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                choices.forEach { choice ->
                    AssistChip(onClick = { viewModel.submitChoice(choice.first) }, label = { Text(choice.second) })
                }
            }
            if (cardChoices.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cardChoices.forEach { card ->
                        KingdomCardTile(card, modifier = Modifier.width(96.dp), onClick = {
                            if (card.highlighted) viewModel.clickCard(card, "CardAction") else viewModel.selectCard(card)
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun CardGrid(title: String, cards: List<CardDto>, location: String, viewModel: KingdomViewModel, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 92.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
        items(cards, key = { it.id + locationForCard(it, location) }) { card ->
            val resolvedLocation = locationForCard(card, location)
            KingdomCardTile(card, onClick = {
                if (card.highlighted) viewModel.clickCard(card, resolvedLocation) else viewModel.selectCard(card)
            })
        }
    }
}

@Composable
private fun KingdomCardTile(card: CardDto, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val border = when {
        card.highlighted -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        card.selected -> BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }
    val colors = cardPalette(card)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.68f)
            .clickable(onClick = onClick),
        border = border,
        colors = CardDefaults.cardColors(containerColor = colors.body)
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .background(colors.header),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    card.name,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = colors.headerText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (card.victoryPointsOnPile != null) {
                    Text("${card.victoryPointsOnPile} VP on pile", color = Color(0xFF087A27), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
                if (card.debtOnPile != null) {
                    Text("${card.debtOnPile} debt on pile", color = Color(0xFFA51422), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
                val text = card.text
                    .substringAfter(": ", card.text)
                    .replace(Regex("\\s+"), " ")
                Text(text, maxLines = 5, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall)
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 3.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                if (!card.type.contains("Landmark") && !card.type.contains("Way")) {
                    Text("${'$'}${card.adjustedCost}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    if (card.debtCost > 0) {
                        Text("  debt ${card.debtCost}", style = MaterialTheme.typography.labelSmall, color = Color(0xFFA51422))
                    }
                }
                Spacer(Modifier.weight(1f))
                card.pileCount?.let { Text("x$it", style = MaterialTheme.typography.labelSmall) }
            }
            Text(
                card.type,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 2.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun PlayersList(game: GameSnapshotDto, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(game.players, key = { it.userId }) { player ->
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(player.username + if (player.bot) " (bot)" else "", fontWeight = FontWeight.SemiBold)
                    Text("Deck ${player.deckCount}  Discard ${player.discardCount}  VP ${player.victoryPoints}")
                }
            }
        }
    }
}

@Composable
private fun ChatPanel(game: GameSnapshotDto, viewModel: KingdomViewModel, modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("") }
    Column(
        modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(game.chats) { chat -> HtmlText(chat.message, modifier = Modifier.fillMaxWidth()) }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = message, onValueChange = { message = it }, modifier = Modifier.weight(1f), label = { Text("Message") })
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                viewModel.sendGameChat(message)
                message = ""
            }, enabled = message.isNotBlank()) { Text("Send") }
        }
    }
}

@Composable
private fun HistoryPanel(game: GameSnapshotDto, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(game.turnHistory) { turn ->
            Text(turn.username, fontWeight = FontWeight.SemiBold)
            turn.logs.forEach {
                HtmlText(it, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun GameActions(game: GameSnapshotDto, viewModel: KingdomViewModel) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (game.canPlayTreasures) {
            Button(onClick = { viewModel.playAllTreasures() }, modifier = Modifier.weight(1f)) { Text("Treasures") }
        }
        if (game.viewer.debt > 0) {
            OutlinedButton(onClick = { viewModel.payDebt() }, modifier = Modifier.weight(1f)) { Text("Debt") }
        }
        Button(onClick = { viewModel.endTurn() }, modifier = Modifier.weight(1f)) { Text("End") }
        game.actionPrompt?.let {
            if (it.showDoNotUse) OutlinedButton(onClick = { viewModel.doNotUse() }) { Text("Skip") }
            if (it.showDone) OutlinedButton(onClick = { viewModel.done() }) { Text("Done") }
        }
    }
}

@Composable
private fun RoomRow(room: GameRoomDto, onJoin: () -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(room.title.ifBlank { room.name }, fontWeight = FontWeight.SemiBold)
                Text("${room.status}  ${room.players.size}/${room.numPlayers}", style = MaterialTheme.typography.bodySmall)
                Text(room.players.joinToString(", "), style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onJoin, enabled = room.players.size < room.numPlayers && !room.privateGame) { Text("Join") }
        }
    }
}

@Composable
private fun CardDetail(card: CardDto) {
    Column(Modifier.padding(20.dp)) {
        Text(card.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(card.type, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Text("Cost ${card.cost}" + if (card.debtCost > 0) " + ${card.debtCost} debt" else "")
        Spacer(Modifier.height(12.dp))
        HtmlText(card.text, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatusLine(state: KingdomUiState) {
    if (state.error != null) {
        Spacer(Modifier.height(12.dp))
        Text(state.error, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun HtmlText(html: String, modifier: Modifier = Modifier) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                setTextColor(textColor)
                textSize = 14f
                setLineSpacing(0f, 1.08f)
            }
        },
        update = { textView ->
            textView.setTextColor(textColor)
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    )
}

private data class CardPalette(val header: Color, val body: Color, val headerText: Color = Color.Black)

private fun cardPalette(card: CardDto): CardPalette {
    val type = card.type
    return when {
        type.contains("Treasure") -> CardPalette(Color(0xFFD6B84B), Color(0xFFFFF6CB))
        type.contains("Victory") -> CardPalette(Color(0xFF71A35D), Color(0xFFE7F2DF))
        type.contains("Curse") -> CardPalette(Color(0xFF7D5AA6), Color(0xFFEDE3F7), Color.White)
        type.contains("Reaction") -> CardPalette(Color(0xFF9CC9E8), Color(0xFFE5F3FC))
        type.contains("Attack") -> CardPalette(Color(0xFFC95A4A), Color(0xFFFBE4DF), Color.White)
        type.contains("Duration") -> CardPalette(Color(0xFFE08A3D), Color(0xFFFFE8D2))
        type.contains("Event") -> CardPalette(Color(0xFFB8A777), Color(0xFFF3EDD8))
        type.contains("Landmark") -> CardPalette(Color(0xFF7C8F4D), Color(0xFFE9EFD7))
        type.contains("Project") -> CardPalette(Color(0xFF6C7F99), Color(0xFFE1E8F1), Color.White)
        type.contains("Way") -> CardPalette(Color(0xFF8D7A5D), Color(0xFFECE4D6), Color.White)
        else -> CardPalette(Color(0xFFCFC6A2), Color(0xFFF5F0DA))
    }
}

private fun locationForCard(card: CardDto, defaultLocation: String): String {
    return when {
        defaultLocation != "Supply" -> defaultLocation
        card.type.contains("Event") -> "Event"
        card.type.contains("Landmark") -> "Landmark"
        card.type.contains("Project") -> "Project"
        card.type.contains("Way") -> "Way"
        else -> "Supply"
    }
}
