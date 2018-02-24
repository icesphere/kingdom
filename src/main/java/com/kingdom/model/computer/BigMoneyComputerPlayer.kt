package com.kingdom.model.computer

import com.kingdom.model.Card
import com.kingdom.model.Game
import com.kingdom.model.Player

class BigMoneyComputerPlayer(player: Player, game: Game) : HardComputerPlayer(player, game) {
    init {
        isBigMoneyUltimate = true
    }

    override fun buyCard(): Card? {
        return buyCardBigMoneyUltimate()
    }
}
