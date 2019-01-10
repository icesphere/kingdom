<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Kingdom</title>
    <#include "commonIncludes.ftl">
    <script type="text/javascript">
        $(document).ready(function() {
            $("#password").focus();
        });
    </script>
</head>
<body>

    <form action="access.html" method="POST" name="accessForm">

        <div style="display: flex; min-height: 130px;">

            <#if !mobile>
                <img src="images/castle_small.jpg" alt="castle" style="margin-right: 20px;"/>
            </#if>

            <div style="display: flex; flex-direction: column; justify-content: center; padding-left: 20px;">

                <#if accessDenied?? && accessDenied>
                    <span style="color: #990000; font-size: 12px;">Access denied!</span>
                </#if>

                <span>Enter password:</span>
                <input type="password" id="password" name="password" class="loginUsername <#if mobile>loginUsernameMobile</#if>"/>

                <input type="submit" value="Submit" style="margin-top:20px" class="loginButton <#if mobile>loginButtonMobile</#if>">

            </div>

        </div>

    </form>

</body>
</html>