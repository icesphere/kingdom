<div style="float:left; padding:10px;">
    <#if error != "">
        <div style="color:red">${error}</div>
    </#if>
    <form action="submitAccountRequest.html" method="POST" name="requestAccountForm" id="requestAccountForm" style="padding-right:20px;">
        <table>
            <tr>
                <td style="text-align:right;" class="loginLabel">Username:</td>
                <td><input class="loginField" type="text" id="username" name="username"/> </td>
            </tr>
            <tr>
                <td colspan="2" style="font-size:12px;">
                    (Used to login, also used as your player's name)
                </td>
            </tr>
            <tr>
                <td style="text-align:right;" class="loginLabel">Gender:</td>
                <td>
                    <input type="radio" name="gender" id="gender" value="M" checked/> Male  <input type="radio" name="gender" value="F"/> Female
                </td>
            </tr>
            <tr>
                <td colspan="2" style="font-size:12px;">
                    (Used for game messages, e.g. John added a card to the top of his deck)
                </td>
            </tr>
            <tr>
                <td style="padding-top:20px;padding-bottom:10px;"><input class="loginButton" id="submitButton" type="button" value="Submit" onclick="requestAccount()"></td>
                <td style="padding-top:20px;padding-bottom:10px;"><input class="loginButton" id="cancelButton" type="button" value="Cancel" onclick="cancel()"></td>
            </tr>
        </table>
    </form>
</div>
