<!DOCTYPE html>
<html>
	<head>
        <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
        <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <META HTTP-EQUIV="Expires" CONTENT="-1">
		<title>Kingdom</title>
        <#include "gameIncludes.ftl">
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
