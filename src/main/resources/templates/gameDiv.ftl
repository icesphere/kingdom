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

    <div id="infoMessageDiv" style="display: none;">
        <#include "infoMessageDiv.ftl">
    </div>

</div>

<#if mobile>
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
        <#include "historyDivMobile.ftl">
    </div>
</#if>

    