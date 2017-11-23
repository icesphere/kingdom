<!DOCTYPE html>
<html>
	<head>
        <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
        <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <META HTTP-EQUIV="Expires" CONTENT="-1">
		<title>Kingdom</title>
        <#include "commonIncludes.ftl">
        <script type="text/javascript" src="js/jquery-ui-1.8.custom.min.js"></script>
        <script type="text/javascript" src="js/toggle-select.js"></script>
        <#if mobile>
            <link href="css/gameMobile.css" rel="stylesheet" type="text/css">
        <#else>
            <link href="css/game.css" rel="stylesheet" type="text/css">
        </#if>
        <link href="css/jquery-ui-1.8.custom.css" rel="stylesheet" type="text/css">
        <script type="text/javascript">
            var timeout = 3000;
            var refreshingLobby = false;

            $(document).ready(function() {
                $.ajaxSetup({ cache: false });

                setTimeout ( "refreshLobby()", timeout );
            });

            function refreshLobby()
            {
                if(!refreshingLobby){
                    refreshingLobby = true;
                    $.getJSON("refreshLobby", function(data) {
                        refreshParts(data);
                    });
                }
                setTimeout ( "refreshLobby()", timeout );
            }

            function refreshFinished() {
                refreshingLobby = false;
            }

            function refreshParts(data){
                if(data.redirectToLogin == "true") {
                    document.location = "login.html";
                    return;
                }
                if(data.startGame == "true") {
                    document.location = "showGame.html";
                    return;
                }
                divsToLoad = data.divsToLoad;
                if(divsToLoad == 0){
                    refreshFinished();
                    return;
                }
                if(data.refreshPlayers == "true"){
                    $('#lobbyPlayersDiv').load('getLobbyPlayersDiv.html', function() {
                        divsToLoad--;
                        if(divsToLoad == 0){
                            refreshFinished();
                        }
                    });
                }
                if(data.refreshGameRooms == "true"){
                    $('#lobbyGameRoomsDiv').load('getLobbyGameRoomsDiv.html', function() {
                        divsToLoad--;
                        if(divsToLoad == 0){
                            refreshFinished();
                        }
                    });
                }
                if(data.refreshChat == "true"){
                    $('#lobbyChatDiv').load('getLobbyChatDiv.html', function() {
                        divsToLoad--;
                        if(divsToLoad == 0){
                            refreshFinished();
                        }
                    });
                }
            }

            function openCardsDialog(gameId){
                var cardsDialog = $("#cardsDialog_"+gameId);
                cardsDialog.dialog(
                    {modal: true, <#if !mobile>width:550, </#if>open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
                });
            }

            function closeCardsDialog(gameId){
                var cardsDialog = $("#cardsDialog_"+gameId);
                cardsDialog.dialog("close");
            }

            function sendChat(){
                var message = $("#chatMessage").val();
                if(message != ""){
                    $("#chatMessage").val("");
                    $.get("sendLobbyChat", {message: message}, function(data) {
                        refreshParts(data);
                    });
                }
            }

            function sendPrivateChat(userId){
                var message = prompt("Enter your private message");
                if(message != null && message != ""){
                    $("#chatMessage").val("");
                    $.get("sendPrivateChat", {message: message, receivingUserId: userId}, function(data) {
                        refreshParts(data);
                    });
                }
            }

            function checkEnterOnAddChat(e)
            {
                if(e.keyCode == 13 || e.which == 13)
                {
                    document.getElementById("sendChatButton").click();
                }
                return true;
            }

            function changeStatus() {
                var status = $("#status").val();
                $.get("changeStatus", {status: status}, function(data) {
                    refreshParts(data);
                });
            }

            function checkEnterOnChangeStatus(e)
            {
                if(e.keyCode == 13 || e.which == 13)
                {
                    document.getElementById("changeStatusButton").click();
                }
                return true;
            }

            var specialDialog;
            function openSpecialDialog(cardId) {
                specialDialog = $("#specialDialog_"+cardId);
                specialDialog.dialog(
                    {modal: true, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
                });
            }

            function closeSpecialDialog() {
                specialDialog.dialog("close");
            }

            function joinPrivateGame(gameRoomId) {
                var gamePassword = $("#gamePassword_"+gameRoomId).val();
                $.getJSON("joinPrivateGame", {gameId: gameRoomId, gamePassword: gamePassword}, function(data) {
                    if(data.redirectToLogin == "true") {
                        document.location = "login.html";
                        return;
                    }
                    if(data.message == "Success") {
                        if(data.start == "true") {
                            document.location = "showGame.html";
                        }
                    }
                    else {
                        alert(data.message);
                    }
                });
            }

            function showPlayerStats() {
                $('#playerStatsDiv').load('getPlayerStatsDiv.html', function() {
                    showPlayerStatsDialog();
                });
            }

            function showPlayerStatsDialog() {
                $("#playerStatsDialog").dialog({
                    modal: false, width: 300, closeOnEscape: false, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
                });
            }

            function closePlayerStatsDialog() {
                $("#playerStatsDialog").dialog("destroy").remove();
            }

        </script>
	</head>
	<body>
        <div class="topGradient"></div>
        <div style="width:100%; text-align:right;">
            <#if user.admin>
                <div style="position:relative; right:5px;">
                    <a href="admin.html">Admin</a>
                </div>
            </#if>
            <div style="position:relative; right:5px;">
                <a href="myAccount.html">My Account</a>
            </div>
            <div style="position:relative; right:5px;">
                <a href="javascript:showPlayerStats()">My Game Stats</a>
            </div>
            <div style="position:relative; right:5px;">
                <a href="logout.html">Logout</a>
            </div>
        </div>
        <div class="lobby">
            <div class="lobbyGameRooms">
                <h3>Game Rooms</h3>
                <#if user.guest>
                    <div>
                        As a guest you can join games, but not create them
                    </div>
                </#if>
                <div id="lobbyGameRoomsDiv">
                    <#include "lobbyGameRoomsDiv.ftl">
                </div>
            </div>
            <div class="lobbyChatOuter">
                <div class="lobbyChatInner">
                    <div id="lobbyChatDiv">
                        <#if mobile>
                            <#include "lobbyChatDivMobile.ftl">
                        <#else>
                            <#include "lobbyChatDiv.ftl">
                        </#if>
                    </div>
                </div>
            </div>
            <div class="lobbyPlayers">
                <div>
                    <div onkeypress="return checkEnterOnChangeStatus(event)">
                        <span class="mediumLabel">My Status: </span>
                        <input type="text" name="status" id="status" style="width:100px;" value="${user.status}"/> <input type="button" id="changeStatusButton" value="Change" onclick="changeStatus()"/>
                    </div>
                </div>
                <div id="lobbyPlayersDiv">
                    <#include "lobbyPlayersDiv.ftl">
                </div>
            </div>
        </div>
        <div id="playerStatsDiv">
        </div>
        <#include "footer.ftl">
	</body>
</html>
