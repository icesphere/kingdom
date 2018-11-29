<!DOCTYPE html>
<html>
	<head>
        <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
        <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <META HTTP-EQUIV="Expires" CONTENT="-1">
		<title>Kingdom</title>
        <#include "commonIncludes.ftl">
        <#if mobile>
            <link href="css/game.css?3" rel="stylesheet" type="text/css">
            <link href="css/gameMobile.css" rel="stylesheet" type="text/css">
            <script type="text/javascript">
                var mobile = true;
            </script>
        <#else>
            <link href="css/game.css?2" rel="stylesheet" type="text/css">
            <script type="text/javascript">
                var mobile = false;
            </script>
        </#if>
        <script type="text/javascript" src="js/game.js?6"></script>
	</head>
	<body>

        <audio id="beepAudio">
          <source src="sounds/beep.mp3" type="audio/mpeg">
          Your browser does not support the audio element.
        </audio>

        <div id="gameDiv">
            <#if gameStatus == "Finished">
                <#include "gameResults.ftl">
            <#else>
                <#include "gameDiv.ftl">
            </#if>
        </div>

        <#include "footerNoGradient.ftl">

	</body>
</html>
