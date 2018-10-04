<div id="playerStatsDialog" class="playerStatsDialog gameDialog" style="display:none" title="Player Game Stats">
    <table cellpadding="3">
        <tr>
            <td style="padding-bottom:10px;">
                <table style="text-align:left">
                    <tr>
                        <td class="label" colspan="2">
                            Games against human players
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            total games:
                        </td>
                        <td>
                            ${user.stats.gamesPlayed}
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            games won:
                        </td>
                        <td>
                            ${user.stats.gamesWon}
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            games lost:
                        </td>
                        <td>
                            ${user.stats.gamesLost}
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            games quit:
                        </td>
                        <td>
                            ${user.stats.gamesQuit}
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            avg margin of victory:
                        </td>
                        <td>
                            ${user.stats.averageMarginOfVictory}
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td style="padding-bottom:10px;">
                <table>
                    <tr>
                        <td class="label" colspan="2">
                            Games with computer players
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            total games:
                        </td>
                        <td>
                            ${user.stats.gamesAgainstComputerPlayed}
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            games won:
                        </td>
                        <td>
                            ${user.stats.gamesAgainstComputerWon}
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            games lost:
                        </td>
                        <td>
                            ${user.stats.gamesAgainstComputerLost}
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            games quit:
                        </td>
                        <td>
                            ${user.stats.gamesAgainstComputerQuit}
                        </td>
                    </tr>
                    <tr>
                        <td class="label">
                            avg margin of victory:
                        </td>
                        <td>
                            ${user.stats.averageMarginOfVictoryAgainstComputer}
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td style="text-align:center">
                <input type="button" onclick="closePlayerStatsDialog()" value="Close">
            </td>
        </tr>
    </table>
</div>