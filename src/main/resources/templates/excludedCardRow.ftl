<#if !card.disabled>
    <tr>
        <td>
            <input type="checkbox" name="excluded_card_${card.name}" id="excluded_card_${card.name}" value="true" <#if excludedCards?seq_contains(card.name)>checked="true"</#if> /> <label for="excluded_card_${card.name}" title="${card.fullCardText}">${card.name}</label>
        </td>
    </tr>
</#if>