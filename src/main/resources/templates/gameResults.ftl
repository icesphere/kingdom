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
                            <div class="gameResultsPlayerPoints">${player.cardCountByName("Province")}</div>
                            <div class="gameResultsPlayerPointsLabel">Duchies:</div>
                            <div class="gameResultsPlayerPoints">${player.cardCountByName("Duchy")}</div>
                            <div class="gameResultsPlayerPointsLabel">Estates:</div>
                            <div class="gameResultsPlayerPoints">${player.cardCountByName("Estate")}</div>
                            <div class="gameResultsPlayerPointsLabel">Curses:</div>
                            <div class="gameResultsPlayerPoints">${player.cardCountByName("Curse")}</div>
                            <#if showGarden>
                                <div class="gameResultsPlayerPointsLabel">Gardens:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Garden")}</div>
                            </#if>
                            <#if showFarmlands>
                                <div class="gameResultsPlayerPointsLabel">Farmlands:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Farmlands")}</div>
                            </#if>
                            <#if showGreatHall>
                                <div class="gameResultsPlayerPointsLabel">Great Halls:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Great Hall")}</div>
                            </#if>
                            <#if showHarem>
                                <div class="gameResultsPlayerPointsLabel">Harems:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Harem")}</div>
                            </#if>
                            <#if showDuke>
                                <div class="gameResultsPlayerPointsLabel">Dukes:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Duke")}</div>
                            </#if>
                            <#if showNobles>
                                <div class="gameResultsPlayerPointsLabel">Nobles:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Nobles")}</div>
                            </#if>
                            <#if showIslandCards>
                                <div class="gameResultsPlayerPointsLabel">Islands:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Island")}</div>
                            </#if>
                            <#if showVineyard>
                                <div class="gameResultsPlayerPointsLabel">Vineyards:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Vineyard")}</div>
                                <div class="gameResultsPlayerPointsLabel">Actions:</div>
                                <div class="gameResultsPlayerPoints">${player.numActions}</div>
                            </#if>
                            <#if showSilkRoads>
                                <div class="gameResultsPlayerPointsLabel">Silk Roads:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Silk Road")}</div>
                                <div class="gameResultsPlayerPointsLabel">Victory Cards:</div>
                                <div class="gameResultsPlayerPoints">${player.numVictoryCards}</div>
                            </#if>
                            <#if showFairgrounds>
                                <div class="gameResultsPlayerPointsLabel">Fairgrounds:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Fairgrounds")}</div>
                                <div class="gameResultsPlayerPointsLabel">Different Cards:</div>
                                <div class="gameResultsPlayerPoints">${player.numDifferentCards}</div>
                            </#if>
                            <#if showVictoryCoins>
                                <div class="gameResultsPlayerPointsLabel">Victory Coins:</div>
                                <div class="gameResultsPlayerPoints">${player.victoryCoins}</div>
                            </#if>
                            <div class="gameResultsPlayerPointsLabel">Cards:</div>
                            <div class="gameResultsPlayerPoints">${player.numCards}</div>
                            <div class="gameResultsPlayerPointsLabel">Turns:</div>
                            <div class="gameResultsPlayerPoints">${player.turns}</div>
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
                                ${player.allCardsString}
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