<div style="clear:both;float:left;">
    <#if gameStatus == 3>
        <div style="float:left;padding-right:10px;">
            ${currentPlayer.buys} Buy<#if currentPlayer.buys != 1>s</#if>
        </div>
        <#if currentPlayer.userId == user.userId>
            <div style="float:left;padding-right:10px;">
                ${currentPlayer.coins}
                <span style="position:relative; top:2px;"><img src="images/coin.png" alt="Coins" style="height:16px; width:16px;"/></span>
            </div>
            <#if showPotions>
                <div style="float:left;position:relative; top:-6px;">
                    ${currentPlayer.potions}
                    <span style="position:relative; left:-4px; top:5px;"><img src="images/bluepotion.png" alt="Potions" style="height:22px; width:22px;"/></span>
                </div>
            </#if>
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