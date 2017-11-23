<!DOCTYPE html>
<html>
<head>
    <title>Annotated Games</title>
    <#include "commonIncludes.ftl">
</head>
<body>
    <#include "adminLinks.ftl">
    <h3>Annotated Games</h3>
    <table cellpadding="3" style="text-align:left">
        <tr>
            <td style="padding-bottom:10px;">
                <a href="showAnnotatedGame.html?id=0">Create New Annotated Game</a>
            </td>
        </tr>
        <tr>
            <td style="font-weight:bold">Title</td>
        </tr>
        <#list games as game>
            <tr>
                <td>
                    <a href="showAnnotatedGame.html?id=${game.gameId}">${game.title}</a>
                </td>
            </tr>
        </#list>
    </table>
</body>
</html>
