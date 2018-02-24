package com.kingdom.model.computer;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.model.Player;

public class BigMoneyComputerPlayer extends HardComputerPlayer {
    public BigMoneyComputerPlayer(Player player, Game game) {
        super(player, game);
        bigMoneyUltimate = true;
    }

    @Override
    public Card buyCard() {
        return buyCardBigMoneyUltimate();
    }
}
