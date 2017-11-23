<!DOCTYPE html>
<html>
<head>
    <title>Kingdom - Create Account</title>
    <#include "commonIncludes.ftl">
    <script type="text/javascript">
        $(document).ready(function() {
            $("#email").focus();
        });

        function requestAccount() {
            if($.trim($("#email").val()) == "") {
                alert("Email required");
            }
            else if($.trim($("#username").val()) == "") {
                alert("Username required");
            }
            else {
                $("#submitButton").hide();
                $("#cancelButton").hide();
                $("#sendingEmailDiv").show();
                $("#requestAccountForm").submit();    
            }
        }

        function cancel() {
            document.location = "login.html";
        }
    </script>
</head>
<body>
<div class="topGradient"></div>
<#if mobile>
    <#include "requestAccountMobile.ftl">
<#else>
    <#include "requestAccountFull.ftl">
</#if>
<#include "footer.ftl">
</body>
</html>