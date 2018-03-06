<!DOCTYPE html>
<html>
	<head>
		<title>Confirm Random Cards</title>
        <#include "commonIncludes.ftl">
        <script type="text/javascript" src="js/jquery-ui-1.8.custom.min.js"></script>
        <script type="text/javascript" src="js/toggle-select.js"></script>
        <#if mobile>
            <link href="css/gameMobile.css" rel="stylesheet" type="text/css">
        <#else>
            <link href="css/game.css" rel="stylesheet" type="text/css">
        </#if>
        <link href="css/jquery-ui-1.8.custom.css" rel="stylesheet" type="text/css">
        <script type="text/javascript">
            var addingTypeOfCard = false;
            var cardType;
            function swapCard(cardName) {
                if(addingTypeOfCard) {
                    document.location = "swapForTypeOfCard.html?cardName="+cardName+"&cardType="+cardType;
                }
                else {
                    document.location = "swapRandomCard.html?cardName="+cardName;
                }
            }
            function addTypeOfCard() {
                addingTypeOfCard = true;
                $("#typeOfCardDialog").dialog({
                    modal: true, closeOnEscape: false, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
                });
            }
            function submitCardType() {
                cardType = $('input[name=cardType]:checked').val();
                if(!cardType) {
                    alert("You need to select a type of card to add");
                }
                else {
                    $("#typeOfCardDialog").dialog("close");
                    alert("Click on the card you want to replace");
                }
            }
            function cancelCardType() {
                addingTypeOfCard = false;
                $("#typeOfCardDialog").dialog("close");
            }
        </script>
	</head>
	<body>
        <div class="topGradient"></div>
        <form action="confirmCards.html" method="POST" name="randomCardsForm" id="randomCardsForm">
            <#if randomizerReplacementCardNotFound>
            <div style="color:red;padding-bottom:5px;">
                The card type you selected could not be found from cards in the decks you selected from the create game screen.  Try selecting more decks when creating a game.
            </div>
            </#if>
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
            <#if playTreasureCardsRequired>
                <div style="clear:both;padding-top:5px;">
                    Playing treasure cards will be required.
                </div>
            </#if>
            <div style="clear:both;padding-top:5px;">
                <a href="#" onclick="addTypeOfCard()">Add a specific type of card</a>
            </div>
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
        <div id="typeOfCardDialog" style="display:none;">
            <div style="float:left;font-size:14px;">
                <div style="float:left;">
                    Choose the type of card you want to add:
                </div>
                <div style="clear:both;margin-top:10px;float:left;">
                    <div style="float:left;padding-right:10px;">
                        <input type="radio" name="cardType" value="extraBuy"/> +1 Buy
                    </div>
                    <div style="float:left;padding-right:10px;">
                        <input type="radio" name="cardType" value="extraActions"/> +2 Actions
                    </div>
                    <div style="float:left;padding-right:10px;">
                        <input type="radio" name="cardType" value="treasure"/> Treasure
                    </div>
                    <div style="float:left;padding-right:10px;">
                        <input type="radio" name="cardType" value="reaction"/> Reaction
                    </div>
                    <div style="float:left;padding-right:10px;">
                        <input type="radio" name="cardType" value="attack"/> Attack
                    </div>
                    <div style="float:left;padding-right:10px;">
                        <input type="radio" name="cardType" value="trashingCard"/> Trashing Card
                    </div>
                </div>
                <div style="clear:both;margin-top:10px;float:left;">
                    <div style="float:left;">
                        <input type="button" onclick="submitCardType()" value="Submit"/>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="button" onclick="cancelCardType()" value="Cancel"/>
                    </div>
                </div>
            </div>
        </div>
        <#include "footer.ftl">
	</body>
</html>