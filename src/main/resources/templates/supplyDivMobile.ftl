<div style="padding-bottom: 7px;">
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
    </#list>

    <#if eventsAndLandmarksAndProjectsAndWays?has_content>
        <div style="float:left;display:flex;justify-content: center; align-items: center; flex-direction: column;padding-right:2px;padding-top:2px;font-size:12px;position:relative;width:74px;height:38px;line-height:1;"><div>Other</div><div>Cards</div></div>

        <#list eventsAndLandmarksAndProjectsAndWays as card>

            <#if card.event>
                <#assign clickType="event">
            <#elseif card.landmark>
                <#assign clickType="landmark">
            <#elseif card.way>
                <#assign clickType="way">
            <#else>
                <#assign clickType="project">
            </#if>

            <div style="float:left;padding-right:2px;padding-top:2px;">
                <#include "gameCard.ftl">
            </div>
        </#list>
    </#if>
</div>

<#if showTradeRouteTokens>
    <div style="clear:both;">
        Trade Route Mat: ${tradeRouteTokensOnMat} token<#if tradeRouteTokensOnMat != 1>s</#if>
    </div>
</#if>