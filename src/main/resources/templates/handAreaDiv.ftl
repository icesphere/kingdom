<table cellpadding="0" cellspacing="0">
    <tr>
        <td class="handAreaTopRow" style="padding-left:50px;">
            <div style="float:left;">Deck:&#160;</div>
            <#assign n = player.deck?size>
            <#if n != 0>
                <#list 1..n as i>
                  <div class="drawPileCard">&#160;</div>
                </#list>
            </#if>
        </td>
        <td class="handAreaTopRow">
            <#assign discardPileWidth = 3 * player.cardsInDiscard?size>
            <div style="float:left;">Discard Pile:&#160;</div>
            <div class="discardPile" style="float:left;width:${discardPileWidth}px;">&#160;</div>
        </td>
        <#if playTreasureCards && currentPlayerId == player.userId>
            <td class="handAreaTopRow"><a href="javascript:playAllTreasureCards()">Play Treasure Cards</a></td>
        </#if>
        <#if showTavern>
            <td class="handAreaTopRow"><#if player.tavernCards?size != 0><a href="javascript:showTavernCards()"></#if>${player.tavernCards?size} Tavern Card<#if player.tavernCards?size != 1>s</#if><#if player.tavernCards?size != 0></a></#if></td>
        </#if>
        <#if showIslandCards>
            <td class="handAreaTopRow"><#if player.islandCards?size != 0><a href="javascript:showIslandCards()"></#if>${player.islandCards?size} Island Card<#if player.islandCards?size != 1>s</#if><#if player.islandCards?size != 0></a></#if></td>
        </#if>
        <#if showNativeVillage>
            <td class="handAreaTopRow"><#if player.nativeVillageCards?size != 0><a href="javascript:showNativeVillageCards()"></#if>${player.nativeVillageCards?size} Native Village Card<#if player.nativeVillageCards?size != 1>s</#if><#if player.nativeVillageCards?size != 0></a></#if></td>
        </#if>
        <#if showPirateShipCoins>
            <td class="handAreaTopRow">${player.pirateShipCoins} Pirate Ship Coin<#if player.pirateShipCoins != 1>s</#if></td>
        </#if>      
        <#if player.coffers != 0>
            <td class="handAreaTopRow"><a href="javascript:useCoffers()">${player.coffers} Coffers</a></td>
        </#if>
        <#if player.villagers != 0>
            <td class="handAreaTopRow"><a href="javascript:useVillagers()">${player.villagers} Villager<#if player.villagers != 1>s</#if></a></td>
        </#if>
        <#if player.victoryCoins != 0>
            <td class="handAreaTopRow">${player.victoryCoins} Victory Coin<#if player.victoryCoins != 1>s</#if></td>
        </#if>
        <#if showJourneyToken>
            <td class="handAreaTopRow">Journey token: <#if player.journeyTokenFaceUp>Face up<#else>Face down</#if></td>
        </#if>
        <#if player.minusCoinTokenInFrontOfPlayer>
            <td class="handAreaTopRow">-$1 token</td>
        </#if>
        <#if player.minusCardTokenOnDeck>
            <td class="handAreaTopRow">-1 Card token</td>
        </#if>
        <#if player.plusCardTokenSupplyPile??>
            <td class="handAreaTopRow">+1 Card token on: ${player.plusCardTokenSupplyPile}</td>
        </#if>
        <#if player.plusActionTokenSupplyPile??>
            <td class="handAreaTopRow">+1 Action token on: ${player.plusActionTokenSupplyPile}</td>
        </#if>
        <#if player.plusBuyTokenSupplyPile??>
            <td class="handAreaTopRow">+1 Buy token on: ${player.plusBuyTokenSupplyPile}</td>
        </#if>
        <#if player.plusCoinTokenSupplyPile??>
            <td class="handAreaTopRow">+$1 token on: ${player.plusCoinTokenSupplyPile}</td>
        </#if>
        <#if player.minusTwoCostTokenSupplyPile??>
            <td class="handAreaTopRow">-$2 cost token on: ${player.minusTwoCostTokenSupplyPile}</td>
        </#if>
        <#if player.trashingTokenSupplyPile??>
            <td class="handAreaTopRow">Trashing token on: ${player.trashingTokenSupplyPile}</td>
        </#if>
        <#if player.inheritanceActionCard??>
            <td class="handAreaTopRow">Estate token on: ${player.inheritanceActionCard.name}</td>
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