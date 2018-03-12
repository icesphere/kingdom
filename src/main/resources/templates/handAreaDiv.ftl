<table cellpadding="0" cellspacing="0">
    <tr>
        <td class="handAreaTopRow" style="padding-left:50px;">
            <div style="float:left;">Draw Pile:&#160;</div>
            <#assign n = player.deck?size>
            <#if n != 0>
                <#list 1..n as i>
                  <div class="drawPileCard">&#160;</div>
                </#list>
            </#if>
        </td>
        <td class="handAreaTopRow">
            <#assign discardPileWidth = 3 * player.discard?size>
            <div style="float:left;">Discard Pile:&#160;</div>
            <div class="discardPile" style="float:left;width:${discardPileWidth}px;">&#160;</div>
        </td>
        <#if playTreasureCards && currentPlayerId == player.userId>
            <td class="handAreaTopRow"><a href="javascript:playAllTreasureCards()">Play Treasure Cards</a> (+ ${player.autoPlayCoins} <span style="position:relative; top:3px;"><img src="images/coin.png" alt="Coins" style="height:16px; width:16px;"/></span>)</td>
        </#if>
        <#if showIslandCards>
            <td class="handAreaTopRow"><#if player.islandCards?size != 0><a href="javascript:showIslandCardsDialog()"></#if>${player.islandCards?size} Island Card<#if player.islandCards?size != 1>s</#if><#if player.islandCards?size != 0></a></#if></td>
        </#if>
        <#if showMuseumCards>
            <td class="handAreaTopRow"><#if player.museumCards?size != 0><a href="javascript:showMuseumCardsDialog()"></#if>${player.museumCards?size} Museum Card<#if player.museumCards?size != 1>s</#if><#if player.museumCards?size != 0></a></#if></td>
        </#if>
        <#if showCityPlannerCards>
            <td class="handAreaTopRow"><#if player.cityPlannerCards?size != 0><a href="javascript:showCityPlannerCardsDialog()"></#if>${player.cityPlannerCards?size} City Planner Card<#if player.cityPlannerCards?size != 1>s</#if><#if player.cityPlannerCards?size != 0></a></#if></td>
        </#if>
        <#if showNativeVillage>
            <td class="handAreaTopRow"><#if player.nativeVillageCards?size != 0><a href="javascript:showNativeVillageDialog()"></#if>${player.nativeVillageCards?size} Native Village Card<#if player.nativeVillageCards?size != 1>s</#if><#if player.nativeVillageCards?size != 0></a></#if></td>
        </#if>
        <#if showPirateShipCoins>
            <td class="handAreaTopRow">${player.pirateShipCoins} Pirate Ship Coin<#if player.pirateShipCoins != 1>s</#if></td>
        </#if>      
        <#if showFruitTokens>
            <#if player.fruitTokens gt 0>
                <td class="handAreaTopRow"><a href="javascript:useFruitTokens()">${player.fruitTokens} Fruit Token<#if player.fruitTokens != 1>s</#if></a></td>
            <#else>
                <td class="handAreaTopRow">${player.fruitTokens} Fruit Token<#if player.fruitTokens != 1>s</#if></td>
            </#if>
        </#if>    
        <#if showCattleTokens>
            <#if player.cattleTokens gt 1>
                <td class="handAreaTopRow"><a href="javascript:useCattleTokens()">${player.cattleTokens} Cattle Token<#if player.cattleTokens != 1>s</#if></a></td>
            <#else>
                <td class="handAreaTopRow">${player.cattleTokens} Cattle Token<#if player.cattleTokens != 1>s</#if></td>
            </#if>
        </#if>
        <#if showVictoryCoins>
            <td class="handAreaTopRow">${player.victoryCoins} Victory Coin<#if player.victoryCoins != 1>s</#if></td>
        </#if>
        <#if showSins>
            <td class="handAreaTopRow">${player.sins} Sin<#if player.sins != 1>s</#if></td>
        </#if>
    </tr>
</table>
<table style="width:100%">
    <tr>
        <td style="width:<#if showDuration>50<#else>80</#if>%">
            <table>
                <tr>
                    <td style="padding-right:5px;">
                        <img src="images/Hand.png" alt="Hand"/>
                    </td>
                    <td>
                        <div id="handDiv">
                            <#include "handDiv.ftl">
                        </div>
                    </td>
                </tr>
            </table>
        </td>
        <#if showDuration>
            <td style="width:35%">
                <table>
                    <tr>
                        <td style="padding-right:5px;">
                            <img src="images/Duration.png" alt="Duration"/>
                        </td>
                        <td>
                            <div id="durationDiv">
                                <#include "durationDiv.ftl">
                            </div>
                        </td>
                    </tr>
                </table>
            </td>
        </#if>
        <td style="width:<#if showDuration>15<#else>20</#if>%">
            <table>
                <tr>
                    <td>
                        <img src="images/Discard.png" alt="Discard"/>
                    </td>
                    <td>
                        <div id="discardDiv">
                            <#include "discardDiv.ftl">
                        </div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
<#if showIslandCards>
    <div id="islandCardsDiv">
    </div>
</#if>
<#if showMuseumCards>
    <div id="museumCardsDiv">
    </div>
</#if>
<#if showCityPlannerCards>
    <div id="cityPlannerCardsDiv">
    </div>
</#if>
<#if showNativeVillage>
    <div id="nativeVillageDiv">
    </div>
</#if>