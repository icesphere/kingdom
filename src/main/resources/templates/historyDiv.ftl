<div style="float:left;clear:both;padding-top:5px;padding-bottom:5px;">

    <#if recentHistory?size != 0>

        <div class="historyLabel">Recent Events:</div>

        <div class="historyLine">
            <#list recentHistory as event>
                <div style="clear:both;">${event}</div>
            </#list>
        </div>

    </#if>

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

                <#if turnSummary.eventsBoughtString?has_content>
                    <div class="historyLine">
                        Events Bought: ${turnSummary.eventsBoughtString}
                    </div>
                </#if>

                <#if turnSummary.projectsBoughtString?has_content>
                    <div class="historyLine">
                        Projects Bought: ${turnSummary.projectsBoughtString}
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

        <div class="historyLabel">Recent turns:</div>

        <#list turnHistory as turn>
            <div class="historyLine">
                <#list turn.allLogs as event>
                    <div style="clear:both;">${event}</div>
                </#list>
            </div>
        </#list>
    </#if>
</div>