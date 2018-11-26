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

    <div class="topGradient"></div>

    <form action="login.html" method="POST" name="loginForm">

        <div style="display: flex; min-height: 130px;">

            <#if !mobile>
                <img src="images/castle_small.jpg" alt="castle" style="margin-right: 20px;"/>
            </#if>

            <div style="display: flex; flex-direction: column; justify-content: center; padding-left: 20px;">

                <#if usernameBeingUsed?? && usernameBeingUsed>
                    <span style="color: #990000; font-size: 12px;">Username being used - pick a different username</span>
                </#if>

                <span>Username:</span>
                <input type="text" id="username" name="username"/>

                <input type="submit" value="Log in" style="margin-top:20px">

            </div>

        </div>

    </form>

    <#include "footer.ftl">

</body>
</html>