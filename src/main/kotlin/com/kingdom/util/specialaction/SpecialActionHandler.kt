package com.kingdom.util.specialaction

import com.kingdom.model.cards.Card
import com.kingdom.model.OldGame
import com.kingdom.model.IncompleteCard

object SpecialActionHandler {

    @JvmOverloads
    fun handleSpecialAction(game: OldGame, card: Card, repeatedAction: Boolean = false) {
        var incompleteCard: IncompleteCard? = null
        when {
            card.isKingdom -> incompleteCard = KingdomSpecialActionHandler.handleSpecialAction(game, card, repeatedAction)
            card.isIntrigue -> incompleteCard = IntrigueSpecialActionHandler.handleSpecialAction(game, card)
            card.isSeaside -> incompleteCard = SeasideSpecialActionHandler.handleSpecialAction(game, card, repeatedAction)
            card.isAlchemy -> incompleteCard = AlchemySpecialActionHandler.handleSpecialAction(game, card)
            card.isProsperity -> incompleteCard = ProsperitySpecialActionHandler.handleSpecialAction(game, card)
            card.isCornucopia -> incompleteCard = CornucopiaSpecialActionHandler.handleSpecialAction(game, card)
            card.isHinterlands -> incompleteCard = HinterlandsSpecialActionHandler.handleSpecialAction(game, card)
            card.isPromo -> incompleteCard = PromoSpecialActionHandler.handleSpecialAction(game, card)
            card.isSalvation -> incompleteCard = SalvationSpecialActionHandler.handleSpecialAction(game, card)
            card.isFairyTale -> incompleteCard = FairyTaleSpecialActionHandler.handleSpecialAction(game, card, repeatedAction)
            card.isProletariat -> incompleteCard = ProletariatSpecialActionHandler.handleSpecialAction(game, card)
            card.isFan -> FanSpecialActionHandler.handleSpecialAction(game, card)
        }

        if (incompleteCard != null) {
            incompleteCard.actionFinished(game.currentPlayer!!)
        }

        if (!game.hasIncompleteCard() && !game.currentPlayer!!.isShowCardAction) {
            if (!game.repeatedActions.isEmpty()) {
                game.playRepeatedAction(game.currentPlayer!!, false)
            } else if (!game.golemActions.isEmpty()) {
                game.playGolemActionCard(game.currentPlayer)
            }
        }
    }
}
