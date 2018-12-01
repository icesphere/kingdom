<!DOCTYPE html>
<html>
	<head>
        <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
        <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <META HTTP-EQUIV="Expires" CONTENT="-1">
		<title>Kingdom</title>
        <#include "commonIncludes.ftl">
        <#if mobile>
            <link href="css/gameMobile.css" rel="stylesheet" type="text/css">
        <#else>
            <link href="css/game.css" rel="stylesheet" type="text/css">
        </#if>
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
                if(data.redirectToLogin) {
                    document.location = "login.html";
                    return;
                }
                if(data.startGame) {
                    document.location = "showGame.html";
                    return;
                }
                divsToLoad = data.divsToLoad;
                if(divsToLoad == 0){
                    refreshFinished();
                    return;
                }
                if(data.refreshPlayers){
                    $('#lobbyPlayersDiv').load('getLobbyPlayersDiv', function() {
                        divsToLoad--;
                        if(divsToLoad == 0){
                            refreshFinished();
                        }
                    });
                }
                if(data.refreshGameRooms){
                    $('#lobbyGameRoomsDiv').load('getLobbyGameRoomsDiv', function() {
                        divsToLoad--;
                        if(divsToLoad == 0){
                            refreshFinished();
                        }
                    });
                }
                if(data.refreshChat){
                    $('#lobbyChatDiv').load('getLobbyChatDiv', function() {
                        divsToLoad--;
                        if(divsToLoad == 0){
                            refreshFinished();
                        }
                    });
                }
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

            function joinPrivateGame(gameRoomId) {
                var gamePassword = $("#gamePassword_"+gameRoomId).val();
                $.getJSON("joinPrivateGame", {gameId: gameRoomId, gamePassword: gamePassword}, function(data) {
                    if(data.redirectToLogin) {
                        document.location = "login.html";
                        return;
                    }
                    if(data.message == "Success") {
                        if(data.start) {
                            document.location = "showGame.html";
                        }
                    }
                    else {
                        alert(data.message);
                    }
                });
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
                        <div style="clear: both; padding-top: 16px;" class="mediumLabel">My Status: </div>
                        <input type="text" name="status" id="status" style="width:100px;" value="${user.status}"/> <input type="button" id="changeStatusButton" value="Change" onclick="changeStatus()"/>
                    </div>
                </div>
                <div id="lobbyPlayersDiv">
                    <#include "lobbyPlayersDiv.ftl">
                </div>
            </div>
        </div>
        <#include "footer.ftl">
	</body>
</html>
