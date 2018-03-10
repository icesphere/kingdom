package com.kingdom.model

import com.kingdom.model.cards.Card
import com.kingdom.model.cards.supply.*
import com.kingdom.model.players.HumanPlayer
import com.kingdom.model.players.Player
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.IOException
import java.util.*

class Game {
    var gameId: String? = null

    var turn: Int = 0

    var players: List<Player>? = null

    var kingdomCards: MutableList<Card> = ArrayList()

    val supplyCards = ArrayList<Card>()
    val supplyAmounts = HashMap<String, Int>()

    var trashedCards: MutableList<Card> = ArrayList()

    var currentPlayerIndex: Int = 0

    var isGameOver: Boolean = false
        private set

    var isCreateGameLog = true

    var gameLog = StringBuilder()

    private val currentTurnLog = StringBuilder()

    var quitGamePlayer: Player? = null
        private set

    val chatMessages: List<ChatMessage> = ArrayList()

    private val recentTurnLogs = ArrayList<String>()

    private var timedOut: Boolean = false

    var creationTime = Date()

    var lastActivity = Date()

    var isIncludePlatinumCards: Boolean = false

    var isIncludeColonyCards: Boolean = false

    var numPlayers: Int = 0

    var numComputerPlayers: Int = 0

    init {
        gameId = UUID.randomUUID().toString()
    }

    fun setupGame() {
        currentPlayerIndex = 0

        turn = 1

        kingdomCards.sortBy { it.cost }

        lastActivity = Date()

        setupSupply()
    }

    fun setupSupply() {
        setupSupplyAmounts()

        supplyCards.add(Copper())
        supplyCards.add(Silver())
        supplyCards.add(Gold())
        if (isIncludePlatinumCards) {
            supplyCards.add(Platinum())
        }
        supplyCards.add(Estate())
        supplyCards.add(Duchy())
        supplyCards.add(Province())
        if (isIncludeColonyCards) {
            supplyCards.add(Colony())
        }
        supplyCards.add(Curse())
    }

    private fun setupSupplyAmounts() {
        for (card in kingdomCards) {
            var numEachCard = 10
            if (card.isVictory) {
                if (numPlayers == 2) {
                    numEachCard = 8
                } else {
                    numEachCard = 12
                }
            }
            supplyAmounts[card.name] = numEachCard
        }
        if (numPlayers > 4) {
            supplyAmounts[Copper.NAME] = 120
        } else {
            supplyAmounts[Copper.NAME] = 60
        }
        if (numPlayers > 4) {
            supplyAmounts[Silver.NAME] = 80
        } else {
            supplyAmounts[Silver.NAME] = 40
        }
        if (numPlayers > 4) {
            supplyAmounts[Gold.NAME] = 60
        } else {
            supplyAmounts[Gold.NAME] = 30
        }
        if (isIncludePlatinumCards) {
            supplyAmounts[Platinum.NAME] = 12
        }
        if (numPlayers == 2) {
            supplyAmounts[Estate.NAME] = 8
            supplyAmounts[Duchy.NAME] = 8
            supplyAmounts[Province.NAME] = 8
            if (isIncludeColonyCards) {
                supplyAmounts[Colony.NAME] = 8
            }
            supplyAmounts[Curse.NAME] = 10
        } else if (numPlayers > 4) {
            supplyAmounts[Estate.NAME] = 12
            supplyAmounts[Duchy.NAME] = 12
            if (isIncludeColonyCards) {
                supplyAmounts[Colony.NAME] = 12
            }
            if (numPlayers == 5) {
                supplyAmounts[Province.NAME] = 15
                supplyAmounts[Curse.NAME] = 40
            } else {
                supplyAmounts[Province.NAME] = 18
                supplyAmounts[Curse.NAME] = 50
            }
        } else {
            supplyAmounts[Estate.NAME] = 12
            supplyAmounts[Duchy.NAME] = 12
            supplyAmounts[Province.NAME] = 12
            if (isIncludeColonyCards) {
                supplyAmounts[Colony.NAME] = 12
            }
            if (numPlayers == 3) {
                supplyAmounts[Curse.NAME] = 20
            } else {
                supplyAmounts[Curse.NAME] = 30
            }
        }
    }

    fun startGame() {
        startTurnInNewThreadIfComputerVsHuman()
    }

