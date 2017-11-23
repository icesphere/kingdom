<table style="width:100%">
    <tr>
        <td>
            <div id="playersDiv">
                <#include "playersDiv.ftl">
            </div>
        </td>
        <td>
            <div style="text-align:right;">
                <a href="javascript:toggleSound()"><img id="soundImage" src="images/<#if user.soundDefault == 1>soundon.png<#else>soundoff.png</#if>" alt="Toggle Sound" style="border:0"/></a>
                &#160;&#160;&#160;<a href="showHelp.html" target="_blank">Help</a>
                &#160;&#160;&#160;<a href="javascript:quitGame();">Quit Game</a>
                <#if user.admin>
                    &#160;&#160;&#160;<a href="admin.html">Admin</a>
                </#if>
            </div>
        </td>
    </tr>
</table>