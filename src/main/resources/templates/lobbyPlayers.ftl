<!DOCTYPE html>
<html>
	<head>
		<title>Kingdom</title>
        <#include "commonIncludes.ftl">
        <script type="text/javascript" src="js/jquery-ui-1.8.custom.min.js"></script>
        <script type="text/javascript" src="js/toggle-select.js"></script>
        <link href="css/game.css" rel="stylesheet" type="text/css">
        <link href="css/jquery-ui-1.8.custom.css" rel="stylesheet" type="text/css">
        <script type="text/javascript">
            function sendPrivateChat(userId){
                var message = prompt("Enter your private message");
                if(message != null && message != ""){
                    $("#chatMessage").val("");
                    $.get("sendPrivateChat.html", {message: message, receivingUserId: userId}, function(data) {
                    });
                }
            }
        </script>
	</head>
	<body>
        <div class="topGradient"></div>
        <div style="padding-top:10px; padding-bottom:10px;">
            This page does not automatically refresh
        </div>
        <div>
            <h3>Players</h3>
            <table style="width:100%">
                <tr>
                    <td class="mediumLabel" style="width:40px;">Playing game?</td>
                    <td class="mediumLabel" style="width:50px; text-align:center">Chat</td>
                    <td class="mediumLabel">Player</td>
                </tr>
                <#list players as player>
                    <#if !player.expired && !player.invisible>
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
        </div>
        <#include "footer.ftl">
	</body>
</html>
