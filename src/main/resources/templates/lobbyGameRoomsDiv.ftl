<div style="padding-bottom: 10px;">
    <a href="showGamesInProgress.html" target="_blank">${numGamesInProgress} Game<#if numGamesInProgress != 1>s</#if> In Progress</a>
</div>
<div style="padding-bottom: 10px;">
    <#if maxGameRoomLimitReached>
        Max Game Room Limit Reached
    <#elseif !(user.gameId??)>
        <a href="createGame.html">Start a New Game</a>
    </#if>
</div>
<#list gameRooms as room>
    <div class="gameRoomDiv">
        <div class="topGradient" style="color:white"><div style="padding-top:5px; padding-left:10px;">${room.name}</div></div>
        <table class="gameRoom">
            <#assign gameStatus = room.game.status>
            <tr>
                <td>
                    <#if gameStatus == "BeingConfigured">
                        A game is being created by ${room.game.creatorName}
                    <#elseif gameStatus == "WaitingForPlayers">
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
            <#if user.admin || (user.userId == room.game.creatorId && user.gameId?? && user.gameId == room.gameId)>
                <tr>
                    <td>
                        <a href="cancelGame.html?gameId=${room.gameId}">Cancel Game</a>
                    </td>
                </tr>
            </#if>
        </table>
    </div>
</#list>