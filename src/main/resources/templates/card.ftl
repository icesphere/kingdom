<!DOCTYPE html>
<html>
	<head>
		<title>Card</title>
        <#include "commonIncludes.ftl">
        <#if mobile>
            <link href="css/gameMobile.css" rel="stylesheet" type="text/css">
        <#else>
            <link href="css/game.css" rel="stylesheet" type="text/css">
        </#if>
        <script type="text/javascript">
            function saveCard(){
                document.forms["cardForm"].submit();
            }
        </script>
	</head>
	<body>
        <form action="saveCard.html" method="POST" name="cardForm">
            <input type="hidden" name="cardName" value="${card.name}"/>
            <table>
                <tr>
                    <td>
                        Deck:
                        <select name="deck">
                            <option value="Kingdom" <#if card.deck == "Kingdom">selected="true"</#if>>Base</option>
                            <option value="Intrigue" <#if card.deck == "Intrigue">selected="true"</#if>>Intrigue</option>
                            <option value="Seaside" <#if card.deck == "Seaside">selected="true"</#if>>Seaside</option>
                            <option value="Prosperity" <#if card.deck == "Prosperity">selected="true"</#if>>Prosperity</option>
                            <option value="Cornucopia" <#if card.deck == "Cornucopia">selected="true"</#if>>Cornucopia</option>
                            <option value="Hinterlands" <#if card.deck == "Hinterlands">selected="true"</#if>>Hinterlands</option>
                            <option value="Promo" <#if card.deck == "Promo">selected="true"</#if>>Promo Card</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>
                        Name: <input type="text" name="name" value="${card.name}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Type:
                        <select name="type">
                            <option value="1" <#if card.type.typeId == 1>selected="true"</#if>>Action</option>
                            <option value="2" <#if card.type.typeId == 2>selected="true"</#if>>Action - Attack</option>
                            <option value="4" <#if card.type.typeId == 4>selected="true"</#if>>Action - Reaction</option>
                            <option value="3" <#if card.type.typeId == 3>selected="true"</#if>>Victory</option>
                            <option value="5" <#if card.type.typeId == 5>selected="true"</#if>>Treasure</option>
                            <option value="6" <#if card.type.typeId == 6>selected="true"</#if>>Curse</option>
                            <option value="7" <#if card.type.typeId == 7>selected="true"</#if>>Action - Victory</option>
                            <option value="8" <#if card.type.typeId == 8>selected="true"</#if>>Treasure - Victory</option>
                            <option value="9" <#if card.type.typeId == 9>selected="true"</#if>>Action - Duration</option>
                            <option value="10" <#if card.type.typeId == 10>selected="true"</#if>>Treasure - Curse</option>
                            <option value="11" <#if card.type.typeId == 11>selected="true"</#if>>Victory - Reaction</option>
                            <option value="12" <#if card.type.typeId == 12>selected="true"</#if>>Duration - Victory</option>
                            <option value="14" <#if card.type.typeId == 14>selected="true"</#if>>Treasure - Reaction</option>
                            <option value="15" <#if card.type.typeId == 15>selected="true"</#if>>Action - Summon</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>
                        Cost:  <input type="text" name="cost" value="${card.cost}"/>
                    </td>
                </tr>   
                <tr>
                    <td>
                        <input type="checkbox" name="costIncludesPotion" value="true" <#if card.costIncludesPotion>checked</#if>/> Cost Includes Potion
                    </td>
                </tr>
                <tr>
                    <td>
                        Special:  <textarea cols="50" rows="3" name="special">${card.special}</textarea>
                    </td>
                </tr>
                <tr>
                    <td>
                        Add Cards:  <input type="text" name="addCards" value="${card.addCards}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Add Actions:  <input type="text" name="addActions" value="${card.addActions}"/>
                    </td>
                </tr>  
                <tr>
                    <td>
                        Add Buys:  <input type="text" name="addBuys" value="${card.addBuys}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Add Coins:  <input type="text" name="addCoins" value="${card.addCoins}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Victory Points:  <input type="text" name="victoryPoints" value="${card.victoryPoints}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Victory Coins:  <input type="text" name="addVictoryCoins" value="${card.addVictoryCoins}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Coin Tokens:  <input type="text" name="coinTokens" value="${card.coinTokens}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="testing" value="true" <#if card.testing>checked</#if>/> Testing
                    </td>
                </tr>  
                <tr>
                    <td>
                        <input type="checkbox" name="disabled" value="true" <#if card.disabled>checked</#if>/> Disabled
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="playTreasureCards" value="true" <#if card.playTreasureCards>checked</#if>/> Play Treasure Cards Required
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="prizeCard" value="true" <#if card.prizeCard>checked</#if>/> Prize Card
                    </td>
                </tr>
                <tr>
                    <td>
                        Font Size:  <input type="text" name="fontSize" value="${card.fontSize}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Name Lines:  <input type="text" name="nameLines" value="${card.nameLines}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Text Size:  <input type="text" name="textSize" value="${card.textSize}"/>
                    </td>
                </tr>
            </table>
            <table style="padding-top:10px;">
                <tr>
                    <td><a href="javascript:saveCard()">Save</a></td>
                    <td style="padding-left:10px;"><a href="listCards.html">Cancel</a></td>
                </tr>
            </table>
            <#if card.name != "">
                <div>
                    <#assign gameStatus = "InProgress">
                    <#assign clickType="admin">
                    <#assign costDiscount = 0>
                    <#assign coinTokensPlayed = 0>
                    <#assign actionCardDiscount = 0>
                    <#assign actionCardsInPlay = 0>
                    <#assign card_index = 1>
                    <div style="padding-top:10px;">
                        <#include "gameCard.ftl">
                    </div>
                </div>
            </#if>
        </form>
	</body>
</html>
