<div id="nativeVillageDialog" title="Native Village Cards" style="display:none">
    <table>
        <tr>
            <#assign clickType="played">
            <#list player.nativeVillageCards as card>
                <td><#include "gameCard.ftl"></td>
            </#list>
        </tr>
    </table>
    <div style="text-align:center; padding-top: 10px; padding-bottom:10px;">
        <input type="button" value="Close" onclick="closeNativeVillageDialog()"/>
    </div>
</div>