<div id="infoDialog" class="infoDialog" style="display:none" title="">
    <#if player.infoDialogSet>
        <div style="font-size:${player.infoDialog.messageFontSize}px; text-align:${player.infoDialog.messageAlign}"><p>${player.infoDialog.message}</p></div>
        <#if player.infoDialog.cards?size != 0>
            <table>
                <tr>
                    <#assign clickType="infoDialog">
                    <#list player.cardAction.cards as card>
                        <#if card_index == 7 || card_index == 14>
                            </tr>
                            <tr>
                        </#if>
                        <td><#include "gameCard.ftl"></td>
                    </#list>
                </tr>
            </table>
        </#if>
    </#if>
</div>