<div id="cardActionDialog_" class="cardActionDialog" style="display:none;padding-bottom:30px;" title="">
    <#if player.showCardAction>
        <#if player.cardAction.type == 20>
            <a id="showLeadersRulesLink" href="#" onclick="showLeadersRules()" style="color:#003399;font-size:12px;">Show Rules</a>
            <div id="leadersRules" style="padding-bottom:10px;font-size:14px;display:none;">
                Rules: Leader cards are not part of the supply, and are never considered to be in play.
                <br/>
                Anytime after the beginning of your 3rd turn, you may active a leader by buying them during the buy phase.
            </div>
            <br/><a href="showLeaders.html" target="_blank" style="color:#003399;font-size:12px;">Show Leader Details</a>
            <br/><a href="showGameCards.html" target="_blank" style="color:#003399;font-size:12px;">Show Kingdom Card Details</a>
        </#if>
        <div style="font-size:14px;"><p>${player.cardAction.instructions}</p></div>
        <#if player.cardAction.type == 11>
            <div style="padding-bottom:10px;font-size:20px;"><table><tr><td><input type="button" onclick="submitCardActionYesNo('yes')" value="Yes"/></td><td style="padding-left:10px;"><input type="button" onclick="submitCardActionYesNo('no')" value="No"/></td></tr></table></div>
        <#elseif player.cardAction.type == 14>
            <div style="padding-bottom:10px;float:left;font-size:20px;">
                <#list player.cardAction.choices as choice>
                    <div style="float:left;padding-right:5px;padding-bottom:5px;">
                        <input type="button" onclick="submitCardActionChoice('${choice.value}')" value="${choice.button}"/>
                    </div>
                </#list>
            </div>
        <#elseif player.cardAction.type == 19>
            <div style="padding-bottom:10px;font-size:14px;float:left;">
                <#list player.cardAction.startNumber..player.cardAction.endNumber as i>
                    <div style="padding-right:5px;float:left;">
                        <a style="color:blue" href="javascript:submitCardActionChooseNumberBetween(${i})">${i}</a>
                    </div>
                </#list>
            </div>
        <#elseif player.cardAction.type == 23>
            <div style="padding-bottom:10px;font-size:14px;float:left;">
                <#list player.cardAction.startNumber..player.cardAction.endNumber as i>
                    <#if i % 2 == 0>
                        <div style="padding-right:5px;float:left;">
                            <a style="color:blue" href="javascript:submitCardActionChooseNumberBetween(${i})">${i}</a>
                        </div>
                    </#if>
                </#list>
            </div>
        <#elseif player.cardAction.buttonValue != "">
            <div style="clear:both;padding-bottom:10px;font-size:20px;"><input type="button" onclick="submitCardAction()" value="${player.cardAction.buttonValue}"/></div>
        </#if>
        <#if player.cardAction.cards?size != 0>
            <div style="float:left;">
                <#assign clickType="cardAction">
                <#assign hideOnSelect = player.cardAction.hideOnSelect>
                <#list player.cardAction.cards as card>
                    <div style="float:left;padding-right:6px;padding-top:6px;">
                        <#include "gameCard.ftl">
                    </div>
                </#list>
            </div>
        </#if>
        <#if player.cardAction.type == 20>
            <div style="padding-top:10px;padding-bottom:10px;">
                <a style="color:#003399;font-size:12px;" id="showKingdomCardsForLeadersLink" href="#" onclick="showKingdomCardsForLeaders()">Show Kingdom Cards Inline</a>
            </div>
            <div id="kingdomCardsForLeaders" style="padding-top:10px;display:none;">
                <div style="float:left;">
                    <#assign clickType="leadersSetup">
                    <#assign gameStatus = 3>
                    <#assign costDiscount = 0>
                    <#assign fruitTokensPlayed = 0>
                    <#assign actionCardDiscount = 0>
                    <#assign showTrollTokens = false>
                    <#assign actionCardsInPlay = 0>
                    <#assign card_index = 1>
                    <#list kingdomCards as card>
                        <div style="float:left;padding-right:6px;padding-top:6px;">
                            <#include "gameCard.ftl">
                        </div>
                    </#list>
                </div>
                <#if includesColonyAndPlatinum?? && includesColonyAndPlatinum>
                    <div style="padding-top:10px;font-size:12px;">
                        Colony and Platinum are included
                    </div>
                </#if>
            </div>
        </#if>
    </#if>
</div>