<table>
    <tr>
        <#if player.discard?size != 0>
            <#assign clickType="discard">
            <#assign card = player.discard[player.discard?size-1]>
            <td><#include "gameCard.ftl"></td>
        <#else>
            <td>&#160;</td>
        </#if>
    </tr>
</table>