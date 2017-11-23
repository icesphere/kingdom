<table>
    <tr>
        <#list players as player>
            <td style="padding-right:10px;">
                <#if !mobile>Player ${player_index + 1}: </#if>${player.username}
                <#if showVictoryPoints>
                    <span style="color:green;"> (${player.victoryPoints} VP)</span>
                </#if>
            </td>
        </#list>
        <td style="padding-left:10px;"><a href="javascript:showGameInfo()">Game Info</a></td>
        <td style="padding-left:10px;"><a href="showLobbyPlayers.html" target="_blank">Lobby Players</a></td>
    </tr>
</table>