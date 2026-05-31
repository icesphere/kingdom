<div>
    <a href="showGameCards.html" target="_blank">Card Details</a>
</div>
<table>
    <tr>
        <#if !mobile>
            <td>
                <img src="images/Supply.png" alt="Supply"/>
            </td>
        </#if>
        <td>
            <table>
                <tr>
                    <#if kingdomCards?size == 11>
                        <td style="vertical-align:bottom;text-align:center;">(Bane Card)</td>
                    </#if>
                    <#list kingdomCards as card>
                        <#if card_index == 5>
                            </tr>
                            <tr>
                        </#if>
                        <#assign clickType="supply">
                        <td>
                            <table cellpadding="0" cellspacing="0">
                                <#if gameStatus == "InProgress">
                                    <tr><td style="font-size:10px;">${supply.get(card.pileName)}</td></tr>
                                </#if>
                                <tr><td><#include "gameCard.ftl"></td></tr>
                            </table>
                        </td>
                    </#list>
                </tr>
            </table>
        </td>
        <td>
            <table>
                <tr>
                    <#list supplyCards as card>
                        <#if (card_index == 3 && supplyCards?size == 7) || (card_index == 4 && supplyCards?size == 8) || (card_index == 4 && supplyCards?size == 9) || (card_index == 5 && supplyCards?size == 10)>
                            </tr>
                            <tr>
                        </#if>
                        <#assign clickType="supply">
                        <td>
                            <table cellpadding="0" cellspacing="0">
                                <#if gameStatus == "InProgress">
                                    <tr><td style="font-size:10px;">${supply.get(card.name)}</td></tr>
                                </#if>
                                <tr><td><#include "gameCard.ftl"></td></tr>
                            </table>
                        </td>
                    </#list>
                </tr>
            </table>
        </td>
        <#if eventsAndLandmarksAndProjectsAndWays?has_content>
            <td>
                <table>
                    <tr>
                        <#list eventsAndLandmarksAndProjectsAndWays as card>
                            <#if card_index == 1>
                                </tr>
                                <tr>
                            </#if>
                            <#if card.event>
                                <#assign clickType="event">
                            <#elseif card.landmark>
                                <#assign clickType="landmark">
                            <#elseif card.way>
                                <#assign clickType="way">
                            <#elseif card.ally>
                                <#assign clickType="ally">
                            <#elseif card.trait>
                                <#assign clickType="trait">
                            <#elseif card.prophecy>
                                <#assign clickType="prophecy">
                            <#else>
                                <#assign clickType="project">
                            </#if>
                            <td>
                                <table cellpadding="0" cellspacing="0">
                                    <#if gameStatus == "InProgress">
                                        <tr><td style="font-size:10px;"><#if card.event>Event<#elseif card.landmark>Landmark<#elseif card.project>Project<#elseif card.ally>Ally<#elseif card.trait>Trait<#elseif card.prophecy>Prophecy<#else>Way</#if></td></tr>
                                    </#if>
                                    <tr><td><#include "gameCard.ftl"></td></tr>
                                </table>
                            </td>
                        </#list>
                    </tr>
                </table>
            </td>
        </#if>
    </tr>
</table>
