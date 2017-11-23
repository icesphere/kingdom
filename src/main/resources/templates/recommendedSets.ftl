<!DOCTYPE html>
<html>
<head>
    <title>Recommended Sets</title>
    <#include "commonIncludes.ftl">
</head>
<body>
    <#include "adminLinks.ftl">
    <h3>Recommended Sets</h3>
    <table cellpadding="3" style="text-align:left">
        <tr>
            <td style="padding-bottom:10px;">
                <a href="showRecommendedSet.html?id=0">Create New Recommended Set</a>
            </td>
        </tr>
        <tr>
            <td style="font-weight:bold">Name</td>
            <td style="font-weight:bold">Deck</td>
            <td style="font-weight:bold">Cards</td>
        </tr>
        <#list recommendedSets as set>
            <tr>
                <td>
                    <a href="showRecommendedSet.html?id=${set.id}">${set.name}</a>
                </td>
                <td>
                    ${set.deck}
                </td>
                <td>
                    ${set.cards}
                </td>
            </tr>
        </#list>
    </table>
</body>
</html>
