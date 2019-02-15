<#if !event.disabled>
    <tr>
        <td>
            <input type="checkbox" name="event_${event.name}" id="event_${event.name}" value="true" onclick="selectEvent()" /> <label for="event_${event.name}" title="${event.fullCardText}">${event.name}</label><#if event.testing> (testing)</#if>
        </td>
    </tr>
</#if>