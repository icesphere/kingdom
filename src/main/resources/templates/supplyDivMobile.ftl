<div>
    <a href="showGameCards.html" target="_blank">Card Details</a>
</div>
<div style="clear:both;float:left;">
    <#assign clickType="supply">
    <#list kingdomCards as card>
        <div style="float:left;padding-right:2px;padding-top:2px;">
            <#if kingdomCards?size == 11 && card_index == 5>
                <#assign baneCard = true>
            <#else>
                <#assign baneCard = false>
            </#if>
            <#include "gameCard.ftl">
        </div>
    </#list>
    <#list supplyCards as card>
        <div style="float:left;padding-right:2px;padding-top:2px;">
            <#include "gameCard.ftl">
        </div>
        <#if gameStatus == "InProgress">
            <#if showEmbargoTokens && (embargoTokens(card.name))?? && embargoTokens(card.name) != 0><div style="font-size:10px; float: left; padding-right: 2px;">(${embargoTokens(card.name)} ET)</div></#if>
            <#if showTradeRouteTokens && tradeRouteTokenMap(card.name)><div style="font-size:10px; float: left; padding-right: 2px;">(TRT)</div></#if>
        </#if>
    </#list>
</div>
<#if showTradeRouteTokens>
    <div style="clear:both;">
        Trade Route Mat: ${tradeRouteTokensOnMat} token<#if tradeRouteTokensOnMat != 1>s</#if>
    </div>
</#if>