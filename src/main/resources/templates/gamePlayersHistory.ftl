<!DOCTYPE html>
<html>
	<head>
		<title>Kingdom</title>
        <#include "commonIncludes.ftl">
        <link href="css/game.css" rel="stylesheet" type="text/css">
	</head>
	<body>
        <#include "adminLinks.ftl">
        <h3>Game Players History</h3>
        <table cellpadding="3" border="1">
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Points</th>
                <th>Winner</th>
                <th>Quit</th>
                <th>Cards</th>
                <th>Victory Coins</th>
                <th>Turns</th>
            </tr>
            <#list players as player>
                <tr>
                    <td>${player.userId}</td>
                    <td>${player.username}</td>
                    <td>${player.points}</td>
                    <td>${player.winner?string}</td>
                    <td>${player.quit?string}</td>
                    <td>${player.cards}</td>
                    <td>${player.victoryCoins}</td>
                    <td>${player.turns}</td>
                </tr>
            </#list>
        </table>
        <!-- todo
        <div style="padding-top:10px;">
            <a href="gamePlayersHistory.html?gameId=${gameId - 1}">Previous Game</a>
        </div>
        <div style="padding-top:10px;">
            <a href="gamePlayersHistory.html?gameId=${gameId + 1}">Next Game</a>
        </div>
        -->
        <div style="padding-top:10px;">
            <a href="gameHistory.html">Return to Game History</a>
        </div>
	</body>
</html>
