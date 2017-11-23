<!DOCTYPE html>
<html>
<head>
    <title>Kingdom</title>
    <#include "commonIncludes.ftl">
    <script type="text/javascript">
        function changeTemporaryPassword() {
            if($.trim($("#password").val()) == "") {
                alert("New Password required");
            }
            else if($("#password").val() != $("#confirmPassword").val()) {
                alert("Passwords do not match");
            }
            else {
                $("#changeTemporaryPasswordForm").submit();
            }
        }
    </script>
</head>
<body>
<div class="topGradient"></div>
<div style="padding-top:10px;">
    <div style="padding-bottom:10px;">
        <span style="font-weight:bold">Change Temporary Password</span>
    </div>
    <form action="changeTemporaryPassword.html" method="POST" name="changeTemporaryPasswordForm" id="changeTemporaryPasswordForm">
        <table>
            <tr>
                <td>New Password:</td>
                <td><input type="password" name="password" id="password"/> </td>
            </tr>
            <tr>
                <td>Confirm Password:</td>
                <td><input type="password" name="confirmPassword" id="confirmPassword"/> </td>
            </tr>
            <tr>
                <td colspan="2" style="padding-top:10px;">
                    <input type="button" onclick="changeTemporaryPassword()" value="Submit"/>
                </td>
            </tr>
        </table>
    </form>
</div>
<#include "footer.ftl">
</body>
</html>