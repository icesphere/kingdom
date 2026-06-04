<table>
    <tr>
        <td style="height:20px;">
            <table>
                <tr>
                    <#if gameStatus == "InProgress">
                        <#if currentPlayer.userId == user.userId>
                            <td>Your Turn</td>
                        <#else>
                            <td>${currentPlayer.username}'s Turn</td>
                        </#if>
                        <td style="padding-left:15px;">${currentPlayer.actions} Action<#if currentPlayer.actions != 1>s</#if></td>
                        <#if currentPlayer.userId == user.userId>
                            <td style="padding-left:15px;"><a href="javascript:endTurn()">End Turn</a></td>
                        </#if>
                        <#if canUndoLastCommand>
                            <td style="padding-left:15px;"><a href="javascript:requestUndo()" title="${undoSummary}">Undo</a></td>
                        </#if>
                    <#elseif gameStatus == "Finished">
                        Game Finished
                    <#else>
                        <td>Waiting for Players to Join the Game</td>
                    </#if>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td>
            <div class="cardStackList">
                <#assign clickType="played">
                <#assign previousCard = "">
                <#list cardsPlayed as card>
                    <#if previousCard != card.name>
                        <#if card_index != 0>
                            </div>
                        </#if>
                        <div class="cardStack">
                    </#if>
                    <#if previousCard == card.name>
                        <#assign zindex = zindex + 100>
                    <#else>
                        <#assign zindex = 0>
                    </#if>
                    <div class="cardStackCard" style="z-index:${zindex};">
                        <#include "gameCard.ftl">
                        <#assign previousCard = card.name>
                    </div>
                    <#if !card_has_next>
                        </div>
                    </#if>
                </#list>
            </div>
        </td>
    </tr>
</table>
