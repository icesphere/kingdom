var currentPlayer = "false";
var gameStatus = 'WaitingForPlayers';
var timeout = 1500;
var reloadTimer;
var reloadTimeout = 6000;
var endTurnRefreshTimeout = 1500;
var selectedCards = new Array();
var selectedCardNumber = 0;
var cardActionDialogOpen = false;
var cardActionNumCards;
var cardActionType;
var cardActionCardsSize;
var refreshingGame = false;
var cardActionDialog;
var cardActionSelectExact;
var cardActionSelectUpTo;
var cardActionSelectAtLeast;
var cardActionWidth = 750;
var loadingDialog;
var divsToLoad = 0;
var specialDialog;
var clickingCard = false;
var submittingCardAction = false;
var cardActionOpen = false;

var stompClient = null;

$(document).ready(function() {
    $.ajaxSetup({ cache: false });

    loadingDialog = $("#loadingDialog");
    loadingDialog.dialog({
        autoOpen: false, width:250, modal:true, draggable:false, resizable:false, closeOnEscape: false, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
    });
    showLoadingDialog();

    connect()

    resizeSupplyCardsDiv();

    soundManager.url="sounds/swf/";
    soundManager.useFlashBlock = false;

    soundManager.onload = function() {
      soundManager.createSound('beep','sounds/beep.mp3');
    };

    $(document).bind('keyup', 't', function(){
        if(!cardActionDialogOpen) {
            playAllTreasureCards();
        }
    });

    $(document).bind('keyup', 'e', function(){
        if(!cardActionDialogOpen) {
            endTurn();
        }
    });
});

$(function(){
    $(window).resize(function(){
        resizeSupplyCardsDiv();
    });
});

