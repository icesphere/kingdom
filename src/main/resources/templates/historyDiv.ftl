<div style="float:left;clear:both;padding-top:5px;padding-bottom:5px;">
    <#if turnHistory?size != 0>

        <#if lastTurnSummaries?size != 0>
            <div style="clear: both; float: left; margin-right:1px; padding:3px; font-weight: bold">Last Turn Summaries:</div>

            <#list lastTurnSummaries as turnSummary>
                <div style="clear: both; float: left; margin-right:1px; padding:3px; font-weight: bold">${turnSummary.username}:</div>

                <#if turnSummary.cardsGainedString?has_content>
                    <div style="clear:both;float:left;margin-right:1px;padding:3px;">
                        Gained: ${turnSummary.cardsGainedString}
                    </div>
                </#if>

                <#if turnSummary.cardsPlayedString?has_content>
                    <div style="clear:both;float:left;margin-right:1px;padding:3px;">
                        Played: ${turnSummary.cardsPlayedString}
                    </div>
                </#if>

                <#if turnSummary.cardsTrashedString?has_content>
                    <div style="clear:both;float:left;margin-right:1px;padding:3px;">
                        Trashed: ${turnSummary.cardsTrashedString}
                    </div>
                </#if>
            </#list>
        </#if>

        <div style="clear: both; float: left;margin-right:1px;padding:3px; font-weight: bold">Events:</div>

        <#list turnHistory as turn>
            <div style="clear:both;float:left;margin-right:1px;padding:3px;">
                <#list turn.history as event>
                    <div style="clear:both;">${event}</div>
                </#list>
            </div>
        </#list>
    </#if>
</div>