package com.kingdom.model.cards.hinterlands

import com.kingdom.model.cards.CardType

class SpiceMerchant : HinterlandsCard(NAME, CardType.Action, 4) {

    init {
        testing = true
        special = ""
        fontSize = 10
    }

    companion object {
        const val NAME: String = "Spice Merchant"
    }
}

