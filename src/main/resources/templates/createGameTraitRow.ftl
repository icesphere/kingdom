<#if !trait.disabled>
    <tr>
        <td>
            <input type="checkbox" name="trait_${trait.name}" id="trait_${trait.name}" value="true" onclick="selectTrait()" /> <label for="trait_${trait.name}" title="${trait.fullCardText}">${trait.name}</label><#if trait.testing> (testing)</#if>
        </td>
    </tr>
</#if>
