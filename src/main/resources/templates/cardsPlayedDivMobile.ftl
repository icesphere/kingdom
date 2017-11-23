<div style="float:left;clear:both;">
    <div style="float:left;padding-right:10px;">
        <#if currentPlayer.userId == user.userId>
            Your Turn
        <#else>
            ${currentPlayer.username}'s Turn
        </#if>
    </div>
    <div style="float:left;padding-right:10px;">
        ${currentPlayer.actions} Action<#if currentPlayer.actions != 1>s</#if>
    </div>
    <div style="float:left;">
        <#if currentPlayer.userId == user.userId>
            <a href="javascript:endTurn()">End Turn</a>
        </#if>
    </div>
</div>
<div style="clear:both;" class="label">
    Cards Played:
</div>
<div>
    <#assign clickType="played">
    <#assign previousCard = "">
    <#list cardsPlayed as card>
        <#if previousCard == card.name>
            <#assign zindex = zindex + 100>
        <#else>
            <#assign zindex = 0>
        </#if>
        <div style="float:left; margin-right:5px;margin-top:2px;<#if previousCard == card.name>margin-left:-65px;</#if>z-index:${zindex};">
            <#include "gameCard.ftl">
            <#assign previousCard = card.name>
        </div>
    </#list>
</div>