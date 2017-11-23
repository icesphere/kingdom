<div style="float:left;">
    <#assign clickType="leader">
    <#assign previousCard = "">
    <#list player.leaders as card>
        <#if previousCard == card.name>
            <#assign zindex = zindex + 100>
        <#else>
            <#assign zindex = 0>
        </#if>
        <div style="float:left; margin-right:5px;<#if mobile>margin-top:2px;</#if><#if previousCard == card.name>margin-left:-65px;</#if>z-index:${zindex};">
            <#include "gameCard.ftl">
            <#assign previousCard = card.name>
        </div>
    </#list>
</div>