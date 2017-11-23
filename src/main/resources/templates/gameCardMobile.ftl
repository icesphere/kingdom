<div class="cardName" style="<#if (clickType == "leader" && !card.activated) || (clickType=='supply' && gameStatus == 3 && supply(card.cardId) == 0)><#else><#if card.type == 7>background-image: url(images/grey_green_mobile.gif);background-repeat: repeat-x;<#elseif card.type == 8>background-image: url(images/gold_green_mobile.gif);background-repeat: repeat-x;<#elseif card.type == 10>background-image: url(images/gold_purple_mobile.gif);background-repeat: repeat-x;<#elseif card.type == 11>background-image: url(images/green_blue_mobile.gif);background-repeat: repeat-x;<#elseif card.type == 12>background-image: url(images/orange_green_mobile.gif);background-repeat: repeat-x;<#elseif card.type == 14>background-image: url(images/gold_blue_mobile.gif);background-repeat: repeat-x;<#else>background-color:${card.backgroundColor};</#if></#if>">
    <div style="position:absolute;left:3px;bottom:2px;font-size:10px;">${cost}</div>
    <div style="position:absolute;<#if cost &gt; 9>left:14px;<#else>left:11px;</#if>bottom:-1px;"><img src="images/coin.png" alt="coin" height="10" width="10"/></div>
    <#if card.costIncludesPotion><div style="position:absolute;left:18px;bottom:-4px;"><img src="images/bluepotion.png" alt="potion" style="height:16px; width:16px;"/></div></#if>
    <div style="position:absolute;width:68px;text-align:center;left:2px;<#if card.nameLines != 1 || card.fontSize &gt; 11 || card.fontSize == 0>top:2px;<#elseif card.fontSize &gt; 9>top:3px;<#elseif card.fontSize &gt; 8>top:4px;<#else>top:5px;</#if>font-size:<#if card.fontSize &gt; 0>${card.fontSize}<#else>12</#if>px;">
        ${card.name}
    </div>
    <#if (clickType=="supply" || clickType=="admin") && gameStatus == 3><div style="position:absolute;right:2px;font-size:10px;bottom:2px;">${supply(card.cardId)}</div></#if>
    <#if (card.victory || card.curse || card.leader) && card.victoryPoints !=0>
        <#if card.type == 8 || card.type == 10>
            <div style="position:absolute;bottom:9px;font-size:9px;left:27px;">${card.victoryPoints} VP</div>
        <#else>
            <div style="position:absolute;bottom:2px;<#if card.victoryPoints &gt; 9>font-size:10px;<#else>font-size:11px;</#if><#if card.victoryPoints &gt; 9>left:27px;<#elseif card.victoryPoints &lt; 0>left:24px;<#else>left:27px;</#if>">${card.victoryPoints} VP</div>
        </#if>
    </#if>
    <#if card.treasure && card.addCoins != 0>
        <#if card.type == 8 || card.type == 10>
            <div style="position:absolute;bottom:0px;font-size:9px;left:27px;">${card.addCoins}</div>
            <div style="position:absolute;left:37px;bottom:-2px;"><img src="images/coin.png" alt="coin" height="8" width="8"/></div>
        <#elseif card.name == "Philosopher's Stone">
            <div style="position:absolute;bottom:1px;font-size:12px;left:34px;">${card.addCoins}</div>
            <div style="position:absolute;left:44px;bottom:-1px;"><img src="images/coin.png" alt="coin" height="12" width="12"/></div>
        <#else>
            <div style="position:absolute;bottom:1px;font-size:12px;left:27px;">${card.addCoins}</div>
            <div style="position:absolute;left:37px;bottom:-1px;"><img src="images/coin.png" alt="coin" height="12" width="12"/></div>
        </#if>
    </#if>
    <#if card.potion>
        <div style="position:absolute;left:27px;bottom:-5px;"><img src="images/bluepotion.png" alt="potion" style="height:22px; width:22px;"/></div>
    </#if>
    <#if baneCard?? && baneCard>
        <div style="position:absolute;bottom:10px;font-size:8px;left:28px;">Bane</div>
        <div style="position:absolute;bottom:2px;font-size:8px;left:28px;">Card</div>
    </#if>
</div>
