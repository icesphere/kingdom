<!DOCTYPE html>
<html>
	<head>
		<title>Confirm Random Cards</title>
        <#include "commonIncludes.ftl">
        <link href="css/game.css" rel="stylesheet" type="text/css">
        <#if mobile>
            <link href="css/gameMobile.css" rel="stylesheet" type="text/css">
        </#if>
        <script type="text/javascript">
            function swapCard(cardName) {
                document.location = "swapRandomCard.html?cardName="+cardName;
            }
        </script>
	</head>
	<body>
        <div class="topGradient"></div>
        <form action="confirmCards.html" method="POST" name="randomCardsForm" id="randomCardsForm">
            <div style="padding-bottom:5px;">
                Click a card to switch it out for a different one.
            </div>
            <#if mobile>
                <div style="padding-bottom:5px;">
                    <a href="showGameCards.html" target="_blank">Card Details</a>
                </div>
                <div>
                    <#assign clickType="random">
                    <#list cards as card>
                        <div style="float:left;padding-right:2px;padding-top:2px;">
                            <#if cards?size == 11 && card_index == 10>
                                <#assign baneCard = true>
                            <#else>
                                <#assign baneCard = false>
                            </#if>
                            <#include "gameCard.ftl">
                        </div>
                    </#list>
                </div>
            <#else>
                <table>
                    <tr>
                        <#assign clickType="random">
                        <#list cards as card>
                            <#if card_index == 5>
                                <#if cards?size == 11>
                                    <td style="vertical-align:bottom;text-align:center;">(Bane Card)</td>
                                </#if>
                                </tr>
                                <tr>
                            </#if>
                            <td style="vertical-align:top"><#include "gameCard.ftl"></td>
                        </#list>
                    </tr>
                </table>
            </#if>
            <#if includeColonyAndPlatinum>
                <div style="clear:both;padding-top:5px;">
                    Colony and Platinum will be included. <a href="togglePlatinumAndColony.html?include=false">Don't Include</a>
                </div>
            <#else>
                <div style="clear:both;padding-top:5px;">
                    Colony and Platinum will NOT be included. <a href="togglePlatinumAndColony.html?include=true">Include</a>
                </div>
            </#if>
            <#if includeShelters>
                <div style="clear:both;padding-top:5px;">
                    Shelters will replace starting Estates. <a href="toggleShelters.html?includeShelters=false">Don't Replace</a>
                </div>
            <#else>
                <div style="clear:both;padding-top:5px;">
                    Shelters will NOT replace starting Estates. <a href="toggleShelters.html?includeShelters=true">Replace Estates with Shelters</a>
                </div>
            </#if>
            <#if playTreasureCardsRequired>
                <div style="clear:both;padding-top:5px;">
                    Playing treasure cards will be required.
                </div>
            </#if>
            <div style="clear:both;padding-top:5px;">
                <table>
                    <tr>
                        <td><a href="keepRandomCards.html">Keep Cards</a></td>
                        <td style="padding-left:15px;"><a href="changeRandomCards.html">Change Cards</a></td>
                        <td style="padding-left:15px;"><a href="cancelCreateGame.html">Cancel</a></td>
                    </tr>
                </table>
            </div>
        </form>
        <#include "footer.ftl">
	</body>
</html>