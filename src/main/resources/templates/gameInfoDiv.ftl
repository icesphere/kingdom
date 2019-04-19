<div class="gameInfoDivContent">

    <div style="padding-bottom: 10px; font-size: 14px; font-weight: bold;">Game Info</div>

    <div style="clear: both; float: left;">

        <table>
            <tr>
                <td style="padding-bottom:10px;">
                    <table>
                        <tr>
                            <td colspan="2" style="text-align:center; font-weight:bold; font-size:20px;">
                                Game
                            </td>
                        </tr>
                        <tr>
                            <td class="gameInfoLabel">
                                Trashed Cards:
                            </td>
                            <td>
                                ${trashedCards}
                            </td>
                        </tr>
                        <#if showPrizeCards>
                            <tr>
                                <td class="gameInfoLabel">
                                    Available Prizes:
                                </td>
                                <td>
                                    ${prizeCards}
                                </td>
                            </tr>
                        </#if>
                    </table>
                </td>
            </tr>
            <#list players as p>
                <tr>
                    <td style="padding-bottom:10px;">
                        <table>
                            <tr>
                                <td colspan="2" style="text-align:center; font-weight:bold; font-size:18px;">
                                    <#if player.userId == p.userId>
                                        You
                                    <#else>
                                        ${p.username}
                                    </#if>
                                </td>
                            </tr>
                            <tr>
                                <td class="gameInfoLabel">
                                    Hand:
                                </td>
                                <td>
                                    ${p.hand?size} Card<#if p.hand?size != 1>s</#if>
                                </td>
                            </tr>
                            <tr>
                                <td class="gameInfoLabel">
                                    Deck:
                                </td>
                                <td>
                                    <#assign drawPileWidth = 3 * p.deck?size>
                                    <div class="discardPile" style=";width:${drawPileWidth}px;">&#160;</div>
                                </td>
                            </tr>
                            <tr>
                                <td class="gameInfoLabel">
                                    Discard Pile:
                                </td>
                                <td>
                                    <#assign discardPileWidth = 3 * p.cardsInDiscard?size>
                                    <div class="discardPile" style=";width:${discardPileWidth}px;">&#160;</div>
                                </td>
                            </tr>
                            <#if p.victoryCoins != 0>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Victory Coins:
                                    </td>
                                    <td>
                                        ${p.victoryCoins}
                                    </td>
                                </tr>
                            </#if>
                            <#if showPirateShipCoins>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Pirate Ship Coins:
                                    </td>
                                    <td>
                                        ${p.pirateShipCoins}
                                    </td>
                                </tr>
                            </#if>
                            <#if p.coffers != 0>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Coffers:
                                    </td>
                                    <td>
                                        ${p.coffers}
                                    </td>
                                </tr>
                            </#if>
                            <#if p.villagers != 0>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Villagers:
                                    </td>
                                    <td>
                                        ${p.villagers}
                                    </td>
                                </tr>
                            </#if>
                            <#if p.sinisterPlotTokens != 0>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Sinister Plot Tokens:
                                    </td>
                                    <td>
                                        ${p.sinisterPlotTokens}
                                    </td>
                                </tr>
                            </#if>
                            <#if showIslandCards>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Island Cards:
                                    </td>
                                    <td>
                                        ${p.islandCardsString}
                                    </td>
                                </tr>
                            </#if>
                            <#if showTavern>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Tavern Cards:
                                    </td>
                                    <td>
                                        ${p.tavernCardsString}
                                    </td>
                                </tr>
                            </#if>
                            <#if showNativeVillage>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Native Village Cards:
                                    </td>
                                    <td>
                                        <#assign nativeVillagePileWidth = 3 * p.nativeVillageCards?size>
                                        <div class="discardPile" style=";width:${nativeVillagePileWidth}px;">&#160;</div>
                                    </td>
                                </tr>
                            </#if>
                            <#if showDuration>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Duration Cards:
                                    </td>
                                    <td>
                                        ${p.durationCardsString}
                                    </td>
                                </tr>
                            </#if>
                            <#if showJourneyToken>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Journey token:
                                    </td>
                                    <td>
                                        <#if p.journeyTokenFaceUp>Face up<#else>Face down</#if>
                                    </td>
                                </tr>
                            </#if>
                            <#if p.minusCoinTokenInFrontOfPlayer>
                                <tr>
                                    <td class="gameInfoLabel">
                                        -$1 token:
                                    </td>
                                    <td>
                                        Yes
                                    </td>
                                </tr>
                            </#if>
                            <#if p.minusCardTokenOnDeck>
                                <tr>
                                    <td class="gameInfoLabel">
                                        -1 Card token on deck:
                                    </td>
                                    <td>
                                        Yes
                                    </td>
                                </tr>
                            </#if>
                            <#if p.plusCardTokenSupplyPile??>
                                <tr>
                                    <td class="gameInfoLabel">
                                        +1 Card token on:
                                    </td>
                                    <td>
                                        ${p.plusCardTokenSupplyPile}
                                    </td>
                                </tr>
                            </#if>
                            <#if p.plusActionTokenSupplyPile??>
                                <tr>
                                    <td class="gameInfoLabel">
                                        +1 Action token on:
                                    </td>
                                    <td>
                                        ${p.plusActionTokenSupplyPile}
                                    </td>
                                </tr>
                            </#if>
                            <#if p.plusBuyTokenSupplyPile??>
                                <tr>
                                    <td class="gameInfoLabel">
                                        +1 Buy token on:
                                    </td>
                                    <td>
                                        ${p.plusBuyTokenSupplyPile}
                                    </td>
                                </tr>
                            </#if>
                            <#if p.plusCoinTokenSupplyPile??>
                                <tr>
                                    <td class="gameInfoLabel">
                                        +$1 token on:
                                    </td>
                                    <td>
                                        ${p.plusCoinTokenSupplyPile}
                                    </td>
                                </tr>
                            </#if>
                            <#if p.minusTwoCostTokenSupplyPile??>
                                <tr>
                                    <td class="gameInfoLabel">
                                        -$2 cost token on:
                                    </td>
                                    <td>
                                        ${p.minusTwoCostTokenSupplyPile}
                                    </td>
                                </tr>
                            </#if>
                            <#if p.trashingTokenSupplyPile??>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Trashing token on:
                                    </td>
                                    <td>
                                        ${p.trashingTokenSupplyPile}
                                    </td>
                                </tr>
                            </#if>
                            <#if p.inheritanceActionCard??>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Estate token on:
                                    </td>
                                    <td>
                                        ${p.inheritanceActionCard.name}
                                    </td>
                                </tr>
                            </#if>
                            <#if p.projectsString?has_content>
                                <tr>
                                    <td class="gameInfoLabel">
                                        Projects:
                                    </td>
                                    <td>
                                        ${p.projectsString}
                                    </td>
                                </tr>
                            </#if>
                        </table>
                    </td>
                </tr>
            </#list>
        </table>

    </div>

    <div style="clear: both; padding-top: 20px;">
        <button onClick="hideGameInfoDiv()">Close</button>
    </div>

</div>