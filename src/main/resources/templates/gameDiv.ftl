<div id="topLineDiv">
    <#if mobile>
        <#include "topLineDivMobile.ftl">
    <#else>
        <#include "topLineDiv.ftl">
    </#if>
</div>

<div id="supplyAreaDiv">
    <#include "supplyAreaDiv.ftl">
</div>

<div style="position: relative">

    <div id="playerAreaDiv">
        <#if mobile>
            <#include "playerAreaDivMobile.ftl">
        <#else>
            <#include "playerAreaDiv.ftl">
        </#if>

    </div>

    <div id="cardActionDiv">
        <#include "cardActionDiv.ftl">
    </div>

    <div class="infoMessageContainer">

        <div id="infoMessageDiv1" style="display: none;">
            <#assign messageSectionNumber = 1>
            <#include "infoMessageDiv.ftl">
        </div>

        <div id="infoMessageDiv2" style="display: none;">
            <#assign messageSectionNumber = 2>
            <#include "infoMessageDiv.ftl">
        </div>

        <div id="infoMessageDiv3" style="display: none;">
            <#assign messageSectionNumber = 3>
            <#include "infoMessageDiv.ftl">
        </div>

        <div id="infoMessageDiv4" style="display: none;">
            <#assign messageSectionNumber = 4>
            <#include "infoMessageDiv.ftl">
        </div>

        <div id="infoMessageDiv5" style="display: none;">
            <#assign messageSectionNumber = 5>
            <#include "infoMessageDiv.ftl">
        </div>

        <div id="infoMessageDiv6" style="display: none;">
            <#assign messageSectionNumber = 6>
            <#include "infoMessageDiv.ftl">
        </div>

    </div>

    <div id="showCardsDiv" style="display: none;">
        <#include "showCardsDiv.ftl">
    </div>

    <div id="gameInfoDiv" style="display: none;>
        <#include "gameInfoDiv.ftl">
    </div>

</div>

<#if mobile && showVictoryPoints>
    <div style="clear:both;padding-top:5px;" class="label">Players:</div>
    <div id="playersDiv">
        <#include "playersDivMobile.ftl">
    </div>
</#if>

<#if !allComputerOpponents && mobile>
    <div style="clear:both;" class="label">Chat:</div>
    <div id="chatDiv" style="clear:both;">
        <#include "chatDivMobile.ftl">
    </div>
    <div style="clear:both;" onkeypress="return checkEnterOnAddChat(event)">
        <input type="text" name="chatMessage" id="chatMessage" style="width:150px;"/> <input type="button" id="sendChatButton" value="Send" onclick="sendChat()"/>
    </div>
</#if>

<#if mobile>
    <div style="clear:both;" class="label">History:</div>
    <div id="historyDiv" style="clear:both;">
        <#include "historyDiv.ftl">
    </div>
</#if>

    