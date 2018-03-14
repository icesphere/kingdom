<div id="gameInfoDialog" class="gameInfoDialog" style="display:none" title="Game Info">
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
                                Draw Pile:
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
                                <#assign discardPileWidth = 3 * p.discard?size>
                                <div class="discardPile" style=";width:${discardPileWidth}px;">&#160;</div>
                            </td>
                        </tr>
                        <#if showVictoryCoins>
                            <tr>
                                <td class="gameInfoLabel">
                                    Victory Coins:
                                </td>
                                <td>
                                    ${p.victoryCoins}
                                </td>
                            </tr>
                        </#if>
                        <#if showSins>
                            <tr>
                                <td class="gameInfoLabel">
                                    Sins:
                                </td>
                                <td>
                                    ${p.sins}
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
                        <#if showCoinTokens>
                            <tr>
                                <td class="gameInfoLabel">
                                    Coin Tokens:
                                </td>
                                <td>
                                    ${p.coinTokens}
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
                        <#if showMuseumCards>
                            <tr>
                                <td class="gameInfoLabel">
                                    Museum Cards:
                                </td>
                                <td>
                                    ${p.museumCardsString}
                                </td>
                            </tr>
                        </#if>
                        <#if showCityPlannerCards>
                            <tr>
                                <td class="gameInfoLabel">
                                    City Planner Cards:
                                </td>
                                <td>
                                    ${p.cityPlannerCardsString}
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
                    </table>
                </td>
            </tr>
        </#list>
        <tr>
            <td style="text-align:center">
                <input type="button" onclick="closeGameInfoDialog()" value="Close">
            </td>
        </tr>
    </table>
</div>