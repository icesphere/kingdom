<div style="float:left;clear:both;padding-top:5px;padding-bottom:5px;">
    <#if turnHistory?size != 0>

        <#if lastTurnSummaries?size != 0>
            <div class="historyLabel">Last Turn Summaries:</div>

            <#list lastTurnSummaries as turnSummary>
                <div class="historyLabel">${turnSummary.username}:</div>

                <#if turnSummary.cardsGainedString?has_content>
                    <div class="historyLine">
                        Gained: ${turnSummary.cardsGainedString}
                    </div>
                </#if>

                <#if turnSummary.cardsPlayedString?has_content>
                    <div class="historyLine">
                        Played: ${turnSummary.cardsPlayedString}
                    </div>
                </#if>

                <#if turnSummary.cardsTrashedString?has_content>
                    <div class="historyLine">
                        Trashed: ${turnSummary.cardsTrashedString}
                    </div>
                </#if>
            </#list>
        </#if>

        <div class="historyLabel">Events:</div>

        <#list turnHistory as turn>
            <div class="historyLine">
                <#list turn.history as event>
                    <div style="clear:both;">${event}</div>
                </#list>
            </div>
        </#list>
    </#if>
</div>