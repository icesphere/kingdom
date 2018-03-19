<table>
    <tr>
        <td style="height:20px;">
            <table>
                <tr>
                    <#if gameStatus == "InProgress">
                        <td style="padding-left:10px;">${currentPlayer.buys} Buy<#if currentPlayer.buys != 1>s</#if></td>
                        <#if currentPlayer.userId == user.userId>
                            <td style="padding-left:10px;">${currentPlayer.coins}</td>
                            <td><span style="position:relative; top:2px;"><img src="images/coin.png" alt="Coins" style="height:16px; width:16px;"/></span></td>
                        </#if>
                    <#else>
                        <td>&#160;</td>
                    </#if>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td>
            <div style="float:left;">
                <#assign clickType="bought">
                <#assign previousCard = "">
                <#list cardsBought as card>
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