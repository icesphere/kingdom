<h3>Lobby Chat</h3>
<div style="clear:left;float:left;">
    <div id="chatDiv">
        <#list chats as chat>
            <#if !chat.expired>
                <#if chat.userId == 0 || chat.userId == user.userId>
                    <div style="clear:both;">
                        ${chat.chat}
                    </div>
                </#if>
            </#if>
        </#list>
    </div>
    <div style="clear:both;" onkeypress="return checkEnterOnAddChat(event)">
        <input type="text" name="chatMessage" id="chatMessage" style="width:150px;"/> <input type="button" id="sendChatButton" value="Send" onclick="sendChat()"/>
    </div>
</div>