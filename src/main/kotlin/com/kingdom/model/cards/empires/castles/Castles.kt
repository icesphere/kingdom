package com.kingdom.model.cards.empires.castles

import com.kingdom.model.Game
import com.kingdom.model.cards.Card
import com.kingdom.model.cards.CardType
import com.kingdom.model.cards.GameSetupModifier
import com.kingdom.model.cards.MultiTypePile
import com.kingdom.model.cards.empires.EmpiresCard

class Castles : EmpiresCard(Castles.NAME, CardType.VictoryCastle, 3), MultiTypePile, GameSetupModifier {

    init {
        special = "Split pile with 8 differently named unique Castles"
    }

    override fun modifyGameSetup(game: Game) {
        game.isShowVictoryCoins = true
    }

    override val otherCardsInPile: List<Card>
        get() = listOf(
                HumbleCastle(),
                CrumblingCastle(),
                SmallCastle(),
                HauntedCastle(),
                OpulentCastle(),
                SprawlingCastle(),
                GrandCastle(),
                KingsCastle()
        )

    override fun createMultiTypePile(game: Game): List<Card> {
        return if (game.numPlayers == 2) {
            listOf(
                    HumbleCastle(),
                    CrumblingCastle(),
                    SmallCastle(),
                    HauntedCastle(),
                    OpulentCastle(),
                    SprawlingCastle(),
                    GrandCastle(),
                    KingsCastle()
            )
        } else {
            listOf(
                    HumbleCastle(),
                    HumbleCastle(),
                    CrumblingCastle(),
                    SmallCastle(),
                    SmallCastle(),
                    HauntedCastle(),
                    OpulentCastle(),
                    OpulentCastle(),
                    SprawlingCastle(),
                    GrandCastle(),
                    KingsCastle(),
                    KingsCastle()
            )
        }
    }

    companion object {
        const val NAME: String = "Castles"
    }
}