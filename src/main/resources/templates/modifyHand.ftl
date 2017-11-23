<!DOCTYPE html>
<html>
<head>
    <title>Kingdom</title>
    <#include "commonIncludes.ftl">
    <script type="text/javascript" src="js/jquery-ui-1.8.custom.min.js"></script>
    <script type="text/javascript" src="js/toggle-select.js"></script>
    <link href="css/game.css" rel="stylesheet" type="text/css">
    <link href="css/jquery-ui-1.8.custom.css" rel="stylesheet" type="text/css">
    <script>
        function cancel() {
            document.location = "showGame.html";
        }
    </script>
</head>
<body>
<form method="POST" action="modifyHand.html">
    <table>
        <tr>
            <td>
                <h3>Modify Your Hand</h3>
                <#assign player = myPlayer>
                <#include "modifyPlayerHand.ftl">
            </td>
            <#list players as player>
                <#if player.userId != myPlayer.userId>
                    <td style="padding-left:30px;">
                        <h3>Modify ${player.username}'s Hand</h3>
                        <#include "modifyPlayerHand.ftl">
                    </td>
                </#if>
            </#list>
        </tr>
    </table>

    <div style="padding-top:20px;">
        <input type="submit" value="Submit"/>&#160&#160;<input type="button" value="Cancel" onclick="cancel()">
    </div>
</form>
</body>
</html>
