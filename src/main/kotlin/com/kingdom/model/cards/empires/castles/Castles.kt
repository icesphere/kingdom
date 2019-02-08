package com.kingdom.model.cards.empires.castles

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.MultiTypePile
import com.kingdom.model.cards.empires.EmpiresCard

class Castles : EmpiresCard(Castles.NAME, CardType.VictoryCastle, 3), MultiTypePile, GameSetupModifier {

    init {
        disabled = true
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowVictoryCoins = true
    }

    override val otherCardsInPile: List<Card>
        get() = listOf(
                HumbleCastle(),
                CrumblingCastle(),
                SmallCastle()
        )

    override fun createMultiTypePile(game: Game): List<Card> {
        return if (game.numPlayers == 2) {
            listOf(
                    HumbleCastle(),
                    CrumblingCastle(),
                    SmallCastle()
            )
        } else {
            listOf(
                    HumbleCastle(),
                    HumbleCastle(),
                    CrumblingCastle(),
                    SmallCastle(),
                    SmallCastle()
            )
        }
    }

    companion object {
        const val NAME: String = "Castles"
    }
}