    private fun startTurnInNewThreadIfComputerVsHuman() {
        if (currentPlayer.isBot && currentPlayer.opponents.any{it is HumanPlayer}) {
            currentPlayer.opponents.filter { it is HumanPlayer }.forEach { p ->
                p.isWaitingForComputer = true
            }
            Thread { currentPlayer.startTurn() }.start()
        } else {
            currentPlayer.startTurn()
        }
    }

    fun getCardsAsString(cards: List<*>): String {
        var cardString = ""
        var first = true
        for (card in cards) {
            if (!first) {
                cardString += ", "
            } else {
                first = false
            }
            cardString += (card as Card).name
        }
        return cardString
    }

    fun turnEnded() {
        gameLog("End of turn " + turn)

        for (player in players!!) {
            //todo calculate if game over
            if (false) {
                gameOver()
                return
            }
        }

        if (currentPlayerIndex == players!!.size - 1) {
            currentPlayerIndex = 0
        } else {
            currentPlayerIndex++
        }

        turn++

        recentTurnLogs.add(currentTurnLog.toString())

        currentTurnLog.setLength(0)

        if (recentTurnLogs.size > 1) {
            recentTurnLogs.removeAt(0)
        }

        if (!isGameOver) {
            startTurnInNewThreadIfComputerVsHuman()
        }
    }

    private fun gameOver() {
        isGameOver = true
        gameLog("-----------------------------")
        gameLog("Game over")
        gameLog("Turns: " + turn)
        for (player in players!!) {
            player.isWaitingForComputer = false
            val playerName = player.playerName
            //todo show score
        }
        for (player in players!!) {
            gameLog("----")
            val playerName = player.playerName
            gameLog(playerName + "'s cards: ")
            player.allCards.forEach { c -> gameLog(c.name) }
        }

        writeGameLog()
    }

    val gameLogFile: File?
        get() {
            //todo
            return null
            /*val userDirectory = FileUtils.getUserDirectory()
            val gameLogDirectory = File(userDirectory, "kingdomgamelogs")

            var gameLogFileName = "game_"

            if (quitGamePlayer != null) {
                gameLogFileName += quitGamePlayer!!.playerName + "_quit_"
            }

            if (timedOut) {
                gameLogFileName += "timeout_"
            }

            gameLogFileName += winner.infoForGameLogName + "_over_" + loser.infoForGameLogName + "_" + gameId

            return File(gameLogDirectory, gameLogFileName)*/
        }

    private fun writeGameLog() {
        try {
            //todo
            /*val gameLogFile = gameLogFile
            //gameLog.append("Game log file: ").append(gameLogFile.getAbsolutePath());
            FileUtils.writeStringToFile(gameLogFile,
                    gameLog.toString().replace("<br/>".toRegex(), "\n").replace("<b>".toRegex(), "").replace("</b>".toRegex(), ""),
                    "UTF-8")*/
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //todo
    /*val winner: Player
        get() {
            return players!!.stream().max { p1, p2 -> Integer.compare(p1.authority, p2.authority) }.get()
        }

    val loser: Player
        get() {
            return players!!.stream().min { p1, p2 -> Integer.compare(p1.authority, p2.authority) }.get()
        }*/

    fun gameLog(log: String) {
        gameLog(log, false)
    }

    private fun gameLog(log: String, simulationInfo: Boolean) {
        if (SHOW_GAME_LOG) {
            println(log)
        }
        if (isCreateGameLog) {
            gameLog.append(log).append("<br/>")
            currentTurnLog.append(log).append("<br/>")
        }
    }

    val currentPlayer: Player
        get() = players!![currentPlayerIndex]

    fun trashCardFromSupply(card: Card) {
        //todo
        gameLog("Trashed " + card.name + " from supply")
        trashedCards.add(card)
    }

    fun quitGame(player: Player) {
        quitGamePlayer = player
        gameLog(player.playerName + " quit the game")
        gameOver()
    }

    val recentTurnsLog: String
        get() = currentTurnLog.toString() + StringUtils.join(recentTurnLogs, "")

    fun gameTimedOut() {
        gameLog("Game timed out")
        timedOut = true
        writeGameLog()
    }

    fun isCardAvailableInSupply(card: Card): Boolean {
        return supplyAmounts.containsKey(card.name) && supplyAmounts[card.name]!! > 0
    }

    fun removeCardFromSupply(card: Card) {
        //todo
    }

    val nonEmptySupplyCards
        get() = supplyCards.filter { supplyAmounts[it.name]!! > 0 }

    val emptySupplyPiles
        get() = supplyCards.size - nonEmptySupplyCards.size

    companion object {

        val SHOW_GAME_LOG = false
    }
}
