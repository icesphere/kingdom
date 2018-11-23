<!DOCTYPE html>
<html>
	<head>
		<title>User</title>
        <#include "commonIncludes.ftl">
        <script type="text/javascript">
            function saveUser(){
                document.forms["userForm"].submit();
            }
            function deleteUser(){
                if(confirm("Are you sure you want to delete this user?"))
                {
                    document.forms["userForm"].action = "deleteUser.html";
                    document.forms["userForm"].submit();
                }
            }
        </script>
	</head>
	<body>
        <form action="saveUser.html" method="POST" name="userForm">
            <input type="hidden" name="id" value="${user.userId}"/>
            <table>
                <#if user.userId != 0>
                    <tr>
                        <td>
                            Last Login: ${user.lastLogin}
                        </td>
                    </tr>
                </#if>
                <tr>
                    <td>
                        Username: <input type="text" name="username" value="${user.username}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        Password: <input type="password" name="password" value="${user.password}"/>
                    </td>
                </tr>
            </table>
            <table style="padding-top:10px;">
                <tr>
                    <td><a href="javascript:saveUser()">Save</a></td>
                    <#if user.userId != 0 && !user.admin>
                        <td style="padding-left:10px;"><a href="javascript:deleteUser()">Delete</a></td>
                    </#if>
                    <td style="padding-left:10px;"><a href="listUsers.html">Cancel</a></td>
                </tr>
            </table>
        </form>
	</body>
</html>
