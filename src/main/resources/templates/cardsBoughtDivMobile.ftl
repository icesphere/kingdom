<div style="clear:both;float:left;">
    <#if gameStatus == "InProgress">
        <div style="float:left;padding-right:10px;">
            ${currentPlayer.buys} Buy<#if currentPlayer.buys != 1>s</#if>
        </div>
        <div style="float:left;padding-right:10px;">
            ${currentPlayer.availableCoins}
            <span style="position:relative; top:2px;"><img src="images/coin.png" alt="Coins" style="height:16px; width:16px;"/></span>
        </div>
        <#if currentPlayer.debt != 0>
            <div style="float:left;padding-right:10px;display: flex; position: relative; top: 2px;">
                <div style="padding-right: 2px;">${currentPlayer.debt}</div>
                <div><img src="images/debt.png" alt="Debt" style="height:18px; width:18px;"/></div>
                <#if currentPlayer.availableCoins != 0>
                    <div><a href="javascript:payOffDebt()">Pay off</a></div>
                </#if>
            </div>
        </#if>
    </#if>
</div>
<div style="clear:both;" class="label">
    Cards Bought:
</div>
<div>
    <#assign clickType="bought">
    <#assign previousCard = "">
    <#list cardsBought as card>
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