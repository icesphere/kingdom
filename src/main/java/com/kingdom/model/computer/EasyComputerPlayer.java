package com.kingdom.model.computer;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.model.Player;

public class EasyComputerPlayer extends ComputerPlayer {
    public EasyComputerPlayer(Player player, Game game) {
        super(player, game);
        difficulty = 1;
    }

    @Override
    protected void setupStartingStrategies() {
    }

    @Override
    protected boolean excludeCard(Card card) {
        return excludeCardEasy(card);
    }
}
