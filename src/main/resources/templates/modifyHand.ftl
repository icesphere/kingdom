<!DOCTYPE html>
<html>
<head>
    <title>Kingdom</title>
    <#include "commonIncludes.ftl">
    <link href="css/game.css" rel="stylesheet" type="text/css">
    <script>
        function cancel() {
            document.location = "showGame.html";
        }

        function updateModifyCardsText(playerId) {
            var modifyCardsChoice = document.getElementsByName("modifyCardsChoice_" + playerId)[0].value;
            var modifyingCardsInPlay = modifyCardsChoice == "inPlay";

            document.getElementById("keepCardsText_" + playerId).innerHTML = modifyingCardsInPlay ? "Keep cards in play" : "Keep cards in hand";
            document.getElementById("discardCardsText_" + playerId).innerHTML = modifyingCardsInPlay ? "Discard cards in play" : "Discard cards in hand";
            document.getElementById("trashCardsText_" + playerId).innerHTML = modifyingCardsInPlay ? "Trash cards in play" : "Trash cards in hand";
            document.getElementById("removeCardsText_" + playerId).innerHTML = modifyingCardsInPlay ? "Remove cards in play" : "Remove cards in hand";
            document.getElementById("addCardsText_" + playerId).innerHTML = modifyingCardsInPlay ? "Add Cards To In Play:" : "Add Cards To Hand:";
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
