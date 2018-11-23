<#include "disclaimerFooter.ftl">
<#include "contactInfo.ftl">
<#if mobile??>
    <#if mobile>
        <a style="font-size:12px;" href="#" onclick="switchSite()">Switch to Full Site</a>
    <#else>
        <a style="font-size:12px;" href="#" onclick="switchSite()">Switch to Mobile Site</a>
    </#if>
</#if>