<div style="float:left;clear:both;">
    <div class="handAreaTopRowLeft">
        <div style="float:left;">Draw Pile:&#160;</div>
        <#assign n = player.deck?size>
        <#if n != 0>
            <#list 1..n as i>
                <div class="drawPileCard">&#160;</div>
            </#list>
        </#if>
    </div>
    <div class="handAreaTopRow">
        <#assign discardPileWidth = 3 * player.discard?size>
        <div style="float:left;">Discard Pile:&#160;</div>
        <div class="discardPile" style="float:left;width:${discardPileWidth}px;">&#160;</div>
    </div>

    <#if playTreasureCards && currentPlayerId == player.userId>
        <div class="handAreaTopRowLeft" style="clear:both;"><a href="javascript:playAllTreasureCards()">Play Treasure Cards</a> (+ ${player.autoPlayCoins} <span style="position:relative; top:3px;"><img src="images/coin.png" alt="Coins" style="height:16px; width:16px;"/></span>)</div>
    </#if>
    <#if showIslandCards>
        <div class="handAreaTopRowLeft" style="clear:both;"><#if player.islandCards?size != 0><a href="javascript:showIslandCardsDialog()"></#if>${player.islandCards?size} Island Card<#if player.islandCards?size != 1>s</#if><#if player.islandCards?size != 0></a></#if></div>
    </#if>
    <#if showMuseumCards>
        <div class="handAreaTopRowLeft" style="clear:both;"><#if player.museumCards?size != 0><a href="javascript:showMuseumCardsDialog()"></#if>${player.museumCards?size} Museum Card<#if player.museumCards?size != 1>s</#if><#if player.museumCards?size != 0></a></#if></div>
    </#if>     
    <#if showCityPlannerCards>
        <div class="handAreaTopRowLeft" style="clear:both;"><#if player.cityPlannerCards?size != 0><a href="javascript:showCityPlannerCardsDialog()"></#if>${player.cityPlannerCards?size} City Planner Card<#if player.cityPlannerCards?size != 1>s</#if><#if player.cityPlannerCards?size != 0></a></#if></div>
    </#if>
    <#if showNativeVillage>
        <div class="handAreaTopRowLeft" style="clear:both;"><#if player.nativeVillageCards?size != 0><a href="javascript:showNativeVillageDialog()"></#if>${player.nativeVillageCards?size} Native Village Card<#if player.nativeVillageCards?size != 1>s</#if><#if player.nativeVillageCards?size != 0></a></#if></div>
    </#if>
    <#if showPirateShipCoins>
        <div class="handAreaTopRowLeft" style="clear:both;">${player.pirateShipCoins} Pirate Ship Coin<#if player.pirateShipCoins != 1>s</#if></div>
    </#if>   
    <#if showFruitTokens>
        <div class="handAreaTopRowLeft" style="clear:both;"><a href="javascript:useFruitTokens()">${player.fruitTokens} Fruit Token<#if player.fruitTokens != 1>s</#if></a></div>
    </#if>   
    <#if showCattleTokens>
        <div class="handAreaTopRowLeft" style="clear:both;"><a href="javascript:useCattleTokens()">${player.cattleTokens} Cattle Token<#if player.cattleTokens != 1>s</#if></a></div>
    </#if>
    <#if showVictoryCoins>
        <div class="handAreaTopRowLeft" style="clear:both;">${player.victoryCoins} Victory Coin<#if player.victoryCoins != 1>s</#if></div>
    </#if>
    <#if showSins>
        <div class="handAreaTopRowLeft" style="clear:both;">${player.sins} Sin<#if player.sins != 1>s</#if></div>
    </#if>
</div>
<div style="clear:both;">
    <div class="label">
        Hand:
    </div>
    <div id="handDiv">
        <#include "handDiv.ftl">
    </div>
    <#if showDuration>
        <div style="clear:both;" class="label">
            Duration:
        </div>
        <div id="durationDiv">
            <#include "durationDiv.ftl">
        </div>
    </#if>
    <div id="discardDiv">
        <#include "discardDivMobile.ftl">
    </div>
</div>
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