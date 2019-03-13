package com.kingdom.model.cards.empires.landmarks

import com.kingdom.model.Choice
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.actions.ChoiceActionCard
import com.kingdom.model.cards.listeners.AfterCardGainedListenerForLandmark
import com.kingdom.model.cards.listeners.TurnEndedListenerForLandmark
import com.kingdom.model.players.Player

class MountainPass : EmpiresLandmark(NAME), AfterCardGainedListenerForLandmark, TurnEndedListenerForLandmark, ChoiceActionCard {

    init {
        special = "When you are the first player to gain a Province, after that turn, each player bids once, up to 40 debt, ending with you. High bidder gets +8 VP and takes the debt they bid."
        fontSize = 9
    }

    var firstProvinceGained: Boolean = false

    var bidOnEndOfTurn: Boolean = false

    var playerBidMap = mutableMapOf<Player, Int>()

    var playersChoices: String = ""

    override fun afterCardGained(card: Card, player: Player) {
        if (!firstProvinceGained && card.isProvince) {
            firstProvinceGained = true
            bidOnEndOfTurn = true
        }
    }

    override fun onTurnEnded(player: Player) {
        if (bidOnEndOfTurn) {
            bidOnEndOfTurn = false

            makeChoice(player.playerToLeft)
        }
    }

    override fun actionChoiceMade(player: Player, choice: Int, info: Any?) {
        playerBidMap[player] = choice

        player.addEventLogWithUsername("bid $choice debt")

        playersChoices += "${player.username} bid $choice debt. "

        if (playerBidMap.size == player.game.players.size || choice == 40) {
            val highestBidder = playerBidMap.maxBy { it.value }!!.key
            val highestBid = playerBidMap[highestBidder]!!
            highestBidder.addVictoryCoins(8)
            highestBidder.addDebt(highestBid)
        } else {
            makeChoice(player.playerToLeft)
        }
    }

    private fun makeChoice(player: Player) {

        val choices = mutableListOf<Choice>()
        for (i in 0..40) {
            choices.add(Choice(i, i.toString()))
        }

        player.makeChoiceFromList(this, playersChoices + "How much debt do you want to bid? High bidder gets +8 VP and takes the debt they bid.", choices)
    }

    companion object {
        const val NAME: String = "Mountain Pass"
    }
}