<#if !project.disabled>
    <tr>
        <td>
            <input type="checkbox" name="project_${project.name}" id="project_${project.name}" value="true" onclick="selectProject()" /> <label for="project_${project.name}" title="${project.fullCardText}">${project.name}</label><#if project.testing> (testing)</#if>
        </td>
    </tr>
</#if>