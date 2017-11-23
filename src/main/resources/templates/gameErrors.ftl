<!DOCTYPE html>
<html>
	<head>
		<title>Kingdom</title>
        <#include "commonIncludes.ftl">
	</head>
	<body>
        <#include "adminLinks.ftl">
        <h3>Game Errors</h3>
        <table cellpadding="3" border="1">
            <tr>
                <th>ID</th>
                <th>Type</th>
                <th>Date</th>
                <th>Error</th>
                <th>History</th>
                <th>Delete</th>
            </tr>
            <#list errors as e>
                <tr>
                    <td>${e.errorId}</td>
                    <td>${e.type}</td>
                    <td>${e.date}</td>
                    <td>${e.error}</td>
                    <td>${e.history}</td>
                    <td><a href="deleteGameError.html?errorId=${e.errorId}">Delete</a></td>
                </tr>
            </#list>
        </table>
	</body>
</html>
