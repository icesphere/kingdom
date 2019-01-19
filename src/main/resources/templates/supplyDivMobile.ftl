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
    </#list>

    <#if events?has_content>
        <div style="float:left;padding-right:2px;padding-top:2px;font-size:14px;position:relative;top:2px;left:5px;width:74px;height:38px;">Events:</div>

        <#assign clickType="event">

        <#list events as card>
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