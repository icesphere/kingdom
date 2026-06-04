<div class="cardStackList">
    <#assign clickType="duration">
    <#assign previousCard = "">
    <#list player.durationCards as card>
        <#if previousCard != card.name>
            <#if card_index != 0>
                </div>
            </#if>
            <div class="cardStack">
        </#if>
        <#if previousCard == card.name>
            <#assign zindex = zindex + 100>
        <#else>
            <#assign zindex = 0>
        </#if>
        <div class="cardStackCard" style="<#if mobile>margin-top:2px;</#if>z-index:${zindex};">
            <#include "gameCard.ftl">
            <#assign previousCard = card.name>
        </div>
        <#if !card_has_next>
            </div>
        </#if>
    </#list>
</div>
