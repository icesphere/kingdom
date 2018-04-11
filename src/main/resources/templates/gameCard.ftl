<#assign cost = card.cost - costDiscount>
<#if cost lt 0><#assign cost = 0></#if>
<div class="card <#if (clickType=="supply" && gameStatus == "InProgress" && supply(card.name) == 0)>cardDisabled<#elseif clickType == "supply" && currentPlayerId == player.userId && player.coins gte cost && player.buys gt 0>greenClickableCard<#elseif clickType == "hand" && currentPlayerId == player.userId && card.action && !player.hasBoughtCard() && player.actions &gt; 0>greenClickableCard<#elseif (clickType == "hand" && currentPlayerId == player.userId && (card.action || card.treasure)) || clickType == "random">clickableCard</#if>" clickType="${clickType}" cardName="${card.name}" special="${card.special}" cardName="${card.name}" disableSelect="${card.disableSelect?string}" hideOnSelect="<#if clickType == "oldCardAction" && hideOnSelect>true<#else>false</#if>" autoSelect="${card.autoSelect?string}" <#if clickType != "discard">cardIndex="${card_index}"</#if> <#if card.special != "">title="${card.special}"</#if> <#if ((clickType == "supply" && player.coins gte cost && player.buys gt 0) || clickType == "hand") && currentPlayerId == player.userId>onclick="clickCard('${clickType}', ${card.name}, ${card.id}, ${card.specialCard?string})"</#if> <#if clickType == "random">onclick="swapCard(${card.name})"</#if>>
    <#if mobile>
        <#include "gameCardMobile.ftl">
    <#else>
        <#include "gameCardNormal.ftl">
    </#if>
</div>