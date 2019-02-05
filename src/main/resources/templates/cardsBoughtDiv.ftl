<table>
    <tr>
        <td style="height:20px;">
            <table>
                <tr>
                    <#if gameStatus == "InProgress">
                        <td style="padding-left:10px;">${currentPlayer.buys} Buy<#if currentPlayer.buys != 1>s</#if></td>
                        <#if currentPlayer.userId == user.userId>
                            <td style="padding-left:10px;">${currentPlayer.availableCoins}</td>
                            <td><span style="position:relative; top:2px;"><img src="images/coin.png" alt="Coins" style="height:16px; width:16px;"/></span></td>
                            <#if currentPlayer.debt != 0>
                                <td style="padding-left:10px;">${currentPlayer.debt}</td>
                                <td><span style="position:relative; top:2px;"><img src="images/debt.png" alt="Debt" style="height:18px; width:18px;"/></span></td>
                                <#if currentPlayer.availableCoins != 0>
                                    <td style="padding-left:10px;"><a href="javascript:payOffDebt()">Pay off</a></div></td>
                                </#if>
                            </#if>
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