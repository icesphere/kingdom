<div id="cardActionDialog_" class="cardActionDialog" style="display:none" title="">
    <#if player.showCardAction>
        <#if player.oldCardAction.type == 20>
            <div id="leadersRules" style="padding-bottom:10px;font-size:14px;">
                Rules: Leader cards are not part of the supply, and are never considered to be in play.
                <br/>
                Anytime after the beginning of your 3rd turn, you may active a leader by buying them during the buy phase.
            </div>
        </#if>
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
        <#if player.oldCardAction.type == 20>
            <div style="padding-top:10px;padding-bottom:10px;">
                <a style="color:#003399;" id="showKingdomCardsForLeadersLink" href="#" onclick="showKingdomCardsForLeaders()">Show Kingdom Cards</a>
            </div>
            <div id="kingdomCardsForLeaders" style="padding-top:10px;display:none;">
                <table>
                    <tr>
                        <#assign clickType="info">
                        <#assign gameStatus = 3>
                        <#assign costDiscount = 0>
                        <#assign fruitTokensPlayed = 0>
                        <#assign actionCardDiscount = 0>
                        <#assign showTrollTokens = false>
                        <#assign actionCardsInPlay = 0>
                        <#assign card_index = 1>
                        <#list kingdomCards as card>
                            <#if card_index % 7 == 0>
                                </tr>
                                <tr>
                            </#if>
                            <td><#include "gameCard.ftl"></td>
                        </#list>
                    </tr>
                </table>
                <#if includesColonyAndPlatinum?? && includesColonyAndPlatinum>
                    <div style="padding-top:10px;">
                        Colony and Platinum are included
                    </div>
                </#if>
            </div>
        </#if>
    </#if>
</div>