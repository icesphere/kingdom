<div style="<#if player.currentAction??>display:block<#else>display:none</#if>;">
    <#if (player.currentAction)??>
        <#assign action = player.currentAction>
        <div class="cardActionContent">
            <div style="font-size: 20px; padding-bottom: 10px;">
                Action
            </div>
            <div>
                ${action.text}
            </div>

            <#if action.choices?has_content>
                <div style="clear: both; float: left; padding-top: 10px;">
                    <#list action.choices as choice>
                        <div style="margin-right: 10px; float: left;">
                            <input type="button" onclick="submitCardActionChoice(${choice.choiceNumber})" value="${choice.text}"/>
                        </div>
                    </#list>
                </div>
            </#if>

            <#if action.showDone || action.showDoNotUse>

                <div style="clear: both; float: left; padding-top: 10px;">

                    <#if action.showDoNotUse>
                        <input type="button" style="margin-right: 15px; float: left;" onclick="submitDoNotUseAction()" value="Do not use"/>
                    </#if>

                    <#if action.showDone>
                        <input type="button" style="margin-right: 15px; float: left;" onclick="submitDoneWithAction()" value="Done"/>
                    </#if>

                </div>

            </#if>

        </div>
    </#if>
</div>
