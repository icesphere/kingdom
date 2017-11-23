<!DOCTYPE html>
<html>
<head>
    <title>Kingdom - Forgot Login</title>
    <#include "commonIncludes.ftl">
    <script type="text/javascript">
        $(document).ready(function() {
            $("#email").focus();
        });

        function forgotLogin() {
            if($.trim($("#email").val()) == "") {
                alert("Email required");
            }
            else {
                $("#submitButton").hide();
                $("#cancelButton").hide();
                $("#sendingEmailDiv").show();
                $("#forgotLoginForm").submit();
            }
        }

        function cancel() {
            document.location = "login.html";
        }
    </script>
</head>
<body>
<div class="topGradient"></div>
<div style="padding-top:10px;">
    <div style="float:left">
        <img src="images/castle_small.jpg" alt="castle"/>
    </div>
    <div style="float:left; padding-left:20px; padding-top:10px;">
    <#if error != "">
        <div style="color:red">${error}</div>
    </#if>
    <form action="submitForgotLogin.html" method="POST" name="forgotLoginForm" id="forgotLoginForm">
        <table>
            <tr>
                <td style="text-align:right;">Email:</td>
                <td><input type="text" id="email" name="email"/> </td>
            </tr>
            <tr>
                <td style="padding-top:10px;"><input id="submitButton" type="button" value="Submit" onclick="forgotLogin()"></td>
                <td style="padding-top:10px;"><input id="cancelButton" type="button" value="Cancel" onclick="cancel()"></td>
            </tr>
        </table>
        <div id="sendingEmailDiv" style="color:blue;padding-top:10px;display:none;">
            Sending Email...
        </div>
    </form>
    </div>
</div>
<#include "footer.ftl">
</body>
</html>