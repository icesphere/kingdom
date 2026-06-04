<div id="playerAreaDiv">
    <table style="width:100%">
        <tr>
            <td style="width:75%; vertical-align: top;">
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
                        <td style="vertical-align:top; padding-top:30px;">
                            <div class="sidewaysLabel"><span>History</span></div>
                        </td>
                        <td style="vertical-align: top;">
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
