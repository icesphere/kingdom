<#assign cost = card.adjustedCost>
<#if cost lt 0><#assign cost = 0></#if>
<div class="card <#if card.selected>selectedCard<#elseif card.highlighted>greenClickableCard<#elseif (clickType=="supply" && gameStatus == "InProgress" && supply(card.pileName) == 0)>cardDisabled<#elseif clickType == "random">clickableCard</#if>" clickType="${clickType}" cardName="${card.name}" special="${card.special}" disableSelect="${card.disableSelect?string}" hideOnSelect="<#if clickType == "cardAction" && hideOnSelect?? && hideOnSelect>true<#else>false</#if>" autoSelect="${card.autoSelect?string}" <#if clickType != "discard">cardIndex="${card_index}"</#if> <#if card.special != "">title="${card.special}"</#if> <#if card.highlighted>onclick="clickCard('${clickType}', '${card.name?js_string}', '${card.id}', ${card.specialCard?string})"</#if> <#if clickType == "random">onclick="<#if card.event>swapEvent<#elseif card.landmark>swapLandmark<#elseif card.project>swapProject<#elseif card.way>swapWay<#elseif card.ally>swapAlly<#elseif card.trait>swapTrait<#else>swapCard</#if>('${card.name?js_string}')"</#if>>
    <#if mobile>
        <#include "gameCardMobile.ftl">
    <#else>
        <#include "gameCardNormal.ftl">
    </#if>
</div>
