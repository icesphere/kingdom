<div style="float:left; padding-bottom:10px;">
    <div style="float:right;">
        <a href="javascript:showGameInfo()">Game Info</a>
        &#160;&#160;&#160;<a href="javascript:exitGame();">Exit Game</a>
        <#if user.admin>
            &#160;&#160;&#160;<a href="admin.html">Admin</a>
        </#if>
    </div>

    <#if winnerString != "">
        <div style="clear:both;float:left;font-weight:bold;font-size:18px;color:green;">
            ${winnerString}
        </div>
    </#if>

    <div style="clear:both;float:left;">
        <span style="font-weight:bold;font-size:18px;">Game Over:</span> ${gameEndReason}
    </div>

    <div class="gameResults" style="clear:both;float:left;padding-top:10px;">
        <div style="clear:both;float:left;font-weight:bold;font-size:16px;">
            Points
        </div>
        <div style="clear:both;float:left;">
            <table>
                <#list players as player>
                    <tr>
                        <td style="padding-right:10px;font-weight:bold;text-align:right;<#if player.winner>color:green;</#if>">
                            ${player.username}
                        </td>
                        <td style="<#if player.winner>color:green;</#if>">
                            <div class="gameResultsPlayerPointsLabel">Total:</div>
                            <div class="gameResultsPlayerPoints">${player.finalVictoryPoints}</div>
                            <#if showColony>
                                <div class="gameResultsPlayerPointsLabel">Colonies:</div>
                                <div class="gameResultsPlayerPoints">${player.colonies}</div>
                            </#if>
                            <div class="gameResultsPlayerPointsLabel">Provinces:</div>
                            <div class="gameResultsPlayerPoints">${player.provinces}</div>
                            <div class="gameResultsPlayerPointsLabel">Duchies:</div>
                            <div class="gameResultsPlayerPoints">${player.duchies}</div>
                            <div class="gameResultsPlayerPointsLabel">Estates:</div>
                            <div class="gameResultsPlayerPoints">${player.estates}</div>
                            <div class="gameResultsPlayerPointsLabel">Curses:</div>
                            <div class="gameResultsPlayerPoints">${player.curses}</div>
                            <#if showGarden>
                                <div class="gameResultsPlayerPointsLabel">Gardens:</div>
                                <div class="gameResultsPlayerPoints">${player.gardens}</div>
                            </#if>
                            <#if showFarmlands>
                                <div class="gameResultsPlayerPointsLabel">Farmlands:</div>
                                <div class="gameResultsPlayerPoints">${player.farmlands}</div>
                            </#if>
                            <#if showGreatHall>
                                <div class="gameResultsPlayerPointsLabel">Great Halls:</div>
                                <div class="gameResultsPlayerPoints">${player.greatHalls}</div>
                            </#if>
                            <#if showHarem>
                                <div class="gameResultsPlayerPointsLabel">Harems:</div>
                                <div class="gameResultsPlayerPoints">${player.harems}</div>
                            </#if>
                            <#if showDuke>
                                <div class="gameResultsPlayerPointsLabel">Dukes:</div>
                                <div class="gameResultsPlayerPoints">${player.dukes}</div>
                            </#if>
                            <#if showNobles>
                                <div class="gameResultsPlayerPointsLabel">Nobles:</div>
                                <div class="gameResultsPlayerPoints">${player.nobles}</div>
                            </#if>
                            <#if showArchbishops>
                                <div class="gameResultsPlayerPointsLabel">Archbishops:</div>
                                <div class="gameResultsPlayerPoints">${player.archbishops}</div>
                            </#if>
                            <#if showIslandCards>
                                <div class="gameResultsPlayerPointsLabel">Islands:</div>
                                <div class="gameResultsPlayerPoints">${player.islands}</div>
                            </#if>
                            <div class="gameResultsPlayerPointsLabel">Cards:</div>
                            <div class="gameResultsPlayerPoints">${player.numCards}</div>
                            <div class="gameResultsPlayerPointsLabel">Turns:</div>
                            <div class="gameResultsPlayerPoints">${player.turns}</div>
                            <#if showVineyard>
                                <div class="gameResultsPlayerPointsLabel">Vineyards:</div>
                                <div class="gameResultsPlayerPoints">${player.vineyards}</div>
                                <div class="gameResultsPlayerPointsLabel">Actions:</div>
                                <div class="gameResultsPlayerPoints">${player.numActions}</div>
                            </#if>
                            <#if showSilkRoads>
                                <div class="gameResultsPlayerPointsLabel">Silk Roads:</div>
                                <div class="gameResultsPlayerPoints">${player.silkRoads}</div>
                                <div class="gameResultsPlayerPointsLabel">Victory Cards:</div>
                                <div class="gameResultsPlayerPoints">${player.numVictoryCards}</div>
                            </#if>
                            <#if showFairgrounds>
                                <div class="gameResultsPlayerPointsLabel">Fairgrounds:</div>
                                <div class="gameResultsPlayerPoints">${player.fairgrounds}</div>
                                <div class="gameResultsPlayerPointsLabel">Different Cards:</div>
                                <div class="gameResultsPlayerPoints">${player.numDifferentCards}</div>
                            </#if>
                            <#if showCathedral>
                                <div class="gameResultsPlayerPointsLabel">Cathedrals:</div>
                                <div class="gameResultsPlayerPoints">${player.cathedrals}</div>
                                <div class="gameResultsPlayerPointsLabel">Sins Removed:</div>
                                <div class="gameResultsPlayerPoints">${player.sinsRemoved}</div>
                                <div class="gameResultsPlayerPointsLabel">Curses Removed:</div>
                                <div class="gameResultsPlayerPoints">${player.cursesRemoved}</div>
                            </#if>
                            <#if showEnchantedPalace>
                                <div class="gameResultsPlayerPointsLabel">Enchanted Palaces:</div>
                                <div class="gameResultsPlayerPoints">${player.enchantedPalaces}</div>
                            </#if>
                            <#if showHedgeWizard>
                                <div class="gameResultsPlayerPointsLabel">Hedge Wizards:</div>
                                <div class="gameResultsPlayerPoints">${player.hedgeWizards}</div>
                            </#if>
                            <#if showGoldenTouch>
                                <div class="gameResultsPlayerPointsLabel">Golden Touches:</div>
                                <div class="gameResultsPlayerPoints">${player.goldenTouches}</div>
                            </#if>
                            <#if showVictoryCoins>
                                <div class="gameResultsPlayerPointsLabel">Victory Coins:</div>
                                <div class="gameResultsPlayerPoints">${player.victoryCoins}</div>
                            </#if>
                            <#if showSins>
                                <div class="gameResultsPlayerPointsLabel">Sins:</div>
                                <div class="gameResultsPlayerPoints">${player.sins}</div>
                            </#if>
                            <#if player.usingLeaders>
                                <div class="gameResultsPlayerPointsLabel">Points From Leaders:</div>
                                <div class="gameResultsPlayerPoints">${player.pointsFromLeaders}</div>
                            </#if>
                        </td>
                    </tr>
                </#list>
            </table>
        </div>
        <div style="clear:both;float:left;">
            <div style="clear:both;float:left;font-weight:bold;font-size:16px;padding-top:10px;">
                Cards
            </div>
            <div style="clear:both;float:left;">
                <table>
                    <#list players as player>
                        <tr>
                            <td style="padding-left:10px;padding-right:15px;font-weight:bold;text-align:right;">
                                ${player.username}
                            </td>
                            <td>
                                ${player.finalCards}
                            </td>
                        </tr>
                    </#list>
                </table>
            </div>
            <div style="clear:both;float:left;padding-left:5px;padding-top:15px;font-size:16px;">
                <a href="javascript:exitGame();">Exit Game</a>
            </div>
        </div>
        <#if showRepeatGameLink>
            <div style="clear:both;float:left;padding-left:5px;padding-top:15px;font-size:16px;">
                <a href="repeatGame.html">Play again with the same cards</a>
            </div>
        </#if>
        <div style="clear:both;float:left;padding-left:5px;padding-top:15px;font-size:16px;">
            <a href="showGameLog.html?logId=${logId}" target="_blank">Game Log</a>
        </div>
    </div>

    <#if !allComputerOpponents>
        <div style="clear:both;float:left;font-weight:bold;padding-top:10px;">Chat</div>
        <div id="chatDiv" style="clear:both;float:left;">
            <#include "chatDivMobile.ftl">
        </div>
        <div style="clear:both;float:left;" onkeypress="return checkEnterOnAddChat(event)">
            <input type="text" name="chatMessage" id="chatMessage" style="width:150px;"/> <input type="button" id="sendChatButton" value="Send" onclick="sendChat()"/>
        </div>
    </#if>

    <div style="clear:both; padding-top:20px;" class="label">Recent Game History:</div>
    <div id="historyDiv" style="clear:both;">
        <#include "historyDivMobile.ftl">
    </div>

</div>