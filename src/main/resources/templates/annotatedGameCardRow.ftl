<#if !card.disabled>
    <tr>
        <td>
            <input type="checkbox" name="card_${card.name}" id="card_${card.name}" value="true" onclick="selectCard()" <#if selectedCards?seq_contains(card.name)>checked</#if> /> <label for="card_${card.name}">${card.name}</label>
        </td>
    </tr>
</#if>