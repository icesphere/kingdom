<table cellpadding="3" style="text-align:left">
    <tr>
        <td class="label">
            Total games played:
        </td>
        <td>
            ${stats.gamesPlayed}
        </td>
    </tr>
    <tr>
        <td class="label">
            Users that finished at least one game:
        </td>
        <td>
            ${stats.numUsers}
        </td>
    </tr>
    <tr>
        <td class="label">
            New user accounts created:
        </td>
        <td>
            ${stats.newAccountsCreated}
        </td>
    </tr>
    <tr>
        <td class="label">
            New users with game played:
        </td>
        <td>
            ${stats.newUsersWithGamePlayed}
        </td>
    </tr>
    <tr>
        <td class="label">
            Games against humans played:
        </td>
        <td>
            ${stats.gamesAgainstHumansPlayed}
        </td>
    </tr>
    <tr>
        <td class="label">
            Games against computers played:
        </td>
        <td>
            ${stats.gamesAgainstComputersPlayed}
        </td>
    </tr>
    <tr>
        <td class="label">
            Games won by computer:
        </td>
        <td>
            <#if stats.gamesAgainstComputersPlayed != 0>
                ${100 - stats.gamesAgainstComputersWon/stats.gamesAgainstComputersPlayed*100}%
            <#else>
                0
            </#if>
        </td>
    </tr>
    <tr>
        <td class="label">
            Games against hard computer played:
        </td>
        <td>
            ${stats.gamesAgainstHardComputerPlayed}
        </td>
    </tr>
    <tr>
        <td class="label">
            Games won by hard computer:
        </td>
        <td>
            <#if stats.gamesWonByHardComputer != 0>
                ${stats.gamesWonByHardComputer/stats.gamesAgainstHardComputerPlayed*100}%
                <#else>
                    0
            </#if>
        </td>
    </tr>
    <tr>
        <td class="label">
            Games against BMU computer played:
        </td>
        <td>
            ${stats.gamesAgainstBMUComputerPlayed}
        </td>
    </tr>
    <tr>
        <td class="label">
            Games won by BMU computer:
        </td>
        <td>
            <#if stats.gamesWonByBMUComputer != 0>
                ${stats.gamesWonByBMUComputer/stats.gamesAgainstBMUComputerPlayed*100}%
                <#else>
                    0
            </#if>
        </td>
    </tr>
    <tr>
        <td class="label">
            Games quit:
        </td>
        <td>
            ${stats.gamesQuit}
        </td>
    </tr>
    <tr>
        <td class="label">
            Games Abandoned:
        </td>
        <td>
            ${stats.gamesAbandoned}
        </td>
    </tr>
    <tr>
        <td class="label">
            Show victory points games:
        </td>
        <td>
            ${stats.showVictoryPointsGames}
        </td>
    </tr>
    <tr>
        <td class="label">
            Identical Starting Hands games:
        </td>
        <td>
            ${stats.identicalStartingHandsGames}
        </td>
    </tr>
    <tr>
        <td class="label">
            Repeated games:
        </td>
        <td>
            ${stats.repeatedGames}
        </td>
    </tr>
    <tr>
        <td class="label">
            Annotated games:
        </td>
        <td>
            ${stats.annotatedGames}
        </td>
    </tr>
    <tr>
        <td class="label">
            Recent games:
        </td>
        <td>
            ${stats.recentGames}
        </td>
    </tr>
    <tr>
        <td class="label">
            Recommended sets:
        </td>
        <td>
            ${stats.recommendedSets}
        </td>
    </tr>
    <tr>
        <td class="label">
            Mobile games:
        </td>
        <td>
            ${stats.mobileGames}
        </td>
    </tr>
</table>