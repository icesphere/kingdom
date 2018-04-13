<#if !card.disabled>
    <tr>
        <td>
            <input type="checkbox" name="card_${card.name}" id="card_${card.name}" value="true" onclick="selectCard()" /> <label for="card_${card.name}" title="${card.fullCardText}">${card.name}</label><#if card.testing> (testing)</#if><#if card.playTreasureCardsRequired> *</#if>
        </td>
    </tr>
</#if>