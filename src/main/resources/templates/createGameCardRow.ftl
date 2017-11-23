<#if !card.disabled>
    <tr>
        <td>
            <input type="checkbox" name="card_${card.cardId}" id="card_${card.cardId}" value="true" onclick="selectCard()" /> <label for="card_${card.cardId}" title="${card.fullCardText}">${card.name}</label><#if card.testing> (testing)</#if><#if card.playTreasureCards> *</#if>
        </td>
    </tr>
</#if>