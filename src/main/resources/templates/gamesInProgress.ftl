<!DOCTYPE html>
<html>
	<head>
        <title>Kingdom - Games In Progress</title>
        <#include "commonIncludes.ftl">
	</head>
	<body>
        <div class="topGradient"></div>
        <div style="padding-top:10px; padding-bottom:10px;">
            This page does not automatically refresh
        </div>
        <div style="padding-bottom:15px;">
            <h3>Games In Progress</h3>
            <table style="width:100%">
                <tr>
                    <td class="mediumLabel" style="text-align:left;padding-left:20px;">Start Time</td>
                    <td class="mediumLabel" style="text-align:left;padding-left:20px;">Empty Piles</td>
                    <td class="mediumLabel" style="text-align:left;padding-left:20px;">Players</td>
                    <td class="mediumLabel" style="text-align:left;padding-left:20px;">Cards</td>
                </tr>
                <#list games as room>
                    <tr>
                        <td style="text-align:left;padding-left:20px;">${room.game.startTime}</td>
                        <td style="text-align:left;padding-left:20px;">${room.game.numEmptyPiles}</td>
                        <td style="text-align:left;padding-left:20px;">${room.game.playerList}</td>
                        <td style="text-align:left;padding-left:20px;">${room.game.cardList}</td>
                    </tr>
                </#list>
            </table>
        </div>
        <#include "footer.ftl">
	</body>
</html>
