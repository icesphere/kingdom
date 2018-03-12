<!DOCTYPE html>
<html>
	<head>
		<title>Cards</title>
        <#include "commonIncludes.ftl">
	</head>
	<body>
        <#include "adminLinks.ftl">
		<div>Cards</div>
		<table>
            <tr>
                <td style="padding-top:10px;">
                    <a href="showCard.html?id=0">Create New Card</a>
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
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if><#if card.disabled> (disabled)</#if>
                                                </td>
                                            </tr>
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
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if>
                                                </td>
                                            </tr>
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
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if>
                                                </td>
                                            </tr>
                                        </#list>
                                    </table>
                                </div>
                            </td>
                            <td style="vertical-align:top">
                                <div id="alchemyCardsDiv" style="width:200px;">
                                    <table>
                                        <tr>
                                            <td>
                                                Alchemy
                                            </td>
                                        </tr>
                                        <#list alchemyCards as card>
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if>
                                                </td>
                                            </tr>
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
                                        <#list prosperityCards as card>
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if>
                                                </td>
                                            </tr>
                                        </#list>
                                    </table>
                                </div>
                            </td>
                            <td style="vertical-align:top">
                                <div id="cornucopiaCardsDiv" style="width:200px;">
                                    <table>
                                        <tr>
                                            <td>
                                                Cornucopia
                                            </td>
                                        </tr>
                                        <#list cornucopiaCards as card>
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if>
                                                </td>
                                            </tr>
                                        </#list>
                                    </table>
                                </div>
                            </td>
                            <td style="vertical-align:top">
                                <div id="hinterlandsCardsDiv" style="width:200px;">
                                    <table>
                                        <tr>
                                            <td>
                                                Hinterlands
                                            </td>
                                        </tr>
                                        <#list hinterlandsCards as card>
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if>
                                                </td>
                                            </tr>
                                        </#list>
                                    </table>
                                </div>
                            </td>
                            <td style="vertical-align:top">
                                <div id="prizeCardsDiv" style="width:200px;">
                                    <table>
                                        <tr>
                                            <td>
                                                Prize Cards
                                            </td>
                                        </tr>
                                        <#list prizeCards as card>
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if>
                                                </td>
                                            </tr>
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
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if><#if card.disabled> (disabled)</#if>
                                                </td>
                                            </tr>
                                        </#list>
                                    </table>
                                </div>
                            </td>
                            <td style="vertical-align:top">
                                <div id="salvationCardsDiv" style="width:200px;">
                                    <table>
                                        <tr>
                                            <td>
                                                Salvation
                                            </td>
                                        </tr>
                                        <#list salvationCards as card>
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if><#if card.disabled> (disabled)</#if>
                                                </td>
                                            </tr>
                                        </#list>
                                    </table>
                                </div>
                            </td>
                            <td style="vertical-align:top">
                                <div id="fairyTaleCardsDiv" style="width:200px;">
                                    <table>
                                        <tr>
                                            <td>
                                                Fairy Tale
                                            </td>
                                        </tr>
                                        <#list fairyTaleCards as card>
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if><#if card.disabled> (disabled)</#if>
                                                </td>
                                            </tr>
                                        </#list>
                                    </table>
                                </div>
                            </td>
                            <td style="vertical-align:top">
                                <div id="proletariatCardsDiv" style="width:200px;">
                                    <table>
                                        <tr>
                                            <td>
                                                Proletariat
                                            </td>
                                        </tr>
                                        <#list proletariatCards as card>
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if><#if card.disabled> (disabled)</#if>
                                                </td>
                                            </tr>
                                        </#list>
                                    </table>
                                </div>
                            </td>
                            <td style="vertical-align:top">
                                <div id="fanCardsDiv" style="width:200px;">
                                    <table>
                                        <tr>
                                            <td>
                                                Other Fan Cards
                                            </td>
                                        </tr>
                                        <#list fanCards as card>
                                            <tr>
                                                <td>
                                                    <a href="showCard.html?id=${card.name}">${card.name}</a><#if card.testing> (testing)</#if><#if card.disabled> (disabled)</#if>
                                                </td>
                                            </tr>
                                        </#list>
                                    </table>
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
		</table>
	</body>
</html>
		