<h3>Players</h3>
<table style="width:100%">
    <tr>
        <td class="mediumLabel" style="width:40px;">Playing game?</td>
        <td class="mediumLabel" style="width:50px; text-align:center">Chat</td>
        <td class="mediumLabel">Player</td>
    </tr>
    <#list players as player>
        <#if !player.expired>
            <tr>
                <td>
                    <#if player.gameId??>
                        Yes
                    <#else>
                        No
                    </#if>
                </td>
                <td style="text-align:center">
                    <a href="javascript:sendPrivateChat(${player.userId})"><img src="images/PlayerChat.png" style="border:0;width:16px;height16px;"/></a>
                </td>
                <td>
                    ${player.username} <span class="playerStatus">${player.status}</span> <#if player.idle><span class="idlePlayer">(idle ${player.idleTime})</span></#if>
                </td>
            </tr>
        </#if>
    </#list>
</table>