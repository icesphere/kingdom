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
                </div>
                <div id="decks" style="padding-top:10px;float:left;clear:both;">
                    <div id="decksLabel" style="float:left;"><span class="label">Decks:</span></div>
                    <div style="clear:both;float: left;">
                        <#list decks as deck>
                            <div style="float:left;padding-left:10px;">
                                <input type="checkbox" name="deck_${deck.deckName}" id="deck_${deck.deckName}" value="${deck.deckName}" onclick="selectDeck(this)" <#if deck.deckChecked>checked</#if>/> <label for="deck_${deck.deckName}">${deck.displayName}</label>
                            </div>
                        </#list>
                    </div>
                </div>
                <div id="decksWeight" style="padding-top:10px;float:left;clear:both;">
                    <div id="decksWeightLabel" style="float:left;"><span class="label">Deck Frequency:</span></div><#if mobile><div style="float:left;padding-left:5px;"><a href="javascript:toggleDeckFrequency()" id="deckFrequencyLink">Show</a></div></#if>
                    <div id="deckFrequency" <#if mobile>style="display:none"<#else>style="clear:both"</#if>>
                        <#list decks as deck>
                            <div style="float:left;padding-left:10px;">
                                ${deck.displayName} <select name="deck_weight_${deck.deckName}" id="deck_weight_${deck.deckName}">
                                    <option value="5" <#if deck.deckWeight == 5>selected</#if>>Very Often</option>
                                    <option value="4" <#if deck.deckWeight == 4>selected</#if>>More Often</option>
                                    <option value="3" <#if deck.deckWeight == 3>selected</#if>>As Often</option>
                                    <option value="2" <#if deck.deckWeight == 2>selected</#if>>Less Often</option>
                                    <option value="1" <#if deck.deckWeight == 1>selected</#if>>Rarely</option>
                                </select>
                            </div>
                        </#list>
                    </div>
                </div>
                <div id="randomizingOptions" style="padding-top:10px;float:left;clear:both;">
                    <div style="float:left;"><span class="label">Randomizing Options:</span></div><#if mobile><div style="float:left;padding-left:5px;"><a href="javascript:toggleRandomizingOptions()" id="randomizingOptionsLink">Show</a></div></#if>
                    <div id="randomizingOptionsContent" <#if mobile>style="display:none"</#if>>
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
                        <#list decks as deck>
                            <div class="customCardsDeck">
                                <div style="float:left;" class="label">${deck.displayName}</div>
                                <#list deck.cards as card>
                                    <div style="float:left;clear:both;">
                                        <#include "excludedCardRow.ftl">
                                    </div>
                                </#list>
                            </div>
                        </#list>
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

                        <#list decks as deck>
                            <div class="customCardsDeck">
                                <div style="float:left;" class="label">${deck.displayName}</div>
                                <#list deck.cards as card>
                                    <div style="float:left;clear:both;">
                                        <#include "createGameCardRow.ftl">
                                    </div>
                                </#list>
                            </div>
                        </#list>

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
