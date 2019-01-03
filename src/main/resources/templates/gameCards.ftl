<!DOCTYPE html>
<html>
<head>
    <title>Card Details</title>
    <#include "commonIncludes.ftl">
    <link href="css/game.css" rel="stylesheet" type="text/css">
</head>
<body>
<div class="topGradient"></div>
<div style="padding:15px;float:left;">
    <#assign clickType="info">
    <#assign gameStatus = "InProgress">
    <#assign card_index = 1>
    <#assign mobile = false>

    <#if events?has_content>
        <div style="clear:both;float:left;">
            Kingdom Cards:
        </div>
    </#if>

    <div style="clear: both; float: left">
        <#list cards as card>
            <div style="float:left;padding-right:2px;padding-top:2px;">
                <#include "gameCard.ftl">
            </div>
        </#list>
    </div>

    <#if events?has_content>
        <div style="clear:both;float:left;">
            Events:
        </div>
        <div style="clear:both;">
            <#list events as card>
                <div style="float:left;padding-right:2px;padding-top:2px;">
                    <#include "gameCard.ftl">
                </div>
            </#list>
        </div>
    </#if>

    <#if prizeCards?size != 0>
        <div style="clear:both;float:left;">
            Prize Cards:
        </div>
        <div style="clear:both;">
            <#list prizeCards as card>
                <div style="float:left;padding-right:2px;padding-top:2px;">
                    <#include "gameCard.ftl">
                </div>
            </#list>
        </div>
    </#if>
</div>
<#if includesColonyAndPlatinum>
    <div style="clear:both;float:left;padding:15px;">
        Colony and Platinum are included
    </div>
</#if>
<#include "footer.ftl">
</body>
</html>
