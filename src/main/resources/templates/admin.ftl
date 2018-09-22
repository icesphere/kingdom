<!DOCTYPE html>
<html>
	<head>
		<title>Admin</title>
        <#include "commonIncludes.ftl">
        <script type="text/javascript">
            function setUpdatingWebsite()
            {
                var updatingWebsite = $("input[name=updatingWebsite]:checked").val();
                var updatingMessage = $("#updatingMessage").val();
                $.get("setUpdatingWebsite.html", {updatingWebsite: updatingWebsite, updatingMessage: updatingMessage});
            }
            function setShowNews()
            {
            var showNews = $("input[name=showNews]:checked").val();
            var news = $("#news").val();
            $.get("setShowNews.html", {showNews: showNews, news: news});
            }
        </script>
	</head>
	<body>
        <#include "adminLinks.ftl">
		<table>
            <tr>
                <td>
                    Logged In Users: ${loggedInUsersCount}
                </td>
            </tr>
			<tr>
				<td>
					<a href="listUsers.html">Users</a>
				</td>
			</tr>
            <tr>
                <td>
                    <a href="userStats.html">User Stats</a>
                </td>
            </tr>
            <tr>
                <td>
                    <a href="overallGameStats.html">Overall Stats</a>
                </td>
            </tr>
			<tr>
				<td>
					<a href="gameHistory.html">Game History</a>
				</td>
			</tr>
			<tr>
				<td>
					<a href="gameErrors.html">Game Errors (${numErrors})</a>
				</td>
			</tr>
            <tr>
                <td>
                    <a href="annotatedGames.html">Annotated Games</a>
                </td>
            </tr>
            <tr>
                <td>
                    <a href="recommendedSets.html">Recommended Sets</a>
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
            <tr>
                <td>
                    Updating Website: <input type="radio" name="updatingWebsite" value="true" onclick="setUpdatingWebsite()" <#if updatingWebsite>checked</#if>> Yes  <input type="radio" name="updatingWebsite" value="false" onclick="setUpdatingWebsite()" <#if !updatingWebsite>checked</#if>> No
                </td>
            </tr>
            <tr>
                <td>
                    <textarea name="updatingMessage" id="updatingMessage" cols="40" rows="5">The website needs to be updated with the latest changes.  Try coming back in about 15 minutes.</textarea>
                </td>
            </tr>
            <tr>
                <td>
                    Show News: <input type="radio" name="showNews" value="true" onclick="setShowNews()" <#if showNews>checked</#if>> Yes  <input type="radio" name="showNews" value="false" onclick="setShowNews()" <#if !showNews>checked</#if>> No
                </td>
            </tr>
            <tr>
                <td>
                    <textarea name="news" id="news" cols="40" rows="5">${news}</textarea>
                </td>
            </tr>
		</table>
	</body>
</html>
		