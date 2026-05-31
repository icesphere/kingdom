<div style="float:left;">
    <#assign clickType="hand">
    <#assign previousCard = "">
    <#list player.groupedHand as card>
        <#if previousCard == card.name>
            <#assign zindex = zindex + 100>
        <#else>
            <#assign zindex = 0>
        </#if>
        <div style="float:left; margin-right:5px;<#if mobile>margin-top:2px;</#if><#if card.highlighted && player.currentAction??><#elseif previousCard == card.name>margin-left:-65px;</#if>z-index:${zindex};">
            <#include "gameCard.ftl">
            <#assign previousCard = card.name>
        </div>
    </#list>
</div>
<#if player.playableShadowCards?has_content>
    <div style="clear:both;float:left;padding-top:4px;">
        <#assign clickType="deck">
        <#list player.playableShadowCards as card>
            <div style="float:left; margin-right:5px;<#if mobile>margin-top:2px;</#if>">
                <#include "gameCard.ftl">
            </div>
        </#list>
    </div>
</#if>
