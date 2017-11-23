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
                        <tr>
                            <td class="loginLabel">Username:</td>
                            <td><input class="loginField" type="text" id="username" name="username"/> </td>
                        </tr>
                        <tr>
                            <td class="loginLabel">Password:</td>
                            <td><input class="loginField" type="password" name="password"/> </td>
                        </tr>
                        <tr>
                            <td><input class="loginButton" type="submit" value="Login"></td>
                            <td style="padding-left:10px;"><a href="forgotLogin.html">Forgot Login?</a></td>
                        </tr>
                        <tr>
                            <td colspan="2" style="padding-top:10px;">
                                <a href="requestAccount.html">Create Account</a>
                            </td>
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
                        <tr>
                            <td>Username:</td>
                            <td><input type="text" id="username" name="username"/> </td>
                        </tr>
                        <tr>
                            <td>Password:</td>
                            <td><input type="password" name="password"/> </td>
                        </tr>
                        <tr>
                            <td><input type="submit" value="Login"></td>
                            <td style="padding-left:10px;"><a href="forgotLogin.html">Forgot Login?</a></td>
                        </tr>
                        <tr>
                            <td colspan="2" style="padding-top:10px;">
                                <a href="requestAccount.html">Create Account</a>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>
    </#if>
    <#include "footer.ftl">
</body>
</html>