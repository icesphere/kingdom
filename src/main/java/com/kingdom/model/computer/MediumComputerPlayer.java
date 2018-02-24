package com.kingdom.model.computer;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.model.Player;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/21/11
 * Time: 10:35 AM
 */
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
