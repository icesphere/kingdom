<div>

    <div style="float:left;padding-left:5px;padding-right:10px;">
        <a href="javascript:showGameInfo()">Game Info</a>
    </div>
    <div style="float:left;padding-right:10px;">
        <a href="showHelp.html" target="_blank">Help</a>
    </div>
    <div style="float:left;padding-right:10px;">
        <a href="javascript:quitGame();">Quit Game</a>
    </div>
    <#if user.admin>
        <div style="float:left;padding-right:10px;">
            <a href="admin.html">Admin</a>
        </div>
    </#if>
    <div style="float:left;padding-right:10px;">
        <a href="javascript:toggleSound()"><img id="soundImage" src="images/<#if user.soundDefault == 1>soundon.png<#else>soundoff.png</#if>" alt="Toggle Sound" style="border:0"/></a>
    </div>
</div>