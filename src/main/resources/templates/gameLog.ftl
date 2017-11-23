<!DOCTYPE html>
<html>
	<head>
		<title>Kingdom - Game Log</title>
        <#include "commonIncludes.ftl">
        <link href="css/game.css" rel="stylesheet" type="text/css">
	</head>
	<body>
        <h3>Game Log</h3>
        <table>
            <#list logs as log>
                <tr>
                    <td>${log}</td>
                </tr>
            </#list>
            <#if logNotFound>
                Game Log Not Found
            </#if>
        </table>
	</body>
</html>
