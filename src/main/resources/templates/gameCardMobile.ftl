<div class="cardName" style="<#if clickType=='supply' && gameStatus == "InProgress" && supply(card.pileName) == 0><#else><#if card.backgroundColor.image>background-image: url(images/${card.backgroundColorMobileColor});background-repeat: repeat-x;<#else>background-color:${card.backgroundColorColor};</#if></#if>">
    <#if !card.landmark && (card.cost != 0 || card.debtCost == 0)>
        <div style="display: flex; position: absolute; left: 3px; bottom: 3px; font-size: 10px; font-weight: bold;">
            <div style="z-index: 1; position: relative; top: 1px; left: <#if cost &gt; 9>1px<#else>3px</#if>;">${cost}</div>
            <div style="position: absolute;"><img src="images/coin.png" alt="coin" height="13" width="12"/></div>
        </div>
    </#if>
    <#if card.debtCost != 0>
        <div style="display: flex; position: absolute; left: <#if card.cost == 0>3px<#else>15px</#if>; bottom: -1px; font-size: 10px; color: white;">
            <div style="z-index: 1; position: absolute; bottom: 3px; left: <#if cost &gt; 9>1px<#else>4px</#if>;">${card.debtCost}</div>
            <div style="position: absolute; bottom: 0;"><img src="images/debt.png" alt="debt" height="14" width="14"/></div>
        </div>
    </#if>
    <div style="position:absolute;width:68px;text-align:center;left:2px;<#if card.nameLines != 1 || card.fontSize &gt; 11 || card.fontSize == 0>top:2px;<#elseif card.fontSize &gt; 9>top:3px;<#elseif card.fontSize &gt; 8>top:4px;<#else>top:5px;</#if>font-size:<#if card.fontSize &gt; 0>${card.fontSize}<#else>12</#if>px;">
        ${card.name}
    </div>
    <#if (clickType=="supply" || clickType=="admin") && gameStatus == "InProgress"><div style="position:absolute;right:2px;font-size:10px;bottom:2px;">${supply.get(card.pileName)}</div></#if>
    <#if (card.victory || card.curse) && card.victoryPoints !=0>
        <#if card.treasure>
            <div style="position:absolute;bottom:9px;font-size:9px;left:27px;">${card.victoryPoints} VP</div>
        <#else>
            <div style="position:absolute;bottom:2px;<#if card.victoryPoints &gt; 9>font-size:10px;<#else>font-size:11px;</#if><#if card.victoryPoints &gt; 9>left:27px;<#elseif card.victoryPoints &lt; 0>left:24px;<#else>left:27px;</#if>">${card.victoryPoints} VP</div>
        </#if>
    </#if>
    <#if card.treasure && card.addCoins != 0>
        <#if (card.victory || card.curse) && card.victoryPoints !=0>
            <div style="position:absolute;bottom:0px;font-size:9px;left:27px;">${card.addCoins}</div>
            <div style="position:absolute;left:37px;bottom:-2px;"><img src="images/coin.png" alt="coin" height="8" width="8"/></div>
        <#else>
            <div style="position:absolute;bottom:1px;font-size:12px;left:27px;">${card.addCoins}</div>
            <div style="position:absolute;left:37px;bottom:-1px;"><img src="images/coin.png" alt="coin" height="12" width="12"/></div>
        </#if>
    </#if>
    <#if baneCard?? && baneCard>
        <div style="position:absolute;bottom:10px;font-size:8px;left:28px;">Bane</div>
        <div style="position:absolute;bottom:2px;font-size:8px;left:28px;">Card</div>
    </#if>

    <#if (clickType=='supply' || clickType == 'landmark') && victoryPointsOnSupplyPile(card.name)?? && victoryPointsOnSupplyPile(card.name) != 0><div style="position: absolute; bottom: 3px; left: 17px; z-index: 5; color: #00BB00; font-size: 8px; font-weight: bold; padding: 0 2px 0 2px;background-color: #ffffffcc;">${victoryPointsOnSupplyPile(card.name)} VP on pile</div></#if>
    <#if clickType=='supply' && debtOnSupplyPile(card.name)?? && debtOnSupplyPile(card.name) != 0><div style="position: absolute; bottom: 10px; left: 14px; z-index: 5; color: #BB0000; font-size: 8px; font-weight: bold; padding: 0 2px 0 2px;background-color: #ffffffcc;">${debtOnSupplyPile(card.name)} debt on pile</div></#if>
    <#if clickType=='supply' && showEmbargoTokens?? && showEmbargoTokens && embargoTokens(card.name)?? && embargoTokens(card.name) != 0><div style="position: absolute; bottom: 11px; left: 5px; z-index: 5; color: #BB0000; font-size: 8px; font-weight: bold;">(${embargoTokens(card.name)} ET)</div></#if>
    <#if clickType=='supply' && showTradeRouteTokens?? && showTradeRouteTokens && tradeRouteTokenMap(card.name)?? && tradeRouteTokenMap(card.name)><div style="position: absolute; bottom: 11px; right: 4px; z-index: 5; color: #0000BB; font-size: 8px;">(TRT)</div></#if>

</div>
