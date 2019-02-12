<#include "commonIncludes.ftl">
<#if mobile>
    <link href="css/game.css?5" rel="stylesheet" type="text/css">
    <link href="css/gameMobile.css" rel="stylesheet" type="text/css">
    <script type="text/javascript">
        var mobile = true;
    </script>
<#else>
    <link href="css/game.css?3" rel="stylesheet" type="text/css">
    <script type="text/javascript">
        var mobile = false;
    </script>
</#if>
<script type="text/javascript" src="js/game.js?12"></script>