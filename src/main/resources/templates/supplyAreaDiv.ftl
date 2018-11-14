<table style="width:100%">
    <tr>
        <td>
            <div id="supplyDiv">
                <#if mobile>
                    <#include "supplyDivMobile.ftl">
                <#else>
                    <#include "supplyDiv.ftl">
                </#if>
            </div>
        </td>
        <#if !allComputerOpponents && !mobile>
            <td style="padding-left:20px;">
                <table style="width:100%; text-align:right">
                    <tr>
                        <td>
                            <img src="images/Chat.png" alt="Chat"/>
                        </td>
                        <td>
                            <table style="width:100%">
                                <tr>
                                    <td style="height:230px;">
                                        <div id="chatDiv" style="height:190px; overflow:auto">
                                            <#include "chatDiv.ftl">
                                        </div>
                                        <div onkeypress="return checkEnterOnAddChat(event)">
                                            <input type="text" name="chatMessage" id="chatMessage" style="width:150px;"/> <input type="button" id="sendChatButton" value="Send" onclick="sendChat()"/>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </#if>
    </tr>
    <#if showCardsNotInSupply>
        <tr>
            <td <#if !mobile>style="padding-left: 48px;"</#if>><a href="javascript:showCardsNotInSupply()">Show cards not in supply</a>
        </tr>
    </#if>
</table>