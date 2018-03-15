<#if showNews>
    <div style="padding-bottom: 10px;">
        ${news}
    </div>
</#if>
<div style="padding-bottom: 10px;">
    <a href="showGamesInProgress.html" target="_blank">${numGamesInProgress} Game<#if numGamesInProgress != 1>s</#if> In Progress</a>
</div>
<div style="padding-bottom: 10px;">
    <#if maxGameRoomLimitReached>
        Max Game Room Limit Reached
    <#elseif !(user.gameId??)>
        <#if updatingWebsite>
            <div>
                ${updatingMessage}
            </div>
        <#else>
            <a href="createGame.html">Start a New Game</a>
        </#if>
    </#if>
</div>
<#list gameRooms as room>
    <div class="gameRoomDiv">
        <div class="topGradient" style="color:white"><div style="padding-top:5px; padding-left:10px;">${room.name}</div></div>
        <table class="gameRoom">
            <#assign gameStatus = room.game.status>
            <tr>
                <td>
                    <#if gameStatus == 1>
                        A game is being created by ${room.game.creatorName}
                    <#elseif gameStatus == 2>
                        <table>
                            <#if room.game.title != "">
                                <tr><td class="gameTitle">${room.game.title}</td></tr>
                            </#if>
                            <#if room.game.showVictoryPoints>
                                <tr><td>Show Victory Points enabled</td></tr>
                            </#if>
                            <#if room.game.identicalStartingHands>
                                <tr><td>Identical Starting Hands</td></tr>
                            </#if>
                            <tr><td><a href="javascript:openCardsDialog(${room.gameId})">Show Cards</a></td></tr>
                            <#list room.game.players as player>
                                <tr><td>${player.username}<#if user.userId == player.userId>&#160;-&#160;<a href="leaveGame.html">Leave Game</a></#if></td></tr>
                            </#list>
                            <#assign openPositions = room.game.numPlayers - room.game.players?size>
                            <#if openPositions != 0>
                                <tr><td>Waiting for ${openPositions?string} more Player<#if openPositions != 1>s</#if></td></tr>
                                <#if !(user.gameId??)>
                                    <#if room.game.privateGame>
                                        <tr><td>Private Game Password: <input type="password" name="gamePassword_${room.gameId}" id="gamePassword_${room.gameId}"/> <a href="javascript:joinPrivateGame('${room.gameId}')">Join Game</a></td></tr>
                                    <#else>
                                        <tr><td><a href="joinGame.html?gameId=${room.gameId}">Join Game</a></td></tr>
                                    </#if>
                                </#if>
                            </#if>
                        </table>
                    </#if>
                </td>
            </tr>
            <#if user.admin || (user.userId == room.game.creatorId && user.gameId == room.gameId)>
                <tr>
                    <td>
                        <a href="cancelGame.html?gameId=${room.gameId}">Cancel Game</a>
                    </td>
                </tr>
            </#if>
        </table>
    </div>
    <div id="cardsDialog_${room.gameId}" style="display:none" title="Kingdom Cards" class="oldCardAction">
        <#assign clickType="gameRoom">
        <#assign costDiscount = room.game.costDiscount>
        <#assign coinTokensPlayed = 0>
        <#assign actionCardDiscount = room.game.actionCardDiscount>
        <#assign actionCardsInPlay = room.game.actionCardsInPlay>
        <#if mobile>
            <div>
                <#list room.game.kingdomCards as card>
                    <div style="float:left;padding-right:2px;padding-top:2px;">
                        <#if room.game.kingdomCards?size == 11 && card_index == 10>
                            <#assign baneCard = true>
                        <#else>
                            <#assign baneCard = false>
                        </#if>
                        <#include "gameCard.ftl">
                    </div>
                </#list>
            </div>
        <#else>
            <table>
                <tr>
                    <#list room.game.kingdomCards as card>
                        <#if card_index == 5>
                            <#if room.game.kingdomCards?size == 11>
                                <td style="vertical-align:bottom;text-align:center;">(Bane Card)</td>
                            </#if>
                            </tr>
                            <tr>
                        </#if>
                        <td style="vertical-align:top"><#include "gameCard.ftl"></td>
                    </#list>
                </tr>
            </table>
        </#if>
        <#if room.game.includeColonyCards && room.game.includePlatinumCards>
            <div style="clear:both;padding-top:5px;">
                Colony and Platinum will be included.
            </div>
        </#if>
        <#if room.game.playTreasureCards>
            <div style="clear:both;padding-top:5px;">
                Playing treasure cards will be required.
            </div>
        </#if>
        <div style="clear:both; padding-top:10px; text-align:center">
            <input type="button" onclick="closeCardsDialog(${room.gameId})" value="Close">
        </div>
    </div>
</#list>