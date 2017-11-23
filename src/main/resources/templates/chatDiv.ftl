<table style="width:100%">
    <#list chats as chat>
        <tr><td style="color:${chat.color}">${chat.message}</td></tr>
    </#list>
</table>