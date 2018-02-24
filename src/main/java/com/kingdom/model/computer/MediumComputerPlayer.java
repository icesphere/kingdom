package com.kingdom.model.computer;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.model.Player;

public class MediumComputerPlayer extends ComputerPlayer {
    public MediumComputerPlayer(Player player, Game game) {
        super(player, game);
        difficulty = 2;
    }

    @Override
    protected void setupStartingStrategies() {
        if (hasGardens && hasExtraBuys) {
            gardensStrategy = true;
        } else if (kingdomCardMap.containsKey("Chapel")) {
            chapelStrategy = true;
        } else if (hasDuke) {
            dukeStrategy = true;
        }
    }

    @Override
    protected boolean excludeCard(Card card) {
        return excludeCardMedium(card);
    }
}
