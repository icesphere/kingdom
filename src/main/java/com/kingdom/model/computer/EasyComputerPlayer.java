package com.kingdom.model.computer;

import com.kingdom.model.Card;
import com.kingdom.model.Game;
import com.kingdom.model.Player;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/21/11
 * Time: 10:35 AM
 */
public class EasyComputerPlayer extends ComputerPlayer
{
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
