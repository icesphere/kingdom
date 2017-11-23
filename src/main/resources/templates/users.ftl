<!DOCTYPE html>
<html>
	<head>
		<title>Users</title>
        <#include "commonIncludes.ftl">
        <script type="text/javascript" src="js/jquery-ui-1.8.custom.min.js"></script>
        <link href="css/jquery-ui-1.8.custom.css" rel="stylesheet" type="text/css">
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
                <td style="font-weight:bold;padding-left:10px;">Alchemy</td>
                <td style="font-weight:bold;padding-left:10px;">Prosperity</td>
                <td style="font-weight:bold;padding-left:10px;">Cornucopia</td>
                <td style="font-weight:bold;padding-left:10px;">Hinterlands</td>
                <td style="font-weight:bold;padding-left:10px;">Promo</td>
                <td style="font-weight:bold;padding-left:10px;">Salvation</td>
                <td style="font-weight:bold;padding-left:10px;">Fairy Tale</td>
                <td style="font-weight:bold;padding-left:10px;">Leaders</td>  
                <td style="font-weight:bold;padding-left:10px;">Proletariat</td>
                <td style="font-weight:bold;padding-left:10px;">Fan Cards</td>
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
                        alchemy <#if user.alchemyChecked>(${user.alchemyWeight})</#if>
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
                        promo <#if user.promoChecked>(${user.promoWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        salvation <#if user.salvationChecked>(${user.salvationWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        fairy tale <#if user.fairyTaleChecked>(${user.fairyTaleWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        leaders <#if user.leadersChecked>(yes)</#if>
                    </td>    
                    <td style="padding-left:10px;">
                        proletariat <#if user.proletariatChecked>(${user.proletariatWeight})</#if>
                    </td>
                    <td style="padding-left:10px;">
                        other fan <#if user.otherFanCardsChecked>(${user.fanWeight})</#if>
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
		