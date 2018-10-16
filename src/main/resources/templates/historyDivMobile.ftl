<div style="float:left;clear:both;padding-top:5px;padding-bottom:5px;">
    <#if turnHistory?size != 0>
        <#list turnHistory as turn>
            <div style="clear:both;float:left;margin-right:1px;padding:3px;">
                <#list turn.reversedHistory as event>
                    <div style="clear:both;">${event}</div>
                </#list>
            </div>
        </#list>
    </#if>
</div>