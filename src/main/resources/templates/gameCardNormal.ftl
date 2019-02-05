<div>
    <div class="cardName" style="margin-bottom:2px;font-size:<#if card.name?length &gt; 12>12<#else>14</#if>px; padding-top:5px; padding-bottom:2px; <#if clickType=='supply' && gameStatus == "InProgress" && supply(card.pileName) == 0><#else><#if card.backgroundColor.image>background-image: url(images/${card.backgroundColorColor});background-repeat: repeat-x;<#else>background-color:${card.backgroundColorColor};</#if></#if>">
        ${card.name}
    </div>

    <#if card.addCards != 0>
        <div class="cardRow" style="<#if adjustFontSizeForMobile?? && adjustFontSizeForMobile>font-size: 8px;</#if>">
            + ${card.addCards} Card<#if card.addCards != 1>s</#if>
        </div>
    </#if>
    <#if card.addActions != 0>
        <div class="cardRow" style="<#if adjustFontSizeForMobile?? && adjustFontSizeForMobile>font-size: 8px;</#if>">
            + ${card.addActions} Action<#if card.addActions != 1>s</#if>
        </div>
    </#if>
    <#if card.addBuys != 0>
        <div class="cardRow" style="<#if adjustFontSizeForMobile?? && adjustFontSizeForMobile>font-size: 8px;</#if>">
            + ${card.addBuys} Buy<#if card.addBuys != 1>s</#if>
        </div>
    </#if>
    <#if card.addCoffers != 0>
        <div class="cardRow" style="<#if adjustFontSizeForMobile?? && adjustFontSizeForMobile>font-size: 8px;</#if>">
            + ${card.addCoffers} Coffers
        </div>
    </#if>
    <#if (card.name == "Philosopher's Stone" || card.name == "Bank") && card.addCoins == 0>
        <div class="cardRow" style="<#if adjustFontSizeForMobile?? && adjustFontSizeForMobile>font-size: 8px;</#if>">
            <div style="font-size:14px; padding-top:3px;">? <img src="images/coin.png" alt="coin" height="16" width="16"/></div>
        </div>
    <#elseif (card.addCoins != 0 || card.treasure) && card.name != "Fool's Gold">
        <div class="cardRow" style="<#if adjustFontSizeForMobile?? && adjustFontSizeForMobile>font-size: 8px;</#if>">
            <#if card.treasure>
                <#if card.special != "">
                    <div style="font-size:14px; padding-top:5px;">${card.addCoins} <img src="images/coin.png" alt="coin" height="16" width="16"/></div>
                <#else>
                    <div style="font-size:16px; padding-top:5px;">${card.addCoins} <img src="images/coin.png" alt="coin" height="20" width="20"/></div>
                </#if>
                <#else>
                    <#if card.addCoins &lt; 0>- ${card.addCoins * -1} <#else>+ ${card.addCoins} </#if><img src="images/coin.png" alt="coin" height="12" width="12" style="position: relative; top: 2px;"/>
            </#if>
        </div>
    </#if>
    <#if card.addVictoryCoins != 0>
        <div class="cardRow" style="<#if adjustFontSizeForMobile?? && adjustFontSizeForMobile>font-size: 8px;</#if>">
            + ${card.addVictoryCoins} VC
        </div>
    </#if>
    <#if card.victoryPoints != 0>
        <div class="cardRow" style="<#if adjustFontSizeForMobile?? && adjustFontSizeForMobile>font-size: 8px;</#if>">
            <div style="font-size:16px;padding-top:5px;">${card.victoryPoints} VP</div>
        </div>
    </#if>
    <#if card.special != "">
        <div class="specialRow" style="<#if adjustFontSizeForMobile?? && adjustFontSizeForMobile>font-size: 8px;</#if>">
            <#if clickType == "info">
                <a style="color: #003399;cursor: pointer;" onclick="javascript:alert('${card.special?js_string}')">${card.truncatedSpecial}</a>
            <#else>
                ${card.truncatedSpecial}
            </#if>
        </div>
    </#if>
</div>
<div style="position:absolute; right:3px; bottom:0;<#if card.attack>color:#C00;</#if> font-size:<#if card.typeAsString?length &gt; 22>6<#elseif card.typeAsString?length &gt; 18>7<#elseif card.typeAsString?length &gt; 14>8<#else>10</#if>px; ">${card.typeAsString}</div>
<#if card.cost != 0 || card.debtCost == 0>
    <div style="position:absolute; left:2px; bottom:16px; display: flex;"><div style="position: absolute; left: <#if cost &gt; 9>2px<#else>4px</#if>; z-index: 1; font-size: 13px; font-weight: bold;">${cost}<#if card.overpayForCardAllowed>+</#if></div><img src="images/coin.png" alt="coin" height="15" width="15" style="position: absolute;"/></div>
</#if>
<#if card.debtCost != 0>
    <div style="position:absolute; <#if card.cost == 0>2px<#else>18px</#if>; bottom:15px; display: flex;"><div style="position: absolute; left: 4px; z-index: 1; font-size: 13px; font-weight: bold; color: white;">${card.debtCost}</div><img src="images/debt.png" alt="debt" height="15" width="15" style="position: absolute;"/></div>
</#if>
