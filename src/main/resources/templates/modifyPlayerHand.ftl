<div>
    <div style="padding-bottom:10px;">
        Current Hand: ${player.currentHand}
    </div>

    <div style="padding-bottom:10px;">
        In Play: ${player.currentCardsInPlay}
    </div>

    <div style="padding-bottom:10px;">
        <select name="modifyCardsChoice_${player.userId}" onchange="updateModifyCardsText('${player.userId}')">
            <option value="hand" selected>Modify Hand</option>
            <option value="inPlay">Modify Cards In Play</option>
        </select>
    </div>

    <div style="padding-bottom:10px;">
        <input type="radio" name="currentHandChoice_${player.userId}" value="keep" checked /> <span id="keepCardsText_${player.userId}">Keep cards in hand</span>
        <br/>
        <input type="radio" name="currentHandChoice_${player.userId}" value="discard" /> <span id="discardCardsText_${player.userId}">Discard cards in hand</span>
        <br/>
        <input type="radio" name="currentHandChoice_${player.userId}" value="trash" /> <span id="trashCardsText_${player.userId}">Trash cards in hand</span>
        <br/>
        <input type="radio" name="currentHandChoice_${player.userId}" value="remove" /> <span id="removeCardsText_${player.userId}">Remove cards in hand</span>
    </div>

    <div style="padding-bottom:10px;">
        Add # Actions: <input type="text" name="addActions_${player.userId}" size="5" />
    </div>

    <div style="padding-bottom:10px;">
        Add # Buys: <input type="text" name="addBuys_${player.userId}" size="5" />
    </div>

    <div style="padding-bottom:10px;">
        Add # Coins: <input type="text" name="addCoins_${player.userId}" size="5" />
    </div>

    <div style="padding-bottom:10px;">
        Add # Coffers: <input type="text" name="addCoffers_${player.userId}" size="5" />
    </div>

    <div style="padding-bottom:10px;">
        Add # Villagers: <input type="text" name="addVillagers_${player.userId}" size="5" />
    </div>

    <div style="padding-bottom:10px;">
        Add # Debt: <input type="text" name="addDebt_${player.userId}" size="5" />
    </div>

    <div style="padding-bottom:10px;">
        Draw # Cards: <input type="text" name="addCards_${player.userId}" size="5" />
    </div>

    <div style="padding-bottom:10px;">
        <span id="addCardsText_${player.userId}">Add Cards To Hand:</span>
    </div>

    <div style="padding-bottom: 10px;">
        <input type="checkbox" id="removeCardsFromSupply_${player.userId}" name="removeCardsFromSupply_${player.userId}" />
        <label for="removeCardsFromSupply_${player.userId}">Remove cards from supply</label>
    </div>

    <#list cards as card>
        <div>
            <select name="card_${card.name}_${player.userId}">
                <option value="0">0</option>
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
                <option value="5">5</option>
                <option value="6">6</option>
            </select>
            &#160;
            ${card.name}
        </div>
    </#list>
</div>
