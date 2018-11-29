<!DOCTYPE html>
<html>
	<head>
		<title>Admin</title>
        <#include "commonIncludes.ftl">
	</head>
	<body>
        <#include "adminLinks.ftl">
		<table>
            <tr>
                <td>
                    Logged In Users: ${loggedInUsersCount}
                </td>
            </tr>
            <#if showGameActions>
                <tr>
                    <td>
                        <a href="cancelGame.html?gameId=${gameId}">Cancel Current Game</a>
                    </td>
                </tr>
                <tr>
                    <td>
                        <a href="clearAllPlayerActions.html?gameId=${gameId}">Clear all player actions</a>
                    </td>
                </tr>
                <tr>
                    <td>
                        <a href="endCurrentPlayersTurn.html?gameId=${gameId}">End current players turn</a>
                    </td>
                </tr>
                <tr>
                    <td>
                        <a href="showModifyHand.html">Modify Hand</a>
                    </td>
                </tr>
            </#if>
		</table>
	</body>
</html>
		