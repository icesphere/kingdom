<!DOCTYPE html>
<html>
<head>
    <title>Overall Stats</title>
    <#include "commonIncludes.ftl">
</head>
<body>
<#include "adminLinks.ftl">
<h3>User Stats</h3>
<table cellpadding="3" style="text-align:left">
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=active">Active Users:</a>
        </td>
        <td>
            ${stats.activeUsers}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=baseChecked">Base Deck:</a>
        </td>
        <td>
            ${stats.baseDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=intrigueChecked">Intrigue Deck:</a>
        </td>
        <td>
            ${stats.intrigueDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=seasideChecked">Seaside Deck:</a>
        </td>
        <td>
            ${stats.seasideDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=alchemyChecked">Alchemy Deck:</a>
        </td>
        <td>
            ${stats.alchemyDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=prosperityChecked">Prosperity Deck:</a>
        </td>
        <td>
            ${stats.prosperityDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=cornucopiaChecked">Cornucopia Deck:</a>
        </td>
        <td>
            ${stats.cornucopiaDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=hinterlandsChecked">Hinterlands Deck:</a>
        </td>
        <td>
            ${stats.hinterlandsDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=promoChecked">Promo Deck:</a>
        </td>
        <td>
            ${stats.promoDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=salvationChecked">Salvation Deck:</a>
        </td>
        <td>
            ${stats.salvationDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=fairyTaleChecked">Fairy Tale Deck:</a>
        </td>
        <td>
            ${stats.fairyTaleDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=leadersChecked">Leaders Deck:</a>
        </td>
        <td>
            ${stats.leadersDeck}
        </td>
    </tr>  
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=proletariatChecked">Proletariat Deck:</a>
        </td>
        <td>
            ${stats.proletariatDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=otherFanCardsChecked">Other Fan Cards:</a>
        </td>
        <td>
            ${stats.fanDeck}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=soundDefault&value=2">Sound Off:</a>
        </td>
        <td>
            ${stats.soundOff}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=alwaysPlayTreasureCards">Always Play Treasure Cards:</a>
        </td>
        <td>
            ${stats.alwaysPlayTreasureCards}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=showVictoryPoints">Show Victory Points:</a>
        </td>
        <td>
            ${stats.showVictoryPoints}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=identicalStartingHands">Identical Starting Hands:</a>
        </td>
        <td>
            ${stats.identicalStartingHands}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=playedMobileGame">Played Mobile Game:</a>
        </td>
        <td>
            ${stats.playedMobileGame}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=usingDeckFrequencies">Using Deck Frequencies:</a>
        </td>
        <td>
            ${stats.usingDeckFrequencies}
        </td>
    </tr>
    <tr>
        <td class="label">
            <a href="showUsersForStat.html?stat=usingExcludedCards">Using Excluded Cards:</a>
        </td>
        <td>
            ${stats.usingExcludedCards}
        </td>
    </tr>
</table>
</body>
</html>
