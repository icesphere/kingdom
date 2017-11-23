<div id="cityPlannerCardsDialog" title="City Planner Cards" style="display:none">
    <table>
        <tr>
            <#assign clickType="played">
            <#list player.cityPlannerCards as card>
                <td><#include "gameCard.ftl"></td>
            </#list>
        </tr>
    </table>
    <div style="text-align:center; padding-top: 10px; padding-bottom:10px;">
        <input type="button" value="Close" onclick="closeCityPlannerCardsDialog()"/>
    </div>
</div>