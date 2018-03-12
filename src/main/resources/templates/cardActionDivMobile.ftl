<div id="cardActionDialog_" class="cardActionDialog" style="display:none;padding-bottom:30px;" title="">
    <#if player.showCardAction>
        <div style="font-size:14px;"><p>${player.oldCardAction.instructions}</p></div>
        <#if player.oldCardAction.type == 11>
            <div style="padding-bottom:10px;font-size:20px;"><table><tr><td><input type="button" onclick="submitCardActionYesNo('yes')" value="Yes"/></td><td style="padding-left:10px;"><input type="button" onclick="submitCardActionYesNo('no')" value="No"/></td></tr></table></div>
        <#elseif player.oldCardAction.type == 14>
            <div style="padding-bottom:10px;float:left;font-size:20px;">
                <#list player.oldCardAction.choices as choice>
                    <div style="float:left;padding-right:5px;padding-bottom:5px;">
                        <input type="button" onclick="submitCardActionChoice('${choice.value}')" value="${choice.button}"/>
                    </div>
                </#list>
            </div>
        <#elseif player.oldCardAction.type == 19>
            <div style="padding-bottom:10px;font-size:14px;float:left;">
                <#list player.oldCardAction.startNumber..player.oldCardAction.endNumber as i>
                    <div style="padding-right:5px;float:left;">
                        <a style="color:blue" href="javascript:submitCardActionChooseNumberBetween(${i})">${i}</a>
                    </div>
                </#list>
            </div>
        <#elseif player.oldCardAction.type == 23>
            <div style="padding-bottom:10px;font-size:14px;float:left;">
                <#list player.oldCardAction.startNumber..player.oldCardAction.endNumber as i>
                    <#if i % 2 == 0>
                        <div style="padding-right:5px;float:left;">
                            <a style="color:blue" href="javascript:submitCardActionChooseNumberBetween(${i})">${i}</a>
                        </div>
                    </#if>
                </#list>
            </div>
        <#elseif player.oldCardAction.buttonValue != "">
            <div style="clear:both;padding-bottom:10px;font-size:20px;"><input type="button" onclick="submitCardAction()" value="${player.oldCardAction.buttonValue}"/></div>
        </#if>
        <#if player.oldCardAction.cards?size != 0>
            <div style="float:left;">
                <#assign clickType="oldCardAction">
                <#assign hideOnSelect = player.oldCardAction.hideOnSelect>
                <#list player.oldCardAction.cards as card>
                    <div style="float:left;padding-right:6px;padding-top:6px;">
                        <#include "gameCard.ftl">
                    </div>
                </#list>
            </div>
        </#if>
    </#if>
</div>