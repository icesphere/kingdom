<div style="padding-top:10px;">
    <div style="float:left">
        <img src="images/castle_small.jpg" alt="castle"/>
    </div>
    <div style="float:left; padding-left:20px; padding-top:10px;">
        <#if error != "">
            <div style="color:red">${error}</div>
        </#if>
        <form action="submitAccountRequest.html" method="POST" name="requestAccountForm" id="requestAccountForm">
            <table>
                <tr>
                    <td style="text-align:right;">Email:</td>
                    <td><input type="text" id="email" name="email"/> </td>
                </tr>
                <tr>
                    <td colspan="2" style="font-size:10px;">
                        (A temporary password will be sent to the above email address)
                    </td>
                </tr>
                <tr>
                    <td style="text-align:right;">Username:</td>
                    <td><input type="text" id="username" name="username"/> </td>
                </tr>
                <tr>
                    <td colspan="2" style="font-size:10px;">
                        (Used to login, also used as your player's name)
                    </td>
                </tr>
                <tr>
                    <td style="text-align:right;">Gender:</td>
                    <td>
                        <input type="radio" name="gender" id="gender" value="M" checked/> Male  <input type="radio" name="gender" value="F"/> Female
                    </td>
                </tr>
                <tr>
                    <td colspan="2" style="font-size:10px;">
                        (Used for game messages, e.g. John added a card to the top of his deck)
                    </td>
                </tr>
                <tr>
                    <td style="padding-top:20px;padding-bottom:10px;"><input id="submitButton" type="button" value="Submit" onclick="requestAccount()"></td>
                    <td style="padding-top:20px;padding-bottom:10px;"><input id="cancelButton" type="button" value="Cancel" onclick="cancel()"></td>
                </tr>
            </table>
            <div id="sendingEmailDiv" style="color:blue;padding-top:10px;display:none;">
                Sending Email...
            </div>
        </form>
    </div>
</div>