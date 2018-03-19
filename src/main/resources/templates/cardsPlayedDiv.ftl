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
            <div style="float:left;">
                <#assign clickType="played">
                <#assign previousCard = "">
                <#list cardsPlayed as card>
                    <#if previousCard == card.name>
                        <#assign zindex = zindex + 100>
                    <#else>
                        <#assign zindex = 0>
                    </#if>
                    <div style="float:left; margin-right:5px;<#if previousCard == card.name>margin-left:-65px;</#if>z-index:${zindex};">
                        <#include "gameCard.ftl">
                        <#assign previousCard = card.name>
                    </div>
                </#list>
            </div>
        </td>
    </tr>
</table>