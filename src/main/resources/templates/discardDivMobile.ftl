<div style="clear:both">
    <span class="label">Discard:</span>
    <#if player.discard?size != 0>
        <#assign clickType="discard">
        <#assign card = player.discard[player.discard?size-1]>
        <#include "gameCard.ftl">
    </#if>
</div>