function connect() {
    $.get("getUserId", function(data) {

        if(data.redirectToLogin) {
            document.location = "login.html";
            return;
        }

        var userId = data.userId
        console.log("connect to game for user id: " + userId)

        var socket = new SockJS('/kingdom-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            closeLoadingDialog()
            stompClient.subscribe('/queue/refresh-game/' + userId,
                function(data) {
                    console.log("got web socket message for refresh-game")
                    console.log(JSON.parse(data.body));
                    refreshParts(data.body)
                }
            );
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function resizeSupplyCardsDiv(){
    var w = $(window).width();
    $("#supplyCardsDiv").css('width', w-350);
    $("#supplyCardsDiv").css('overflow', 'auto');
}

function openSpecialDialog(cardName){
    specialDialog = $("#specialDialog_"+cardName);
    specialDialog.dialog(
        {modal: true, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
    });
}

function closeSpecialDialog(){
    specialDialog.dialog("close");
}

function showIslandCardsDialog(){
    if(mobile) {
        $("#islandCardsDiv").load('loadIslandCardsDialog.html', function() {
            $("#islandCardsDialog").dialog(
                {modal: true, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
            });
        });
    }
    else {
        $("#islandCardsDiv").load('loadIslandCardsDialog.html', function() {
            $("#islandCardsDialog").dialog(
                {modal: true, width: 450, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
            });
        });
    }
}

function closeIslandCardsDialog(){
    $("#islandCardsDialog").dialog("destroy").remove();
}  

function showMuseumCardsDialog(){
    if(mobile) {
        $("#museumCardsDiv").load('loadMuseumCardsDialog.html', function() {
            $("#museumCardsDialog").dialog(
                {modal: true, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
            });
        });
    }
    else {
        $("#museumCardsDiv").load('loadMuseumCardsDialog.html', function() {
            $("#museumCardsDialog").dialog(
                {modal: true, width: 450, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
            });
        });
    }
}

function closeMuseumCardsDialog(){
    $("#museumCardsDialog").dialog("destroy").remove();
}   

function showCityPlannerCardsDialog(){
    if(mobile) {
        $("#cityPlannerCardsDiv").load('loadCityPlannerCardsDialog.html', function() {
            $("#cityPlannerCardsDialog").dialog(
                {modal: true, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
            });
        });
    }
    else {
        $("#cityPlannerCardsDiv").load('loadCityPlannerCardsDialog.html', function() {
            $("#cityPlannerCardsDialog").dialog(
                {modal: true, width: 450, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
            });
        });
    }
}

function closeCityPlannerCardsDialog(){
    $("#cityPlannerCardsDialog").dialog("destroy").remove();
}

function showNativeVillageDialog(){
    if(mobile) {
        $("#nativeVillageDiv").load('loadNativeVillageDialog.html', function() {
            $("#nativeVillageDialog").dialog(
                {modal: true, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
            });
        });
    }
    else {
        $("#nativeVillageDiv").load('loadNativeVillageDialog.html', function() {
            $("#nativeVillageDialog").dialog(
                {modal: true, width: 450, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
            });
        });
    }
}

function closeNativeVillageDialog(){
    $("#nativeVillageDialog").dialog("destroy").remove();
}

function reloadPage() {
    document.location = "showGame.html";
}

function refreshGame(){
    if(!refreshingGame){
        refreshingGame = true;
        $.getJSON("refreshGame", function(data) {
            refreshParts(data);
        });
    }
    refreshTimer = setTimeout ( "refreshGame()", timeout );
}

function refreshFinished() {
    refreshingGame = false;
    closeLoadingDialog();
}

function endTurnRefreshFinished() {
    refreshingGame = false;
    refreshGame();
}

function refreshParts(data){
    if(data.redirectToLogin) {
        document.location = "login.html";
        return;
    }
    if(data.redirectToLobby) {
        document.location = "showGameRooms.html";
        return;
    }

    if(data.refresh.isRefreshEndTurn){
        if(data.refresh.isRefreshHandOnEndTurn){
            $('#handAreaDiv').load('getHandAreaDivOnEndTurn.html');
        }
        if(data.refresh.isRefreshSupplyOnEndTurn){
            $('#supplyDiv').load('getSupplyDivOnEndTurn.html');
        }
        if(data.refresh.isRefreshPlayersOnEndTurn){
            $('#playersDiv').load('getPlayersDiv.html');
        }
        $('#playingAreaDiv').load('getPreviousPlayerPlayingAreaDiv.html', function() {
            closeLoadingDialog();
            setTimeout("endTurnRefreshFinished()", endTurnRefreshTimeout);
            return;
        });
    }

    if(data.refresh.isPlayBeep) {
        playBeep();
    }
    if(data.refresh.isRefreshTitle){
        document.title = data.title;
    }
    if(data.refresh.isRefreshGameStatus){
        gameStatus = data.gameStatus;
        currentPlayer = data.currentPlayer;
        if(gameStatus == "Finished") {
            $('#gameDiv').load('showGameResults.html', function() {
                refreshFinished();
                return;
            });
        }
    }
    if(data.refresh.isCloseCardActionDialog){
        closeCardActionDialog();
    }
    divsToLoad = data.divsToLoad;
    if(divsToLoad == 0){
        refreshFinished();
        return;
    }
    if(data.refresh.isRefreshPlayers){
        $('#playersDiv').load('getPlayersDiv.html', function() {
            divsToLoad--;
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshSupply){
        $('#supplyDiv').load('getSupplyDiv.html', function() {
            divsToLoad--;
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshPlayingArea){
        $('#playingAreaDiv').load('getPlayingAreaDiv.html', function() {
            divsToLoad--;
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshCardsPlayed){
        $('#cardsPlayedDiv').load('getCardsPlayedDiv.html', function() {
            divsToLoad--;
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshCardsBought){
        $('#cardsBoughtDiv').load('getCardsBoughtDiv.html', function() {
            divsToLoad--;
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshHistory){
        $('#historyDiv').load('getHistoryDiv.html', function() {
            divsToLoad--;
            if(!mobile) {
                $("#historyScrollingDiv").scrollTop($("#historyScrollingDiv")[0].scrollHeight);
            }
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshHandArea){
        $('#handAreaDiv').load('getHandAreaDiv.html', function() {
            divsToLoad--;
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshHand){
        $('#handDiv').load('getHandDiv.html', function() {
            divsToLoad--;
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshDiscard){
        $('#discardDiv').load('getDiscardDiv.html', function() {
            divsToLoad--;
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshChat){
        $('#chatDiv').load('getChatDiv.html', function() {
            divsToLoad--;
            if(!mobile) {
                $("#chatDiv").scrollTop($("#chatDiv")[0].scrollHeight);
            }
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshInfoDialog){
        $('#infoDialogDiv').load('getInfoDialogDiv.html', function() {
            openInfoDialog(data.infoDialog.infoDialogHideMethod, data.infoDialog.infoDialogWidth, data.infoDialog.infoDialogHeight, data.infoDialog.infoDialogTimeout);
            divsToLoad--;
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
    if(data.refresh.isRefreshCardAction){
        $('#cardActionDiv').load('getCardActionDiv.html', function() {
            divsToLoad--;
            if(divsToLoad == 0){
                refreshFinished();
            }
        });
    }
}

function showLoadingDialog() {
    loadingDialog.dialog("open");
}

function closeLoadingDialog() {
    loadingDialog.dialog("close");
    clearTimeout(reloadTimer);
}

function clickCard(clickType, cardName, cardId, special){
    debugger;
    if (!clickingCard && currentPlayer && gameStatus == "InProgress" && (clickType == "supply" || clickType == "hand")){
        clickingCard = true;
        showLoadingDialog();
        $.post("clickCard", {clickType: clickType, cardName: cardName, cardId: cardId}, function(data) {
            clickingCard = false;
        });
    }
}

function endTurn(){
    if(gameStatus == "InProgress" && currentPlayer){
        $.post("endTurn");
    }
}

function playAllTreasureCards(){
    if(gameStatus == "InProgress" && currentPlayer){
        refreshingGame = true;
        showLoadingDialog();
        $.post("playAllTreasureCards");
    }
}

function selectCard(cardIndex, cardName){
    selectedCards[cardIndex] = cardName;
}

function selectCardInOrder(cardName){
    selectedCards[selectedCardNumber] = cardName;
    selectedCardNumber++;
}

function showCardActionDialog(){
    cardActionDialogOpen = true;
    $(".cardAction").each(function(idx) {
        if($(this).attr("hideOnSelect") == "true"){
            selectedCardNumber = 0;
            $(this).click(function() {
                var cardName = $(this).attr("cardName");
                $(this).hide();
                selectCardInOrder(cardName);
            });
        }
        else if($(this).attr("disableSelect") == "false"){
            if($(this).attr("autoSelect") == "true"){
                $(this).removeClass("cardAction").addClass("cardActionSelected");
                selectCard($(this).attr("cardIndex"), $(this).attr("cardName"));
            }
            $(this).click(function() {
                var cardIndex = $(this).attr("cardIndex");
                var cardName = $(this).attr("cardName");
                var fromClass = "cardAction";
                var toClass = "cardActionSelected";
                if(selectedCards[cardIndex]){
                    fromClass = "cardActionSelected";
                    toClass = "cardAction";
                    cardName = null;
                }
                $(this).removeClass(fromClass).addClass(toClass);
                selectCard(cardIndex, cardName);
            });
        }
    });

    cardActionDialog = $("#cardActionDialog_");
    if(mobile) {
        cardActionDialog.dialog({
            modal: true, closeOnEscape: false, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
        });
    }
    else {
        cardActionDialog.dialog({
            modal: true, width: cardActionWidth, closeOnEscape: false, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
        });
    }
}

function closeCardActionDialog(){
    cardActionDialogOpen = false;
    if(cardActionDialog) {
        cardActionDialog.dialog("close");
        cardActionDialogOpen = false;
        focusChat();
    }
}

function showGameInfo() {
    $('#gameInfoDiv').load('getGameInfoDiv.html', function() {
        showGameInfoDialog();
    });
}

function showGameInfoDialog() {
    if(mobile) {
        $("#gameInfoDialog").dialog({
            modal: false, closeOnEscape: false, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
        });
    }
    else {
        $("#gameInfoDialog").dialog({
            modal: false, width: 600, closeOnEscape: false, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
        });
    }
}

function closeGameInfoDialog() {
    $("#gameInfoDialog").dialog("destroy").remove();
}

function openInfoDialog(infoDialogHideMethod, infoDialogWidth, infoDialogHeight, infoDialogTimeout){

    if(mobile && infoDialogWidth > 200) {
        $("#infoDialog").dialog({
            modal: false, draggable:false, resizable:false, hide: infoDialogHideMethod, closeOnEscape: false, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
        });
    }
    else {
        $("#infoDialog").dialog({
            modal: false, width: infoDialogWidth, height: infoDialogHeight, draggable:false, resizable:false, hide: infoDialogHideMethod, closeOnEscape: false, open: function(event, ui) { $(".ui-dialog-titlebar-close").hide();}
        });
    }

    setTimeout ( "closeInfoDialog()", infoDialogTimeout);
}

function closeInfoDialog(){
    $("#infoDialog").dialog("destroy").remove();
    focusChat();
}

function focusChat(){
    var message = $("#chatMessage").val();
    if(message != ""){
        $("#chatMessage").focus();
    }
}

function submitCardAction(){
    if(!submittingCardAction) {
        submittingCardAction = true;
        if(cardActionType == 13){
            $.get("submitCardAction.html", function(data) {
                closeCardActionDialog();
            });
        }
        var canSubmit = true;
        var numSelected = 0;
        for(var i=0; i<cardActionCardsSize; i++){
            if(selectedCards[i]){
                numSelected++;
            }
        }
        var cardString = "cards";
        if(cardActionNumCards == 1){
            cardString = "card";
        }
        if(cardActionSelectExact){
            if(numSelected != cardActionNumCards && cardActionNumCards >= 0){
                canSubmit = false;
                alert("You must select "+cardActionNumCards+" "+cardString);
            }
        }
        else if(cardActionType == 3){
            if(cardActionCardsSize - numSelected != cardActionNumCards){
                canSubmit = false;
                alert("You must discard down to exactly "+cardActionNumCards+" "+cardString);
            }
        }
        else if(cardActionSelectUpTo){
            if(numSelected > cardActionNumCards){
                canSubmit = false;
                alert("You can only select up to "+cardActionNumCards+" "+cardString);
            }
        }
        else if(cardActionSelectAtLeast){
            if(numSelected < cardActionNumCards){
                canSubmit = false;
                alert("You must select at least "+cardActionNumCards+" "+cardString);
            }
        }
        if(canSubmit){
            var selectedCardsString = selectedCards.join();
            $.get("submitCardAction.html", {selectedCards: selectedCardsString}, function(data) {
                closeCardActionDialog();
                submittingCardAction = false;
                showLoadingDialog();
            });
        }
        else {
            submittingCardAction = false;
        }
    }
}

function submitCardActionYesNo(answer){
    if(!submittingCardAction) {
        submittingCardAction = true;
        $.get("submitCardAction.html", {answer: answer}, function(data) {
            closeCardActionDialog();
            submittingCardAction = false;
            showLoadingDialog();
        });
    }
}

function submitCardActionChoice(choice){
    if(!submittingCardAction) {
        submittingCardAction = true;
        $.get("submitCardActionChoice", {choice: choice}, function(data) {
            submittingCardAction = false;
        });
    }
}

function submitDoNotUseAction() {
    if(!submittingCardAction) {
        submittingCardAction = true;
        $.get("submitDoNotUseAction", function(data) {
            submittingCardAction = false;
        });
    }
}

function submitDoneWithAction() {
    if(!submittingCardAction) {
        submittingCardAction = true;
        $.get("submitDoneWithAction", function(data) {
            submittingCardAction = false;
            refreshParts(data);
        });
    }
}

function submitCardActionChooseNumberBetween(numberChosen){
    if(!submittingCardAction) {
        submittingCardAction = true;
        $.get("submitCardAction.html", {numberChosen: numberChosen}, function(data) {
            closeCardActionDialog();
            submittingCardAction = false;
            showLoadingDialog();
        });
    }
}

function exitGame(){
    $.get("exitGame.html", function(data) {
        document.location = "showGameRooms.html";
    });
}

function quitGame(){
    if(gameStatus != 'Finished'){
        if(confirm("Are you sure you want to quit this game?")){
            refreshingGame = true;
            $.get("quitGame", function(data) {
                if(gameStatus == "WaitingForPlayers"){
                    document.location = "showGameRooms.html";
                }
                else {
                    refreshParts(data);
                }
            });
        }
    }
}

function sendChat(){
    var message = $("#chatMessage").val();
    if(message != ""){
        $("#chatMessage").val("");
        refreshingGame = true;
        $.get("sendChat", {message: message}, function(data) {
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

function playBeep(){
    if(playSound) {
        try{
            soundManager.play("beep")
        }
        catch(err){
        }
    }
}

function toggleSound() {
    if(playSound) {
        $("#soundImage").attr("src", "images/soundoff.png");
    }
    else {
        $("#soundImage").attr("src", "images/soundon.png");
    }
    playSound = !playSound;

    $.get("toggleSound.html");
}

function useCoinTokens(){
    if(gameStatus == "InProgress"){
        refreshingGame = true;
        showLoadingDialog();
        $.post("useCoinTokens", function(data) {
            refreshParts(data);
        });
    }
}