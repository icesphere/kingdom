<!DOCTYPE html>
<html>
<head>
    <title>Annotated Game</title>
    <#include "commonIncludes.ftl">
    <script type="text/javascript">
        function saveAnnotatedGame(){
            var cardsSelected = $("input[name^='card_']:checked:visible").length;
            if(cardsSelected != 10) {
                alert("You need to select 10 cards");
            }
            else if($("#title").val() == "") {
                alert("Title is required");
            }
            else {
                document.forms["annotatedGameForm"].submit();
            }
        }

        function deleteAnnotatedGame(){
            if(confirm("Are you sure you want to delete this annotated game?"))
            {
                document.forms["annotatedGameForm"].action = "deleteAnnotatedGame.html";
                document.forms["annotatedGameForm"].submit();
            }
        }

        function selectCard() {
            var cardsSelected = $("input[name^='card_']:checked:visible").length;
            $("#numCardsSelected").html(cardsSelected);
        }

        $(document).ready(function() {
            selectCard();
        });
    </script>
</head>
<body>
<form action="saveAnnotatedGame.html" method="POST" name="annotatedGameForm">
    <input type="hidden" name="id" value="${game.gameId}"/>
    <table>
        <tr>
            <td>
                Title: <input type="text" id="title" name="title" value="${game.title}"/>
            </td>
        </tr>

        <tr>
            <td class="label" style="padding-top:10px;">
                Cards:
            </td>
        </tr>
        <tr>
            <td>
                <div style="float:left;">
                    <div style="float:left;padding-left:10px;color:red;">Cards selected:</div><div id="numCardsSelected" style="float:left; padding-left:5px; color:red">0</div>
                </div>
            </td>
        </tr>
        <tr>
            <td style="padding-left:10px;">
                <table>
                    <tr>
                        <td style="vertical-align:top">
                            <div id="kingdomCardsDiv" style="width:200px;">
                                <table>
                                    <tr>
                                        <td>
                                            Base Set
                                        </td>
                                    </tr>
                                    <#list kingdomCards as card>
                                        <#include "annotatedGameCardRow.ftl">
                                    </#list>
                                </table>
                            </div>
                        </td>
                        <td style="vertical-align:top">
                            <div id="intrigueCardsDiv" style="width:200px;">
                                <table>
                                    <tr>
                                        <td>
                                            Intrigue
                                        </td>
                                    </tr>
                                    <#list intrigueCards as card>
                                        <#include "annotatedGameCardRow.ftl">
                                    </#list>
                                </table>
                            </div>
                        </td>
                        <td style="vertical-align:top">
                            <div id="seasideCardsDiv" style="width:200px;">
                                <table>
                                    <tr>
                                        <td>
                                            Seaside
                                        </td>
                                    </tr>
                                    <#list seasideCards as card>
                                        <#include "annotatedGameCardRow.ftl">
                                    </#list>
                                </table>
                            </div>
                        </td>
                        <td style="vertical-align:top">
                            <div id="prosperityCardsDiv" style="width:200px;">
                                <table>
                                    <tr>
                                        <td>
                                            Prosperity
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding-bottom:10px;">
                                            <input type="checkbox" name="includeColonyAndPlatinumCards" id="includeColonyAndPlatinumCards" value="true" <#if game.includeColonyAndPlatinum>checked</#if>/> <label for="includeColonyAndPlatinumCards">Include Colony/Platinum</label>
                                        </td>
                                    </tr>
                                    <#list prosperityCards as card>
                                        <#include "annotatedGameCardRow.ftl">
                                    </#list>
                                </table>
                            </div>
                        </td>
                        <td style="vertical-align:top">
                            <div id="promoCardsDiv" style="width:200px;">
                                <table>
                                    <tr>
                                        <td>
                                            Promo Cards
                                        </td>
                                    </tr>
                                    <#list promoCards as card>
                                        <#include "annotatedGameCardRow.ftl">
                                    </#list>
                                </table>
                            </div>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    <table style="padding-top:10px;">
        <tr>
            <td><a href="javascript:saveAnnotatedGame()">Save</a></td>
            <td style="padding-left:10px;"><a href="javascript:deleteAnnotatedGame()">Delete</a></td>
            <td style="padding-left:10px;"><a href="annotatedGames.html">Cancel</a></td>
        </tr>
    </table>
</form>
</body>
</html>
