<!DOCTYPE html>
<html>
<head>
    <title>Recommended Set</title>
    <#include "commonIncludes.ftl">
    <script type="text/javascript">
        function saveRecommendedSet(){
            if($("#name").val() == "") {
                alert("Name is required");
            }
            else if($("#deck").val() == "") {
                alert("Deck is required");
            }
            else if($("#cards").val() == "") {
                alert("Cards are required");
            }
            else {
                document.forms["recommendedSetForm"].submit();
            }
        }

        function deleteRecommendedSet(){
            if(confirm("Are you sure you want to delete this recommended set?"))
            {
                document.forms["recommendedSetForm"].action = "deleteRecommendedSet.html";
                document.forms["recommendedSetForm"].submit();
            }
        }
    </script>
</head>
<body>
<form action="saveRecommendedSet.html" method="POST" name="recommendedSetForm">
    <input type="hidden" name="id" value="${set.id}"/>
    <table>
        <tr>
            <td>
                Name: <input type="text" id="name" name="name" value="${set.name}"/>
            </td>
        </tr>
        <tr>
            <td>
                Deck:
                        <select name="deck">
                            <option value="Base Set" <#if set.deck == "Base Set">selected="true"</#if>>Base Set</option>
                            <option value="Intrigue" <#if set.deck == "Intrigue">selected="true"</#if>>Intrigue</option>
                            <option value="Intrigue & Base" <#if set.deck == "Intrigue & Base">selected="true"</#if>>Intrigue & Base</option>
                            <option value="Seaside" <#if set.deck == "Seaside">selected="true"</#if>>Seaside</option>
                            <option value="Seaside & Base" <#if set.deck == "Seaside & Base">selected="true"</#if>>Seaside & Base</option>
                            <option value="Alchemy & Base" <#if set.deck == "Alchemy & Base">selected="true"</#if>>Alchemy & Base</option>
                            <option value="Alchemy & Intrigue" <#if set.deck == "Alchemy & Intrigue">selected="true"</#if>>Alchemy & Intrigue</option>
                            <option value="Prosperity" <#if set.deck == "Prosperity">selected="true"</#if>>Prosperity</option>
                            <option value="Prosperity & Base" <#if set.deck == "Prosperity & Base">selected="true"</#if>>Prosperity & Base</option>
                            <option value="Prosperity & Intrigue" <#if set.deck == "Prosperity & Intrigue">selected="true"</#if>>Prosperity & Intrigue</option>
                            <option value="Cornucopia & Base" <#if set.deck == "Cornucopia & Base">selected="true"</#if>>Cornucopia & Base</option>
                            <option value="Cornucopia & Intrigue" <#if set.deck == "Cornucopia & Intrigue">selected="true"</#if>>Cornucopia & Intrigue</option>
                            <option value="Hinterlands" <#if set.deck == "Hinterlands">selected="true"</#if>>Hinterlands</option>
                            <option value="Hinterlands & Base" <#if set.deck == "Hinterlands & Base">selected="true"</#if>>Hinterlands & Base</option>
                            <option value="Hinterlands & Intrigue" <#if set.deck == "Hinterlands & Intrigue">selected="true"</#if>>Hinterlands & Intrigue</option>
                            <option value="Hinterlands & Seaside" <#if set.deck == "Hinterlands & Seaside">selected="true"</#if>>Hinterlands & Seaside</option>
                            <option value="Hinterlands & Alchemy" <#if set.deck == "Hinterlands & Alchemy">selected="true"</#if>>Hinterlands & Alchemy</option>
                            <option value="Hinterlands & Prosperity" <#if set.deck == "Hinterlands & Prosperity">selected="true"</#if>>Hinterlands & Prosperity</option>
                            <option value="Hinterlands & Cornucopia" <#if set.deck == "Hinterlands & Cornucopia">selected="true"</#if>>Hinterlands & Cornucopia</option>
                            <option value="Other" <#if set.deck == "Other">selected="true"</#if>>Other</option>
                        </select>
            </td>
        </tr>
        <tr>
            <td>
                Cards: <input type="text" id="cards" name="cards" value="${set.cards}"/>
            </td>
        </tr>
    </table>
    <table style="padding-top:10px;">
        <tr>
            <td><a href="javascript:saveRecommendedSet()">Save</a></td>
            <td style="padding-left:10px;"><a href="javascript:deleteRecommendedSet()">Delete</a></td>
            <td style="padding-left:10px;"><a href="recommendedSets.html">Cancel</a></td>
        </tr>
    </table>
</form>
</body>
</html>
