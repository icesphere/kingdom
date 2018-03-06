<div>
    <div style="padding-bottom:10px;">
        Show Card Action: ${player.showCardAction?string}<#if player.showCardAction> - ${player.oldCardAction.type}</#if>
    </div>
    <div style="padding-bottom:10px;">
        Current Hand: ${player.currentHand}
    </div>
    <div style="padding-bottom:10px;">
        <input type="radio" name="currentHandChoice_${player.userId}" value="keep" checked /> Keep cards in hand
        <br/>
        <input type="radio" name="currentHandChoice_${player.userId}" value="discard" /> Discard cards in hand
        <br/>
        <input type="radio" name="currentHandChoice_${player.userId}" value="trash" /> Trash cards in hand
    </div>
    <div style="padding-bottom:10px;">
        Add Cards To Hand:
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
