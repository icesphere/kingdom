<!DOCTYPE html>
<html>
	<head>
        <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
        <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <META HTTP-EQUIV="Expires" CONTENT="-1">
		<title>Kingdom</title>
        <#include "commonIncludes.ftl">
        <#if mobile>
            <script type="text/javascript" src="js/soundmanager2empty.js"></script>
            <link href="css/gameMobile.css" rel="stylesheet" type="text/css">
            <script type="text/javascript" >
                var playSound = false;
                var mobile = true;
            </script>
        <#else>
            <script type="text/javascript" src="js/soundmanager2-nodebug-jsmin.js"></script>
            <link href="css/game.css" rel="stylesheet" type="text/css">
            <script type="text/javascript" >
                var playSound = <#if user.soundDefault == 1>true<#else>false</#if>;
                var mobile = false;
            </script>
        </#if>
        <script type="text/javascript" src="js/game.js"></script>
	</head>
	<body>
        <div class="topGradient"></div>
        <div id="gameDiv">
            <#if gameStatus == "Finished">
                <#include "gameResults.ftl">
            <#else>
                <#include "gameDiv.ftl">
            </#if>
        </div>

        <div id="loadingDialog" class="loadingDialog" style="display:none" title="">
            Loading...
        </div>
        <div id="gameInfoDiv">
            <#include "gameInfoDiv.ftl">
        </div>
        <#include "footer.ftl">
	</body>
</html>
