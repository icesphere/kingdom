<div id="playerAreaDiv">
    <table style="width:100%">
        <tr>
            <td style="width:75%">
                <table style="width:100%">
                    <tr>
                        <td>
                            <div id="playingAreaDiv">
                                <#include "playingAreaDiv.ftl">
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div id="handAreaDiv">
                                <#include "handAreaDiv.ftl">
                            </div>
                        </td>
                    </tr>
                </table>
            </td>
            <td style="width:25%; vertical-align:top;">
                <table style="width:100%">
                    <tr>
                        <#if !mobile>
                            <td style="vertical-align:top; padding-top:30px;">
                                <img src="images/History.png" alt="History"/>
                            </td>
                        </#if>
                        <td>
                            <div id="historyDiv">
                                <#include "historyDiv.ftl">
                            </div>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</div>