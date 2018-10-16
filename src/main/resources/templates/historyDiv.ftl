<table style="width:100%">
    <tr>
        <td class="historyDivColumn">
            <div id="historyScrollingDiv">
                <#if turnHistory?size != 0>
                    <table style="width:100%">
                        <#list turnHistory as turn>
                            <#list turn.reversedHistory as event>
                                <tr><td>${event}</td></tr>
                            </#list>
                        </#list>
                    </table>
                </#if>
            </div>
        </td>
    </tr>
</table>