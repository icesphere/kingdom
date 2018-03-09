package com.kingdom.model.computer

import com.kingdom.model.cards.Card
import com.kingdom.model.OldGame
import com.kingdom.model.OldPlayer

class BigMoneyComputerPlayer(player: OldPlayer, game: OldGame) : HardComputerPlayer(player, game) {
    init {
        isBigMoneyUltimate = true
    }

    override fun buyCard(): Card? {
        return buyCardBigMoneyUltimate()
    }
}
