<#if !way.disabled>
    <tr>
        <td>
            <input type="checkbox" name="way_${way.name}" id="way_${way.name}" value="true" onclick="selectWay()" /> <label for="way_${way.name}" title="${way.fullCardText}">${way.name}</label><#if way.testing> (testing)</#if>
        </td>
    </tr>
</#if>