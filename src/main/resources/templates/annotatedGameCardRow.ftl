<#if !card.disabled>
    <tr>
        <td>
            <input type="checkbox" name="card_${card.cardId}" id="card_${card.cardId}" value="true" onclick="selectCard()" <#if selectedCards?seq_contains(card.name)>checked</#if> /> <label for="card_${card.cardId}">${card.name}</label>
        </td>
    </tr>
</#if>