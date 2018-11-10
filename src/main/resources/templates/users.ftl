<!DOCTYPE html>
<html>
	<head>
		<title>Users</title>
        <#include "commonIncludes.ftl">
        <script type="text/javascript">
            function showPlayerStats(userId) {
                $('#playerStatsDiv').load('getPlayerStatsDivFromAdmin.html', {userId: userId}, function() {
                    showPlayerStatsDialog();
                });
            }

            function showPlayerStatsDialog() {
                $("#playerStatsDialog").dialog({
                    modal: false, width: 300, closeOnEscape: false, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
                });
            }

            function closePlayerStatsDialog() {
                $("#playerStatsDialog").dialog("destroy").remove();
            }
        </script>
	</head>
	<body>
        <#include "adminLinks.ftl">
		<h3>Users</h3>
		<table>
            <tr>
                <td style="padding-bottom:10px;">
                    <a href="showUser.html?id=0">Create New User</a>
                </td>
            </tr>
            <tr>
                <td style="font-weight:bold">Username</td>
                <td style="font-weight:bold;padding-left:10px;">Email</td>
                <td style="font-weight:bold;padding-left:10px;">Gender</td>
                <td style="font-weight:bold;padding-left:10px;">Logins</td>
                <td style="font-weight:bold;padding-left:10px;">Last Login</td>
                <td style="font-weight:bold;padding-left:10px;">Creation Date</td>
                <td style="font-weight:bold;padding-left:10px;">Stats</td>
                <td style="font-weight:bold;padding-left:10px;">Game History</td>
                <td style="font-weight:bold;padding-left:10px;">User Agent</td>
                <td style="font-weight:bold;padding-left:10px;">IP Address</td>
                <td style="font-weight:bold;padding-left:10px;">Location</td>
                <td style="font-weight:bold;padding-left:10px;">Base Set</td>
                <td style="font-weight:bold;padding-left:10px;">Intrigue</td>
                <td style="font-weight:bold;padding-left:10px;">Seaside</td>
                <td style="font-weight:bold;padding-left:10px;">Prosperity</td>
                <td style="font-weight:bold;padding-left:10px;">Cornucopia</td>
                <td style="font-weight:bold;padding-left:10px;">Hinterlands</td>
                <td style="font-weight:bold;padding-left:10px;">Promo</td>
                <td style="font-weight:bold;padding-left:10px;">Excluded Cards</td>
                <td style="font-weight:bold;padding-left:10px;">Username</td>
            </tr>
			<#list users as user>
				<tr>
					<td>
						<a href="showUser.html?id=${user.userId}">${user.username}</a>
					</td>
                    <td style="padding-left:10px;">
                        ${user.email}
                    </td>
                    <td style="padding-left:10px;">
                        ${user.gender}
                    </td>
                    <td style="padding-left:10px;">
                        ${user.logins}
                    </td>
					<td style="padding-left:10px;">
						${user.lastLogin}
					</td>
					<td style="padding-left:10px;">
						${user.creationDate}
					</td>
                    <td style="padding-left:10px;">
                        <a href="javascript:showPlayerStats(${user.userId})">Game Stats</a>
                    </td>
                    <td style="padding-left:10px;">
                        <a href="playerGameHistory.html?userId=${user.userId}">Game History</a>
                    </td>
                    <td style="padding-left:10px;">
                        ${user.userAgent}
                    </td>
                    <td style="padding-left:10px;">
                        ${user.ipAddress}
                    </td>
                    <td style="padding-left:10px;">
                        ${user.location}
                    </td>
                    <td style="padding-left:10px;">
                        base <#if user.baseChecked>(${user.baseWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        intrigue <#if user.intrigueChecked>(${user.intrigueWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        seaside <#if user.seasideChecked>(${user.seasideWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        prosperity <#if user.prosperityChecked>(${user.prosperityWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        cornucopia <#if user.cornucopiaChecked>(${user.cornucopiaWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        hinterlands <#if user.hinterlandsChecked>(${user.hinterlandsWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        dark ages <#if user.darkAgesChecked>(${user.darkAgesWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        promo <#if user.promoChecked>(${user.promoWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        ${user.excludedCards}
                    </td>
					<td style="padding-left:10px;">
						${user.username}
					</td>
				</tr>
			</#list>
		</table>
        <div id="playerStatsDiv">
        </div>
	</body>
</html>
		