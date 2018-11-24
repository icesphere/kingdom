<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Kingdom</title>
    <#include "commonIncludes.ftl">
    <script type="text/javascript">
        $(document).ready(function() {
            $("#username").focus();    
        });
    </script>
</head>
<body>
    <#if mobile>
        <div class="topGradient"></div>
        <div style="padding-top:10px; text-align:center; padding:20px;">
            <div style="padding-top:10px;">
                <form action="login.html" method="POST" name="loginForm" style="padding-right:20px;">
                    <table style="text-align:center;">
                        <#if usernameBeingUsed?? && usernameBeingUsed>
                            <tr>
                                <td colspan="2" style="color: #990000; font-size: 12px;">Username being used - pick a different username</td>
                            </tr>
                        </#if>
                        <tr>
                            <td class="loginLabel">Username:</td>
                            <td><input class="loginField" type="text" id="username" name="username"/> </td>
                        </tr>
                        <tr>
                            <td colspan="2"><input class="loginButton" type="submit" value="Login"></td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>
    <#else>
        <div class="topGradient"></div>
        <div style="padding-top:10px;">
            <div style="float:left">
                <img src="images/castle_small.jpg" alt="castle"/>
            </div>
            <div style="float:left; padding-left:20px; padding-top:10px;">
                <form action="login.html" method="POST" name="loginForm">
                    <table>
                        <#if usernameBeingUsed?? && usernameBeingUsed>
                            <tr>
                                <td colspan="2" style="color: #990000; font-size: 12px;">Username being used - pick a different username</td>
                            </tr>
                        </#if>
                        <tr>
                            <td>Username:</td>
                            <td><input type="text" id="username" name="username"/> </td>
                        </tr>
                        <tr>
                            <td colspan="2"><input type="submit" value="Login"></td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>
    </#if>
    <#include "footer.ftl">
</body>
</html>