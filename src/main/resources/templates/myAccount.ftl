<!DOCTYPE html>
<html>
<head>
    <title>Kingdom</title>
    <#include "commonIncludes.ftl">
    <script type="text/javascript">
        function saveMyAccountPassword() {
            if($.trim($("#currentPassword").val()) == "") {
                alert("Current Password required");
            }
            else if($.trim($("#password").val()) == "") {
                alert("New Password required");
            }
            else if($("#password").val() != $("#confirmPassword").val()) {
                alert("Passwords do not match");
            }
            else {
                $("#myAccountPasswordForm").submit();
            }
        }
        function saveMyAccount() {
            $("#myAccountForm").submit();
        }
    </script>
</head>
<body>
    <div>
        <div style="padding-bottom:10px;">
            <span style="font-weight:bold">Change Password</span>
        </div>
        <form action="saveMyAccountPassword.html" method="POST" name="myAccountPasswordForm" id="myAccountPasswordForm">
            <table>
                <tr>
                    <td>Current Password:</td>
                    <td><input type="password" name="currentPassword" id="currentPassword"/> </td>
                </tr>
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
                        <a href="javascript:saveMyAccountPassword()">Save</a>
                        &#160;<a href="showGameRooms.html">Cancel</a>
                    </td>
                </tr>
            </table>
        </form>
        <#if invalidPassword>
            <div style="color:red; padding-top:10px;">
                Invalid Password
            </div>
        </#if>
    </div>
    <div style="padding-top:10px;">
        <div style="padding-bottom:10px;">
            <span style="font-weight:bold">Game Settings</span>
        </div>
        <form action="saveMyAccount.html" method="POST" name="myAccountForm" id="myAccountForm">
            <table>
                <tr>
                    <td>Sound Default:</td>
                    <td><input type="radio" name="soundDefault" value="1" <#if user.soundDefault == 1>checked</#if>/>&#160;On&#160;&#160;<input type="radio" name="soundDefault" value="2" <#if user.soundDefault == 2>checked</#if>/>&#160;Off </td>
                </tr>
                <tr>
                    <td colspan="2" style="padding-top:10px;">
                        <a href="javascript:saveMyAccount()">Save</a>
                        &#160;<a href="showGameRooms.html">Cancel</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</body>
</html>