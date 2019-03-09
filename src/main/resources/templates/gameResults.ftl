<!DOCTYPE html>
<html>

<head>
    <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <META HTTP-EQUIV="Expires" CONTENT="-1">
    <title>Kingdom - Game Over</title>
    <#include "gameIncludes.ftl">
</head>

<body>

<div id="gameInfoDiv" style="display: none;>
    <#include "gameInfoDiv.ftl">
</div>

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
                            <div class="gameResultsPlayerPoints">${player.finalVictoryPoints} VP</div>

                            <#list victoryCards as card>
                                <#if player.cardCountByName(card.name) != 0>
                                    <div class="gameResultsPlayerPointsLabel">${card.name}:</div>
                                    <div class="gameResultsPlayerPoints">
                                        ${player.cardCountByName(card.name)} (<#if card.victoryPointsCalculator>${player.cardCountByName(card.name) * card.calculatePoints(player)}<#else>${player.cardCountByName(card.name) * card.victoryPoints}</#if> VP)
                                    </div>
                                </#if>
                            </#list>

                            <#list scoringLandmarks as card>
                                <div class="gameResultsPlayerPointsLabel">${card.name}:</div>
                                <div class="gameResultsPlayerPoints">${card.calculatePoints(player)} VP</div>
                            </#list>

                            <#if player.cardCountByName("Curse") != 0>
                                <div class="gameResultsPlayerPointsLabel">Curses:</div>
                                <div class="gameResultsPlayerPoints">${player.cardCountByName("Curse")} (${player.cardCountByName("Curse") * -1} VP)</div>
                            </#if>

                            <#if player.victoryCoins != 0>
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
                            <td style="padding-bottom: 10px;padding-left:10px;padding-right:15px;font-weight:bold;text-align:right;">
                                ${player.username}
                            </td>
                            <td style="padding-bottom: 10px;">
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
        <#include "historyDiv.ftl">
    </div>

</div>

</body>

</html>