<h3>Lobby Chat</h3>
<table style="width:100%">
    <tr>
        <td style="height:230px;">
            <div id="chatDiv" style="height:190px; overflow:auto">
                <table style="width:100%">
                    <#list chats as chat>
                        <#if !chat.expired>
                            <#if chat.userId == 0 || chat.userId == user.userId>
                                <tr><td>${chat.chat}</td></tr>
                            </#if>
                        </#if>
                    </#list>
                </table>
            </div>
            <div onkeypress="return checkEnterOnAddChat(event)">
                <input type="text" name="chatMessage" id="chatMessage" style="width:150px;"/> <input type="button" id="sendChatButton" value="Send" onclick="sendChat()"/>
            </div>
        </td>
    </tr>
</table>