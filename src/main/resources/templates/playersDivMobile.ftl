<#list players as player>
    <div style="float:left;clear:both;">
        ${player.username}
        <#if showVictoryPoints>
            <span style="color:green;"> (${player.victoryPoints} VP)</span>
        </#if>
    </div>
</#list>
