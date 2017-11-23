<!DOCTYPE html>
<html>
	<head>
		<title>${title}</title>
        <#include "commonIncludes.ftl">
        <#if mobile>
            <link href="css/gameMobile.css" rel="stylesheet" type="text/css">
        <#else>
            <link href="css/game.css" rel="stylesheet" type="text/css">
        </#if>
        <#if mobile>
            <script type="text/javascript" >
                var mobile = true;
            </script>
        </#if>
        <script type="text/javascript" >
            var createGame = ${createGame?string};
        </script>
        <script type="text/javascript" src="js/selectCards.js"></script>
	</head>
	<body>
        <div class="topGradient"></div>
		<h3>${title}</h3>
        <form action="${action}" method="POST" name="selectCardOptionsForm">
            <input type="hidden" name="createGame" value="${createGame?string}"/>
            <div style="clear:both; float:left;">
                <#if createGame>
                    <div><span class="label">Players:</span></div>
                    <div style="clear:both;float:left;padding-right:20px;">
                        Player 1: You
                    </div>
                    <div style="float:left;padding-right:20px;">
                        Player 2: <select name="player2">
                            <option value="human" <#if user.player2Default == "human">selected</#if>>human</option>
                            <option value="computer_easy" <#if user.player2Default == "computer_easy">selected</#if>>computer (easy)</option>
                            <option value="computer_medium" <#if user.player2Default == "computer_medium">selected</#if>>computer (medium)</option>
                            <option value="computer_hard" <#if user.player2Default == "computer_hard">selected</#if>>computer (hard)</option>
                            <option value="computer_bmu" <#if user.player2Default == "computer_bmu">selected</#if>>computer (BMU)</option>
                        </select>
                    </div>
                    <div style="float:left;padding-right:20px;">
                        Player 3: <select name="player3">
                            <option value="human" <#if user.player3Default == "human">selected</#if>>human</option>
                            <option value="computer_easy" <#if user.player3Default == "computer_easy">selected</#if>>computer (easy)</option>
                            <option value="computer_medium" <#if user.player3Default == "computer_medium">selected</#if>>computer (medium)</option>
                            <option value="computer_hard" <#if user.player3Default == "computer_hard">selected</#if>>computer (hard)</option>
                            <option value="computer_bmu" <#if user.player3Default == "computer_bmu">selected</#if>>computer (BMU)</option>
                            <option value="none" <#if user.player3Default == "none">selected</#if>>none</option>
                        </select>
                    </div>
                    <div style="float:left;padding-right:20px;">
                        Player 4: <select name="player4">
                            <option value="human" <#if user.player4Default == "human">selected</#if>>human</option>
                            <option value="computer_easy" <#if user.player4Default == "computer_easy">selected</#if>>computer (easy)</option>
                            <option value="computer_medium" <#if user.player4Default == "computer_medium">selected</#if>>computer (medium)</option>
                            <option value="computer_hard" <#if user.player4Default == "computer_hard">selected</#if>>computer (hard)</option>
                            <option value="computer_bmu" <#if user.player4Default == "computer_bmu">selected</#if>>computer (BMU)</option>
                            <option value="none" <#if user.player4Default == "none">selected</#if>>none</option>
                        </select>
                    </div>
                    <div style="float:left;padding-right:20px;">
                        Player 5: <select name="player5">
                            <option value="human" <#if user.player5Default == "human">selected</#if>>human</option>
                            <option value="computer_easy" <#if user.player5Default == "computer_easy">selected</#if>>computer (easy)</option>
                            <option value="computer_medium" <#if user.player5Default == "computer_medium">selected</#if>>computer (medium)</option>
                            <option value="computer_hard" <#if user.player5Default == "computer_hard">selected</#if>>computer (hard)</option>
                            <option value="computer_bmu" <#if user.player5Default == "computer_bmu">selected</#if>>computer (BMU)</option>
                            <option value="none" <#if user.player5Default == "none">selected</#if>>none</option>
                        </select>
                    </div>
                    <div style="float:left;padding-right:20px;">
                        Player 6: <select name="player6">
                            <option value="human" <#if user.player6Default == "human">selected</#if>>human</option>
                            <option value="computer_easy" <#if user.player6Default == "computer_easy">selected</#if>>computer (easy)</option>
                            <option value="computer_medium" <#if user.player6Default == "computer_medium">selected</#if>>computer (medium)</option>
                            <option value="computer_hard" <#if user.player6Default == "computer_hard">selected</#if>>computer (hard)</option>
                            <option value="computer_bmu" <#if user.player6Default == "computer_bmu">selected</#if>>computer (BMU)</option>
                            <option value="none" <#if user.player6Default == "none">selected</#if>>none</option>
                        </select>
                    </div>
                    <div style="clear:both; padding-top:10px; font-size:12px;">
                        (BMU stands for the "Big Money Ultimate" strategy.  Go to <a href="http://simulatedominion.wordpress.com/strategies/big-money/" target="_blank">http://simulatedominion.wordpress.com/strategies/big-money/</a> for more information on how this strategy works.)
                    </div>
                    <div style="padding-top:10px;clear:both;">
                        <div class="label" style="float:left;">Options: </div><#if mobile><div style="float:left;padding-left:5px;"><a href="javascript:toggleOptions()" id="optionsLink">Show</a></div></#if>
                        <div id="options" <#if mobile>style="display:none"</#if>>
                            <div style="clear:both;">
                                <input type="checkbox" name="playTreasureCards" id="playTreasureCards" value="true" <#if user.alwaysPlayTreasureCards>checked</#if>/> <label for="playTreasureCards">Play Treasure Cards</label> (Default is to assume treasure cards in hand are always played)
                            </div>
                            <div>
                                (Cards marked with an * require treasure cards to be played.  If you select any of these cards, the Play Treasure Cards option will be used)
                            </div>
                            <div style="padding-top:10px;">
                                <input type="checkbox" name="showVictoryPoints" id="showVictoryPoints" value="true" <#if user.showVictoryPoints>checked</#if>/> Show Victory Points (points are calculated as you play and displayed next to each player's name)
                            </div>
                            <div style="padding-top:10px;">
                                <input type="checkbox" name="identicalStartingHands" id="identicalStartingHands" value="true" <#if user.identicalStartingHands>checked</#if>/> Identical Starting Hands (all players start with the same cards)
                            </div>
                            <div style="padding-top:10px;">
                                <span class="label">Game Title/Description: </span><input type="text" name="title" style="width:200px;"> (optional - this will be displayed for your game room in the lobby)
                            </div>
                            <div style="padding-top:10px;">
                                <input type="checkbox" name="privateGame" onclick="togglePrivateGame(this)" value="true"/> <span class="label">Private Game </span>
                                <div id="gamePasswordDiv" style="display:none;">Password: <input type="text" name="gamePassword" id="gamePassword" style="width:150px;"></div>
                            </div>
                        </div>
                    </div>
                </#if>
                <div style="padding-top:10px;float:left;clear:both;">
                    <div style="float:left;"><span class="label">Card Selection:</span></div>
                    <div style="clear:both;float:left;padding-left:10px;">
                        <input type="radio" name="generateType" value="random" onclick="toggleGenerateType()" checked="true"/> Random
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="radio" name="generateType" value="custom" onclick="toggleGenerateType()"/> Custom
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="radio" name="generateType" value="recommendedSet" onclick="toggleGenerateType()"/> Recommended Set
                    </div>
                    <#if recentGames?size != 0>
                        <div style="float:left;padding-left:10px;">
                            <input type="radio" name="generateType" value="recentGame" onclick="toggleGenerateType()"/> Cards from a recent game
                        </div>
                    </#if>
                    <div style="float:left;padding-left:10px;">
                        <input type="radio" name="generateType" value="annotatedGame" onclick="toggleGenerateType()"/> Annotated Game
                    </div>
                </div>
                <div id="decks" style="padding-top:10px;float:left;clear:both;">
                    <div id="decksLabel" style="float:left;"><span class="label">Decks:</span></div>
                    <div style="clear:both;float:left;padding-left:10px;">
                        <input type="checkbox" name="deck_kingdom" id="deck_kingdom" value="Kingdom" onclick="selectDeck(this)" <#if user.baseChecked>checked</#if>/> <label for="deck_kingdom">Base Set</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="deck_intrigue" id="deck_intrigue" value="Intrigue" onclick="selectDeck(this)" <#if user.intrigueChecked>checked</#if>/>  <label for="deck_intrigue">Intrigue</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="deck_seaside" id="deck_seaside" value="Seaside" onclick="selectDeck(this)" <#if user.seasideChecked>checked</#if>/>  <label for="deck_seaside">Seaside</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="deck_alchemy" id="deck_alchemy" value="Alchemy" onclick="selectDeck(this)" <#if user.alchemyChecked>checked</#if>/>  <label for="deck_alchemy">Alchemy</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="deck_prosperity" id="deck_prosperity" value="Prosperity" onclick="selectDeck(this)" <#if user.prosperityChecked>checked</#if>/>  <label for="deck_prosperity">Prosperity</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="deck_cornucopia" id="deck_cornucopia" value="Cornucopia" onclick="selectDeck(this)" <#if user.cornucopiaChecked>checked</#if>/>  <label for="deck_cornucopia">Cornucopia</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="deck_hinterlands" id="deck_hinterlands" value="Hinterlands" onclick="selectDeck(this)" <#if user.hinterlandsChecked>checked</#if>/>  <label for="deck_hinterlands">Hinterlands</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="promo_cards" id="promo_cards" value="true" onclick="selectDeck(this)" <#if user.promoChecked>checked</#if>/>  <label for="promo_cards">Promo Cards</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="deck_salvation" id="deck_salvation" value="Salvation" onclick="selectDeck(this)" <#if user.salvationChecked>checked</#if>/>  <label for="deck_salvation">Salvation</label> <a href="http://www.boardgamegeek.com/boardgame/80435/salvation-fan-expansion-for-dominion" target="_blank"><img style="border:0px" src="images/help.png" alt="salvation info"/></a>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="deck_fairytale" id="deck_fairytale" value="FairyTale" onclick="selectDeck(this)" <#if user.fairyTaleChecked>checked</#if>/>  <label for="deck_fairytale">Fairy Tale</label> <a href="http://boardgamegeek.com/boardgameexpansion/68281/fairy-tale-fan-expansion-for-dominion" target="_blank"><img style="border:0px" src="images/help.png" alt="fairy tale info"/></a>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="include_leaders" id="include_leaders" value="true" <#if user.leadersChecked>checked</#if>/>  <label for="deck_leaders">Leaders</label> <a href="http://www.boardgamegeek.com/article/7451160#7451160" target="_blank"><img style="border:0px" src="images/help.png" alt="leader info"/></a>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="deck_proletariat" id="deck_proletariat" value="Proletariat" onclick="selectDeck(this)" <#if user.proletariatChecked>checked</#if>/>  <label for="deck_proletariat">Proletariat</label> <a href="http://forum.dominionstrategy.com/index.php?topic=843.0" target="_blank"><img style="border:0px" src="images/help.png" alt="proletariat info"/></a>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input type="checkbox" name="other_fan_cards" id="other_fan_cards" value="true" onclick="selectDeck(this)" <#if user.otherFanCardsChecked>checked</#if>/>  <label for="other_fan_cards">Other Fan Cards</label>
                    </div>
                </div>
                <div id="decksWeight" style="padding-top:10px;float:left;clear:both;">
                    <div id="decksWeightLabel" style="float:left;"><span class="label">Deck Frequency:</span></div><#if mobile><div style="float:left;padding-left:5px;"><a href="javascript:toggleDeckFrequency()" id="deckFrequencyLink">Show</a></div></#if>
                    <div id="deckFrequency" <#if mobile>style="display:none"</#if>>
                        <div style="clear:both;float:left;padding-left:10px;">
                            Base Set <select name="deck_weight_kingdom" id="deck_weight_kingdom">
                                <option value="5" <#if user.baseWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.baseWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.baseWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.baseWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.baseWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Intrigue <select name="deck_weight_intrigue" id="deck_weight_intrigue">
                                <option value="5" <#if user.intrigueWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.intrigueWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.intrigueWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.intrigueWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.intrigueWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Seaside <select name="deck_weight_seaside" id="deck_weight_seaside">
                                <option value="5" <#if user.seasideWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.seasideWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.seasideWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.seasideWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.seasideWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Alchemy <select name="deck_weight_alchemy" id="deck_weight_alchemy">
                                <option value="5" <#if user.alchemyWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.alchemyWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.alchemyWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.alchemyWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.alchemyWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Prosperity <select name="deck_weight_prosperity" id="deck_weight_prosperity">
                                <option value="5" <#if user.prosperityWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.prosperityWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.prosperityWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.prosperityWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.prosperityWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Cornucopia <select name="deck_weight_cornucopia" id="deck_weight_cornucopia">
                                <option value="5" <#if user.cornucopiaWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.cornucopiaWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.cornucopiaWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.cornucopiaWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.cornucopiaWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Hinterlands <select name="deck_weight_hinterlands" id="deck_weight_hinterlands">
                                <option value="5" <#if user.hinterlandsWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.hinterlandsWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.hinterlandsWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.hinterlandsWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.hinterlandsWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Promo Cards <select name="deck_weight_promo" id="deck_weight_promo">
                                <option value="5" <#if user.promoWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.promoWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.promoWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.promoWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.promoWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Salvation <select name="deck_weight_salvation" id="deck_weight_salvation">
                                <option value="5" <#if user.salvationWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.salvationWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.salvationWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.salvationWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.salvationWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Fairy Tale <select name="deck_weight_fairytale" id="deck_weight_fairytale">
                                <option value="5" <#if user.fairyTaleWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.fairyTaleWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.fairyTaleWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.fairyTaleWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.fairyTaleWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Proletariat <select name="deck_weight_proletariat" id="deck_weight_proletariat">
                                <option value="5" <#if user.proletariatWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.proletariatWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.proletariatWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.proletariatWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.proletariatWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            Other Fan Cards <select name="deck_weight_fan" id="deck_weight_fan">
                                <option value="5" <#if user.fanWeight == 5>selected</#if>>Very Often</option>
                                <option value="4" <#if user.fanWeight == 4>selected</#if>>More Often</option>
                                <option value="3" <#if user.fanWeight == 3>selected</#if>>As Often</option>
                                <option value="2" <#if user.fanWeight == 2>selected</#if>>Less Often</option>
                                <option value="1" <#if user.fanWeight == 1>selected</#if>>Rarely</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div id="randomizingOptions" style="padding-top:10px;float:left;clear:both;">
                    <div style="float:left;"><span class="label">Randomizing Options:</span></div><#if mobile><div style="float:left;padding-left:5px;"><a href="javascript:toggleRandomizingOptions()" id="randomizingOptionsLink">Show</a></div></#if>
                    <div id="randomizingOptionsContent" <#if mobile>style="display:none"</#if>>
                        <div style="clear:both;float:left;padding-left:10px;">
                            <input type="checkbox" name="threeToFiveAlchemy" id="threeToFiveAlchemy" value="true"/> <label for="threeToFiveAlchemy">3-5 Alchemy cards when included</label>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            <input type="checkbox" name="oneOfEachCost" id="oneOfEachCost" value="true"/> <label for="oneOfEachCost">At least one card each of cost 2, 3, 4 and 5</label>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            <input type="checkbox" name="alwaysIncludeColonyAndPlatinum" id="alwaysIncludeColonyAndPlatinum" value="true"/> <label for="alwaysIncludeColonyAndPlatinum">Always Include Colony and Platinum</label>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            <input type="checkbox" name="oneWithBuy" id="oneWithBuy" value="true"/> <label for="oneWithBuy">At least one card with additional buys</label>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            <input type="checkbox" name="oneWithActions" id="oneWithActions" value="true"/> <label for="oneWithActions">At least one card with +2 or more actions</label>
                        </div>
                        <div style="float:left;padding-left:10px;">
                            <input type="checkbox" name="defenseForAttack" id="defenseForAttack" value="true"/> <label for="defenseForAttack">Include Moat, Lighthouse or Watchtower if there is an Attack card</label>
                        </div>
                    </div>
                </div>
                <div id="excludedCards" style="padding-top:10px;float:left;clear:both;">
                    <div style="float:left;"><span class="label">Excluded Cards:</span></div><div style="float:left;padding-left:5px;"><a href="javascript:toggleExcludedCards()" id="excludedCardsLink">Show</a></div>
                    <div id="excludedCardsContent" style="display:none;clear:both;float:left;padding-left:10px;">
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Base Set</div>
                            <#list kingdomCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Intrigue</div>
                            <#list intrigueCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Seaside</div>
                            <#list seasideCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Alchemy</div>
                            <#list alchemyCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Prosperity</div>
                            <#list prosperityCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Cornucopia</div>
                            <#list cornucopiaCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Hinterlands</div>
                            <#list hinterlandsCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Promo Cards</div>
                            <#list promoCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Salvation</div>
                            <#list salvationCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Fairy Tale</div>
                            <#list fairyTaleCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Proletariat</div>
                            <#list proletariatCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Other Fan Cards</div>
                            <#list fanCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "excludedCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                    </div>
                </div>
                <div id="customCards" style="display:none;float:left;clear:both;">
                    <div class="label" style="padding-top:10px;">
                        Choose Specific Cards:
                    </div>
                    <div style="clear:both;float:left;padding-left:10px;">
                        (Cards marked with an * require treasure cards to be played. If you select any of these cards, the Play Treasure Cards option will be used)
                    </div>
                    <div style="clear:both;float:left;">
                        <div style="float:left;padding-left:10px;color:red;">Cards selected:</div><div id="numCardsSelected" style="float:left; padding-left:5px; color:red">0</div><div style="float:left;padding-left:15px;color:black;">(remaining cards will be randomized based on selected decks)</div>
                    </div>
                    <div style="clear:both;float:left;padding-left:10px;">
                        <div id="kingdomCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Base Set</div>
                            <#list kingdomCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="intrigueCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Intrigue</div>
                            <#list intrigueCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="seasideCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Seaside</div>
                            <#list seasideCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="alchemyCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Alchemy</div>
                            <#list alchemyCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="prosperityCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Prosperity</div>
                            <div style="float:left;clear:both;">
                                <input type="checkbox" name="includeColonyAndPlatinumCards" id="includeColonyAndPlatinumCards" value="true"/> <label for="includeColonyAndPlatinumCards">Include Colony/Platinum</label>
                            </div>
                            <#list prosperityCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="cornucopiaCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Cornucopia</div>
                            <#list cornucopiaCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="hinterlandsCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Hinterlands</div>
                            <#list hinterlandsCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="promoCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Promo Cards</div>
                            <#list promoCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="salvationCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Salvation</div>
                            <#list salvationCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="fairyTaleCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Fairy Tale</div>
                            <#list fairyTaleCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="proletariatCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Proletariat</div>
                            <#list proletariatCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                        <div id="fanCardsDiv" class="customCardsDeck">
                            <div style="float:left;" class="label">Other Fan Cards</div>
                            <#list fanCards as card>
                                <div style="float:left;clear:both;">
                                    <#include "createGameCardRow.ftl">
                                </div>
                            </#list>
                        </div>
                    </div>
                </div>

                <div id="recommendedSets" style="float:left;clear:both;padding-top:10px;display:none;">
                    <div class="label" style="clear:both;float:left;">Choose cards from a recommended set:</div>
                    <div style="clear:both;float:left;">
                        <#assign currentDeck = "">
                        <select name="recommendedSetCards">
                            <#list recommendedSets as recommendedSet>
                                <#if currentDeck != recommendedSet.deck>
                                    <#if currentDeck != "">
                                        </optgroup>
                                    </#if>
                                    <optgroup label="${recommendedSet.deck}">
                                    <#assign currentDeck = recommendedSet.deck>
                                </#if>
                                <option value="${recommendedSet.cards}">${recommendedSet.name} - ${recommendedSet.cards}</option>
                            </#list>
                            </optgroup>
                        </select>
                    </div>
                </div>

                <div id="recentGames" style="float:left;clear:both;padding-top:10px;display:none;">
                    <div class="label" style="clear:both;float:left;">Choose cards from a recent game:</div>
                    <div style="clear:both;float:left;">
                        <select name="recentGameCards">
                            <#list recentGames as recentGame>
                                <option value="${recentGame.cards}">${recentGame.cards}</option>
                            </#list>
                        </select>
                    </div>
                </div>

                <div id="annotatedGames" style="float:left;clear:both;padding-top:10px;display:none;">
                    <div class="label" style="clear:both;float:left;">Choose Annotated Game:</div>
                    <div style="clear:both;float:left;">
                        (Go to <a href="http://dominionstrategy.com/category/annotated-games/" target="_blank">http://dominionstrategy.com/category/annotated-games/</a> for more information about the annotated games.)
                    </div>
                    <div style="clear:both;float:left;">
                        <select name="annotatedGameId">
                            <#list annotatedGames as annotatedGame>
                                <option value="${annotatedGame.gameId}">${annotatedGame.title}</option>
                            </#list>
                        </select>
                    </div>
                </div>

                <div style="clear:both;float:left;padding-top:10px; padding-right:10px;">
                    <a href="javascript:createGame()">Create</a>
                </div>

                <div style="float:left;padding-top:10px;">
                    <a href="cancelCreateGame.html">Cancel</a>
                </div>
            </div>
        </form>
        <#include "footer.ftl">
	</body>
</html>
