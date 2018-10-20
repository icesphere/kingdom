<div class="showCardsDivContent">

    <#if cardsToShow??>

        <div style="padding-bottom: 10px; font-size: 14px; font-weight: bold;">${cardsToShowTitle}</div>

        <div style="clear: both; float: left;">
            <#assign clickType="cardToShow">
            <#list cardsToShow as card>
                <div style="float:left;padding-right:6px;padding-top:6px;">
                    <#include "gameCard.ftl">
                </div>
            </#list>
        </div>

        <div style="clear: both; padding-top: 20px;">
            <button onClick="hideShowCardsDiv()">Close</button>
        </div>

    </#if>

</div>
