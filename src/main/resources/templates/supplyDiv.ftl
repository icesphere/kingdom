<div>
    <a href="showGameCards.html" target="_blank">Card Details</a>
</div>
<table>
    <tr>
        <#if !mobile>
            <td>
                <img src="images/Supply.png" alt="Supply"/>
            </td>
        </#if>
        <td>
            <table>
                <tr>
                    <#if kingdomCards?size == 11>
                        <td style="vertical-align:bottom;text-align:center;">(Bane Card)</td>
                    </#if>
                    <#list kingdomCards as card>
                        <#if card_index == 5>
                            </tr>
                            <tr>
                        </#if>
                        <#assign clickType="supply">
                        <td>
                            <table cellpadding="0" cellspacing="0">
                                <#if gameStatus == "InProgress">
                                    <tr><td style="font-size:10px;">${supply.get(card.pileName)}<#if showEmbargoTokens && embargoTokens.get(card.name)?? && embargoTokens.get(card.name) != 0> (${embargoTokens.get(card.name)} embargo token<#if embargoTokens.get(card.name) != 1>s</#if>)</#if><#if showTradeRouteTokens && tradeRouteTokenMap.get(card.name)??> (trade route token)</#if></td></tr>
                                </#if>
                                <tr><td><#include "gameCard.ftl"></td></tr>
                            </table>
                        </td>
                    </#list>
                </tr>
            </table>
        </td>
        <td>
            <table>
                <tr>
                    <#list supplyCards as card>
                        <#if (card_index == 3 && supplyCards?size == 7) || (card_index == 4 && supplyCards?size == 8) || (card_index == 4 && supplyCards?size == 9) || (card_index == 5 && supplyCards?size == 10)>
                            </tr>
                            <tr>
                        </#if>
                        <#assign clickType="supply">
                        <td>
                            <table cellpadding="0" cellspacing="0">
                                <#if gameStatus == "InProgress">
                                    <tr><td style="font-size:10px;">${supply.get(card.name)}<#if showEmbargoTokens && embargoTokens.get(card.name)?? && embargoTokens.get(card.name) != 0> (${embargoTokens.get(card.name)} embargo token<#if embargoTokens.get(card.name) != 1>s</#if>)</#if><#if showTradeRouteTokens && tradeRouteTokenMap.get(card.name)?? && tradeRouteTokenMap.get(card.name)> (trade route token)</#if></td></tr>
                                </#if>
                                <tr><td><#include "gameCard.ftl"></td></tr>
                            </table>
                        </td>
                    </#list>
                </tr>
            </table>
        </td>
        <#if events?has_content>
            <#assign clickType="event">
            <td>
                <table>
                    <tr><td style="font-weight: bold; font-size: 14px; padding-bottom: 3px;">Events</td></tr>
                    <tr>
                        <#list events as card>
                            <#if card_index == 1>
                                </tr>
                                <tr>
                            </#if>
                            <td>
                                <#include "gameCard.ftl">
                            </td>
                        </#list>
                        <#if events?size == 1>
                            <tr><td style="height: 110px;">&#160;</td></tr>
                        </#if>
                    </tr>
                </table>
            </td>
        </#if>
    </tr>
    <#if showTradeRouteTokens>
        <tr>
            <td colspan="3" style="padding-left: 45px;">Trade Route Mat: ${tradeRouteTokensOnMat} token<#if tradeRouteTokensOnMat != 1>s</#if></td>
        </tr>
    </#if>
</table>