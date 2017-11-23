<div>
    <a href="showGameCards.html" target="_blank">Card Details</a>
</div>
<#if player.usingLeaders>
    <div>
        <a href="showLeaders.html" target="_blank">Leader Details</a>
    </div>
</#if>
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
    </#list>
</div>
<#if showTradeRouteTokens>
    <div style="clear:both;">
        Trade Route Mat: ${tradeRouteTokensOnMat} token<#if tradeRouteTokensOnMat != 1>s</#if>
    </div>
</#if>