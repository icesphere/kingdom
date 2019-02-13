<div style="float:left;clear:both;">
    <div class="handAreaTopRowLeft">
        <div style="float:left;">Deck:&#160;</div>
        <#assign n = player.deck?size>
        <#if n != 0>
            <#list 1..n as i>
                <div class="drawPileCard">&#160;</div>
            </#list>
        </#if>
    </div>
    <div class="handAreaTopRow">
        <#assign discardPileWidth = 3 * player.cardsInDiscard?size>
        <div style="float:left;">Discard Pile:&#160;</div>
        <div class="discardPile" style="float:left;width:${discardPileWidth}px;">&#160;</div>
    </div>

    <#if playTreasureCards && currentPlayerId == player.userId>
        <div class="handAreaTopRowLeft" style="clear:both;"><a href="javascript:playAllTreasureCards()">Play Treasure Cards</a></div>
    </#if>
    <#if showTavern>
        <div class="handAreaTopRowLeft" style="clear:both;"><#if player.tavernCards?size != 0><a href="javascript:showTavernCards()"></#if>${player.tavernCards?size} Tavern Card<#if player.tavernCards?size != 1>s</#if><#if player.tavernCards?size != 0></a></#if></div>
    </#if>
    <#if showIslandCards>
        <div class="handAreaTopRowLeft" style="clear:both;"><#if player.islandCards?size != 0><a href="javascript:showIslandCards()"></#if>${player.islandCards?size} Island Card<#if player.islandCards?size != 1>s</#if><#if player.islandCards?size != 0></a></#if></div>
    </#if>
    <#if showNativeVillage>
        <div class="handAreaTopRowLeft" style="clear:both;"><#if player.nativeVillageCards?size != 0><a href="javascript:showNativeVillageCards()"></#if>${player.nativeVillageCards?size} Native Village Card<#if player.nativeVillageCards?size != 1>s</#if><#if player.nativeVillageCards?size != 0></a></#if></div>
    </#if>
    <#if showPirateShipCoins>
        <div class="handAreaTopRowLeft" style="clear:both;">${player.pirateShipCoins} Pirate Ship Coin<#if player.pirateShipCoins != 1>s</#if></div>
    </#if>   
    <#if showCoffers>
        <div class="handAreaTopRowLeft" style="clear:both;"><a href="javascript:useCoffers()">${player.coffers} Coffers</a></div>
    </#if>
    <#if player.victoryCoins != 0>
        <div class="handAreaTopRowLeft" style="clear:both;">${player.victoryCoins} Victory Coin<#if player.victoryCoins != 1>s</#if></div>
    </#if>
    <#if showJourneyToken>
        <div class="handAreaTopRowLeft" style="clear:both;">Journey token: <#if player.journeyTokenFaceUp>Face up<#else>Face down</#if></div>
    </#if>
    <#if player.minusCoinTokenInFrontOfPlayer>
        <div class="handAreaTopRowLeft" style="clear:both;">-$1 token</div>
    </#if>
    <#if player.minusCardTokenOnDeck>
        <div class="handAreaTopRowLeft" style="clear:both;">-1 Card token</div>
    </#if>
    <#if player.plusCardTokenSupplyPile??>
        <div class="handAreaTopRowLeft" style="clear:both;">+1 Card token on: ${player.plusCardTokenSupplyPile}</div>
    </#if>
    <#if player.plusActionTokenSupplyPile??>
        <div class="handAreaTopRowLeft" style="clear:both;">+1 Action token on: ${player.plusActionTokenSupplyPile}</div>
    </#if>
    <#if player.plusBuyTokenSupplyPile??>
        <div class="handAreaTopRowLeft" style="clear:both;">+1 Buy token on: ${player.plusBuyTokenSupplyPile}</div>
    </#if>
    <#if player.plusCoinTokenSupplyPile??>
        <div class="handAreaTopRowLeft" style="clear:both;">+$1 token on: ${player.plusCoinTokenSupplyPile}</div>
    </#if>
    <#if player.minusTwoCostTokenSupplyPile??>
        <div class="handAreaTopRowLeft" style="clear:both;">-$2 cost token on: ${player.minusTwoCostTokenSupplyPile}</div>
    </#if>
    <#if player.trashingTokenSupplyPile??>
        <div class="handAreaTopRowLeft" style="clear:both;">Trashing token on: ${player.trashingTokenSupplyPile}</div>
    </#if>
    <#if player.inheritanceActionCard??>
        <div class="handAreaTopRowLeft" style="clear:both;">Estate token on: ${player.inheritanceActionCard.name}</div>
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