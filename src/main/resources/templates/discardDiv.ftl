<table>
    <tr>
        <#if player.cardsInDiscard?size != 0>
            <#assign clickType="discard">
            <#assign card = player.cardsInDiscard[player.cardsInDiscard?size-1]>
            <td><#include "gameCard.ftl"></td>
        <#else>
            <td>&#160;</td>
        </#if>
    </tr>
</table>