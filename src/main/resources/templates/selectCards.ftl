<!DOCTYPE html>
<html>
	<head>
		<title>${title}</title>
        <#include "commonIncludes.ftl">
        <#if mobile>
            <link href="css/game.css?1" rel="stylesheet" type="text/css">
            <link href="css/gameMobile.css?1" rel="stylesheet" type="text/css">
        <#else>
            <link href="css/game.css?1" rel="stylesheet" type="text/css">
        </#if>
        <#if mobile>
            <script type="text/javascript" >
                var mobile = true;
            </script>
        </#if>
        <script type="text/javascript" >
            var createGame = ${createGame?string};
        </script>
        <script type="text/javascript" src="js/selectCards.js?10"></script>
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
                            <option value="human">human</option>
                            <option value="computer_easy">computer (easy)</option>
                            <option value="computer_medium">computer (medium)</option>
                            <option value="computer_hard">computer (hard)</option>
                            <option value="computer_bmu" selected>computer (BMU)</option>
                        </select>
                    </div>
                    <div style="float:left;padding-right:20px;">
                        Player 3: <select name="player3">
                            <option value="human">human</option>
                            <option value="computer_easy">computer (easy)</option>
                            <option value="computer_medium">computer (medium)</option>
                            <option value="computer_hard" selected>computer (hard)</option>
                            <option value="computer_bmu">computer (BMU)</option>
                            <option value="none">none</option>
                        </select>
                    </div>
                    <div style="float:left;padding-right:20px;">
                        Player 4: <select name="player4">
                            <option value="human">human</option>
                            <option value="computer_easy">computer (easy)</option>
                            <option value="computer_medium">computer (medium)</option>
                            <option value="computer_hard">computer (hard)</option>
                            <option value="computer_bmu">computer (BMU)</option>
                            <option value="none" selected>none</option>
                        </select>
                    </div>
                    <div style="float:left;padding-right:20px;">
                        Player 5: <select name="player5">
                            <option value="human">human</option>
                            <option value="computer_easy">computer (easy)</option>
                            <option value="computer_medium">computer (medium)</option>
                            <option value="computer_hard">computer (hard)</option>
                            <option value="computer_bmu">computer (BMU)</option>
                            <option value="none" selected>none</option>
                        </select>
                    </div>
                    <div style="float:left;padding-right:20px;">
                        Player 6: <select name="player6">
                            <option value="human">human</option>
                            <option value="computer_easy">computer (easy)</option>
                            <option value="computer_medium">computer (medium)</option>
                            <option value="computer_hard">computer (hard)</option>
                            <option value="computer_bmu">computer (BMU)</option>
                            <option value="none" selected>none</option>
                        </select>
                    </div>
                    <div style="clear:both; padding-top:10px; font-size:12px;">
                        (BMU stands for the "Big Money Ultimate" strategy.  Go to <a href="http://simulatedominion.wordpress.com/strategies/big-money/" target="_blank">http://simulatedominion.wordpress.com/strategies/big-money/</a> for more information on how this strategy works.)
                    </div>
                    <div style="padding-top:10px;clear:both;">
                        <div class="label" style="float:left;">Options: </div><#if mobile><div style="float:left;padding-left:5px;"><a href="javascript:toggleOptions()" id="optionsLink">Show</a></div></#if>
                        <div id="options" <#if mobile>style="display:none"</#if>>
                            <div style="clear: both; padding-top: 10px;">
                                <input type="checkbox" name="showVictoryPoints" id="showVictoryPoints" value="true"/> Show Victory Points (points are calculated as you play and displayed next to each player's name)
                            </div>
                            <div style="padding-top:10px;">
                                <input type="checkbox" name="identicalStartingHands" id="identicalStartingHands" value="true"/> Identical Starting Hands (all players start with the same cards)
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
                        <input id="generateTypeRandom" type="radio" name="generateType" value="random" onclick="toggleGenerateType()" checked="true"/> <label for="generateTypeRandom">Random</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input id="generateTypeCustom" type="radio" name="generateType" value="custom" onclick="toggleGenerateType()"/> <label for="generateTypeCustom">Custom</label>
                    </div>
                </div>
                <div id="decks" style="padding-top:10px;float:left;clear:both;">
                    <div id="decksLabel" style="float:left;"><span class="label">Decks:</span></div>
                    <div style="clear:both;float: left;">
                        <#list decks as deck>
                            <div style="float:left;padding-left:10px;">
                                <input type="checkbox" name="deck_${deck.deckName}" id="deck_${deck.deckName}" value="${deck.deckName}" onclick="selectDeck(this)" checked/> <label for="deck_${deck.deckName}">${deck.displayName}</label>
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
                                    <option value="5">Very Often</option>
                                    <option value="4">More Often</option>
                                    <option value="3" selected>As Often</option>
                                    <option value="2">Less Often</option>
                                    <option value="1">Rarely</option>
                                </select>
                            </div>
                        </#list>
                    </div>
                </div>
                <div style="padding-top:10px;float:left;clear:both;">
                    <div style="float:left;"><span class="label"># Events/Landmarks/Projects/Ways:</span></div>
                    <div style="clear:both;float:left;padding-left:10px;">
                        <select id="numEventsAndLandmarksAndProjectsAndWays" name="numEventsAndLandmarksAndProjectsAndWays">
                            <option value="0">0</option>
                            <option value="1">1</option>
                            <option value="2" selected>2</option>
                            <option value="3">3</option>
                        </select>
                    </div>
                </div>

                <div id="customCards" style="display:none;float:left;clear:both;">
                    <div class="label" style="padding-top:10px;">
                        Choose Specific Cards:
                    </div>
                    <div style="clear:both;float:left;">
                        <div style="float:left;padding-left:10px;color:red;">Cards selected:</div><div id="numCardsSelected" style="float:left; padding-left:5px; color:red">0</div><div style="float:left;padding-left:15px;color:black;">(remaining cards will be randomized based on selected decks)</div>
                    </div>
                    <div class="customCardsDeckContainer">

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

                <div style="padding-top:10px;float:left;clear:both;">
                    <div style="float:left;"><span class="label">Event Selection:</span></div>
                    <div style="clear:both;float:left;padding-left:10px;">
                        <input id="eventSelectionRandom" type="radio" name="eventSelection" value="random" onclick="toggleEventSelection()" checked="true"/> <label for="eventSelectionRandom">Random</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input id="eventSelectionCustom" type="radio" name="eventSelection" value="custom" onclick="toggleEventSelection()"/> <label for="eventSelectionCustom">Custom</label>
                    </div>
                </div>

                <div id="customEvents" style="display:none;float:left;clear:both;">
                    <div class="label" style="padding-top:10px;">
                        Choose Specific Events:
                    </div>
                    <div style="clear:both;float:left;">
                        <div style="float:left;padding-left:10px;color:red;">Events selected:</div><div id="numEventsSelected" style="float:left; padding-left:5px; color:red">0</div><div style="float:left;padding-left:15px;color:black;">(remaining events will be randomized based on selected decks)</div>
                    </div>
                    <div class="customCardsDeckContainer">
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Events</div>
                            <#list events as event>
                                <div style="float:left;clear:both;">
                                    <#include "createGameEventRow.ftl">
                                </div>
                            </#list>
                        </div>
                    </div>
                </div>

                <div style="padding-top:10px;float:left;clear:both;">
                    <div style="float:left;"><span class="label">Landmark Selection:</span></div>
                    <div style="clear:both;float:left;padding-left:10px;">
                        <input id="landmarkSelectionRandom" type="radio" name="landmarkSelection" value="random" onclick="toggleLandmarkSelection()" checked="true"/> <label for="landmarkSelectionRandom">Random</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input id="landmarkSelectionCustom" type="radio" name="landmarkSelection" value="custom" onclick="toggleLandmarkSelection()"/> <label for="landmarkSelectionCustom">Custom</label>
                    </div>
                </div>

                <div id="customLandmarks" style="display:none;float:left;clear:both;">
                    <div class="label" style="padding-top:10px;">
                        Choose Specific Landmarks:
                    </div>
                    <div style="clear:both;float:left;">
                        <div style="float:left;padding-left:10px;color:red;">Landmarks selected:</div><div id="numLandmarksSelected" style="float:left; padding-left:5px; color:red">0</div><div style="float:left;padding-left:15px;color:black;">(remaining landmarks will be randomized based on selected decks)</div>
                    </div>
                    <div class="customCardsDeckContainer">
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Landmarks</div>
                            <#list landmarks as landmark>
                                <div style="float:left;clear:both;">
                                    <#include "createGameLandmarkRow.ftl">
                                </div>
                            </#list>
                        </div>
                    </div>
                </div>

                <div style="padding-top:10px;float:left;clear:both;">
                    <div style="float:left;"><span class="label">Project Selection:</span></div>
                    <div style="clear:both;float:left;padding-left:10px;">
                        <input id="projectSelectionRandom" type="radio" name="projectSelection" value="random" onclick="toggleProjectSelection()" checked="true"/> <label for="projectSelectionRandom">Random</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input id="projectSelectionCustom" type="radio" name="projectSelection" value="custom" onclick="toggleProjectSelection()"/> <label for="projectSelectionCustom">Custom</label>
                    </div>
                </div>

                <div id="customProjects" style="display:none;float:left;clear:both;">
                    <div class="label" style="padding-top:10px;">
                        Choose Specific Projects:
                    </div>
                    <div style="clear:both;float:left;">
                        <div style="float:left;padding-left:10px;color:red;">Projects selected:</div><div id="numProjectsSelected" style="float:left; padding-left:5px; color:red">0</div><div style="float:left;padding-left:15px;color:black;">(remaining projects will be randomized based on selected decks)</div>
                    </div>
                    <div class="customCardsDeckContainer">
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Projects</div>
                            <#list projects as project>
                                <div style="float:left;clear:both;">
                                    <#include "createGameProjectRow.ftl">
                                </div>
                            </#list>
                        </div>
                    </div>
                </div>

                <div style="padding-top:10px;float:left;clear:both;">
                    <div style="float:left;"><span class="label">Way Selection:</span></div>
                    <div style="clear:both;float:left;padding-left:10px;">
                        <input id="waySelectionRandom" type="radio" name="waySelection" value="random" onclick="toggleWaySelection()" checked="true"/> <label for="waySelectionRandom">Random</label>
                    </div>
                    <div style="float:left;padding-left:10px;">
                        <input id="waySelectionCustom" type="radio" name="waySelection" value="custom" onclick="toggleWaySelection()"/> <label for="waySelectionCustom">Custom</label>
                    </div>
                </div>

                <div id="customWays" style="display:none;float:left;clear:both;">
                    <div class="label" style="padding-top:10px;">
                        Choose Specific Ways:
                    </div>
                    <div style="clear:both;float:left;">
                        <div style="float:left;padding-left:10px;color:red;">Ways selected:</div><div id="numWaysSelected" style="float:left; padding-left:5px; color:red">0</div><div style="float:left;padding-left:15px;color:black;">(remaining ways will be randomized based on selected decks)</div>
                    </div>
                    <div class="customCardsDeckContainer">
                        <div class="customCardsDeck">
                            <div style="float:left;" class="label">Ways</div>
                            <#list ways as way>
                                <div style="float:left;clear:both;">
                                    <#include "createGameWayRow.ftl">
                                </div>
                            </#list>
                        </div>
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
                    <div id="excludedCardsContent" class="customCardsDeckContainer" style="display:none;">
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
