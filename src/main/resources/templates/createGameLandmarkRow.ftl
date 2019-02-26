<#if !landmark.disabled>
    <tr>
        <td>
            <input type="checkbox" name="landmark_${landmark.name}" id="landmark_${landmark.name}" value="true" onclick="selectLandmark()" /> <label for="landmark_${landmark.name}" title="${landmark.fullCardText}">${landmark.name}</label><#if landmark.testing> (testing)</#if>
        </td>
    </tr>
</#if>