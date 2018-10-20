<div style="clear:both">
    <span class="label">Discard:</span>
    <#if player.cardsInDiscard?size != 0>
        <#assign clickType="discard">
        <#assign card = player.cardsInDiscard[player.cardsInDiscard?size-1]>
        <#include "gameCard.ftl">
    </#if>
</div>