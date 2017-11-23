<!DOCTYPE html>
<html>
	<head>
		<title>Kingdom</title>
        <#include "commonIncludes.ftl">
	</head>
	<body>
        <#include "adminLinks.ftl">
        <h3>Game History</h3>
        <table cellpadding="3" border="1">
            <tr>
                <th>ID</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Num Players</th>
                <th>Num Computer Players</th>
                <th>Cards</th>
                <th>Custom</th>
                <th>Mobile</th>
                <th>Show Victory Points</th>
                <th>Game End Reason</th>
                <th>Winner</th>
                <th>Game Log</th>
                <th>Player Info</th>
            </tr>
            <#assign rowClass = "oddRow">
            <#list games as game>
                <tr class="${rowClass}">
                    <td>${game.gameId}</td>
                    <td>${game.startDate}</td>
                    <td>${game.endDate}</td>
                    <td>${game.numPlayers}</td>
                    <td>${game.numComputerPlayers}</td>
                    <td>${game.cards}</td>
                    <td>${game.custom?string}</td>
                    <td>${game.mobile?string}</td>
                    <td>${game.showVictoryPoints?string}</td>
                    <td>${game.gameEndReason}</td>
                    <td>${game.winner}</td>
                    <td><a href="showGameLog.html?gameId=${game.gameId}" target="_blank">Show Log</a></td>
                    <td><a href="gamePlayersHistory.html?gameId=${game.gameId}">Players</a></td>
                </tr>
                <#if rowClass == "oddRow"><#assign rowClass = "evenRow"><#else><#assign rowClass = "oddRow"></#if>
            </#list>
        </table>
	</body>
</html>
