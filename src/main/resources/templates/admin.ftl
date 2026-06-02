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
            <#if loggedInUsers?has_content>
                <tr>
                    <td>
                        <table>
                            <tr>
                                <th align="left">User</th>
                                <th align="left">Status</th>
                                <th align="left">Game</th>
                                <th align="left">Action</th>
                            </tr>
                            <#list loggedInUsers as loggedInUser>
                                <tr>
                                    <td>${loggedInUser.username}</td>
                                    <td>${loggedInUser.status}</td>
                                    <td><#if loggedInUser.gameId??>${loggedInUser.gameId}<#else>-</#if></td>
                                    <td>
                                        <#if loggedInUser.userId != currentUserId>
                                            <a href="adminLogoutUser.html?userId=${loggedInUser.userId}">Logout user</a>
                                        <#else>
                                            -
                                        </#if>
                                    </td>
                                </tr>
                            </#list>
                        </table>
                    </td>
                </tr>
            </#if>
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
