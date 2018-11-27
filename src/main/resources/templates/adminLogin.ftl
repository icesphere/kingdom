<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Kingdom</title>
    <#include "commonIncludes.ftl">
    <script type="text/javascript">
        $(document).ready(function() {
            $("#adminPassword").focus();
        });
    </script>
</head>
<body>

    <div class="topGradient"></div>

    <form action="adminLogin.html" method="POST" name="adminLoginForm">

            <div style="padding: 20px;">

                <#if wrongPassword?? && wrongPassword>
                    <div style="color: #990000; font-size: 12px;">Wrong password</div>
                </#if>

                <div>

                    <span>Admin Password:</span>
                    <input type="password" id="adminPassword" name="adminPassword"/>

                    <input type="submit" value="Submit" style="margin-left:20px">

                </div>

            </div>

        </div>

    </form>

    <#include "footer.ftl">

</body>
</html>