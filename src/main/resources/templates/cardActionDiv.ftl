<div id="cardActionDialog_" class="cardActionDialog" style="display:none" title="">
    <#if player.showCardAction>
        <div style="font-size:14px;"><p>${player.oldCardAction.instructions}</p></div>
        <#if player.oldCardAction.type == 11>
            <div style="padding-bottom:10px;"><table><tr><td><input type="button" onclick="submitCardActionYesNo('yes')" value="Yes"/></td><td style="padding-left:10px;"><input type="button" onclick="submitCardActionYesNo('no')" value="No"/></td></tr></table></div>
        <#elseif player.oldCardAction.type == 14>
            <div style="padding-bottom:10px;">
                <table>
                    <tr>
                        <#list player.oldCardAction.choices as choice>
                            <#if choice_index == 3>
                                </tr>
                                <tr>
                            </#if>
                            <td <#if choice_index != 0 && choice_index != 3>style="padding-left:10px;"</#if>><input type="button" onclick="submitCardActionChoice('${choice.value}')" value="${choice.button}"/></td>
                        </#list>
                    </tr>
                </table>
            </div>
        <#elseif player.oldCardAction.type == 19>
            <div style="padding-bottom:10px;font-size:14px;">
                <#list player.oldCardAction.startNumber..player.oldCardAction.endNumber as i>
                    <span style="padding-right:10px;"><a style="color:blue" href="javascript:submitCardActionChooseNumberBetween(${i})">${i}</a></span>
                </#list>
            </div>
        <#elseif player.oldCardAction.type == 23>
            <div style="padding-bottom:10px;font-size:14px;">
                <#list player.oldCardAction.startNumber..player.oldCardAction.endNumber as i>
                    <#if i % 2 == 0>
                        <span style="padding-right:10px;"><a style="color:blue" href="javascript:submitCardActionChooseNumberBetween(${i})">${i}</a></span>
                    </#if>
                </#list>
            </div>
        <#elseif player.oldCardAction.buttonValue != "">
            <div style="padding-bottom:10px;"><input type="button" onclick="submitCardAction()" value="${player.oldCardAction.buttonValue}"/></div>
        </#if>
        <#if player.oldCardAction.cards?size != 0>
            <table>
                <tr>
                    <#assign clickType="oldCardAction">
                    <#assign hideOnSelect = player.oldCardAction.hideOnSelect>
                    <#list player.oldCardAction.cards as card>
                        <#if card_index % 7 == 0>
                            </tr>
                            <tr>
                        </#if>    
                        <td><#include "gameCard.ftl"></td>
                    </#list>
                </tr>
            </table>
        </#if>
    </#if>
</div>