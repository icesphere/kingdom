<#assign cost = card.cost - costDiscount>
<#if cost lt 0><#assign cost = 0></#if>
<div class="card <#if (clickType=="supply" && gameStatus == "InProgress" && supply(card.name) == 0)>cardDisabled<#elseif card.highlighted>greenClickableCard<#elseif clickType == "random">clickableCard</#if>" clickType="${clickType}" cardName="${card.name}" special="${card.special}" disableSelect="${card.disableSelect?string}" hideOnSelect="<#if clickType == "cardAction" && hideOnSelect>true<#else>false</#if>" autoSelect="${card.autoSelect?string}" <#if clickType != "discard">cardIndex="${card_index}"</#if> <#if card.special != "">title="${card.special}"</#if> <#if card.highlighted>onclick="clickCard('${clickType}', '${card.name}', '${card.id}', ${card.specialCard?string})"</#if> <#if clickType == "random">onclick="swapCard('${card.name}')"</#if>>
    <#if mobile>
        <#include "gameCardMobile.ftl">
    <#else>
        <#include "gameCardNormal.ftl">
    </#if>
</div>