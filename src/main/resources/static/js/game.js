var currentPlayer = "false";
var gameStatus = 'WaitingForPlayers';
var endTurnRefreshTimeout = 1500;
var selectedCards = new Array();
var selectedCardNumber = 0;
var cardActionNumCards;
var cardActionType;
var cardActionCardsSize;
var refreshingGame = false;
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
        playAllTreasureCards();
    });

    $(document).bind('keyup', 'e', function(){
        endTurn();
    });

    setTimeout(function() { refreshGameInfo(); }, 1000);
});

$(function(){
    $(window).resize(function(){
        resizeSupplyCardsDiv();
    });
});

function refreshGameInfo() {
    $.post("getGameInfo", function(data) {
        let gameData = data.refreshGameData

        gameStatus = gameData.gameStatus;

        currentPlayer = gameData.currentPlayer;

        if(gameStatus == "Finished") {
            $('#gameDiv').load('showGameResults.html', function() {
                refreshFinished();
                return;
            });
        }
    });
}

function connect() {
    $.get("getUserId", function(data) {

        if(data.redirectToLogin) {
            document.location = "login.html";
            return;
        }

        var userId = data.userId
        console.log("connect to game for user id: " + userId)

        let debouncedGameRefresh = debounce(function(data) {
            console.log("run debounced refresh-game")
            refreshGame(JSON.parse(data.body))
        }, 200)

        let debouncedHandAreaRefresh = debounce(function(data) {
            console.log("run debounced refresh-hand")
            refreshHandArea()
        }, 200)

        let debouncedCardsPlayedRefresh = debounce(function(data) {
            console.log("run debounced refresh-cards-played")
            refreshCardsPlayed()
        }, 200)

        let debouncedCardsBoughtRefresh = debounce(function(data) {
            console.log("run debounced refresh-cards-bought")
            refreshCardsBought()
        }, 200)

        let debouncedSupplyRefresh = debounce(function(data) {
            console.log("run debounced refresh-supply")
            refreshSupply()
        }, 200)

        let debouncedCardActionRefresh = debounce(function(data) {
            console.log("run debounced refresh-card-action")
            refreshCardAction()
        }, 200)

        let debouncedChatRefresh = debounce(function(data) {
            console.log("run debounced refresh-chat")
            refreshChat()
        }, 200)

        let debouncedHistoryRefresh = debounce(function(data) {
            console.log("run debounced refresh-history")
            refreshHistory()
        }, 200)

        var socket = new SockJS('/kingdom-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            closeLoadingDialog()
            stompClient.subscribe('/queue/refresh-game/' + userId, debouncedGameRefresh);
            stompClient.subscribe('/queue/refresh-hand-area/' + userId, debouncedHandAreaRefresh);
            stompClient.subscribe('/queue/refresh-cards-played/' + userId, debouncedCardsPlayedRefresh);
            stompClient.subscribe('/queue/refresh-cards-bought/' + userId, debouncedCardsBoughtRefresh);
            stompClient.subscribe('/queue/refresh-supply/' + userId, debouncedSupplyRefresh);
            stompClient.subscribe('/queue/refresh-card-action/' + userId, debouncedCardActionRefresh);
            stompClient.subscribe('/queue/refresh-chat/' + userId, debouncedChatRefresh);
            stompClient.subscribe('/queue/refresh-history/' + userId, debouncedHistoryRefresh);
            stompClient.subscribe('/queue/show-info-message/' + userId, function(data) { showInfoMessage(data.body) });
        });
    });
}

function refreshGame(data){
    if(data.redirectToLogin) {
        document.location = "login.html";
        return;
    }

    if(data.redirectToLobby) {
        document.location = "showGameRooms.html";
        return;
    }

    refreshGameInfo();

    if(gameStatus == "Finished") {
        $('#gameDiv').load('showGameResults.html', function() {
            refreshFinished();
            return;
        });
    }

    $('#gameDiv').load('getGameDiv.html');

    refreshFinished();
}

function showInfoMessage(message) {
    $('#infoMessageText').html(message)
    $('#infoMessageDiv').show()
    setTimeout(function() {
        $('#infoMessageDiv').hide()
    }, 5000)
}

function refreshHandArea() {
    $('#handAreaDiv').load('getHandAreaDiv.html')
}

function refreshCardsPlayed() {
    $('#cardsPlayedDiv').load('getCardsPlayedDiv.html')
}

function refreshCardsBought() {
    $('#cardsBoughtDiv').load('getCardsBoughtDiv.html')
}

function refreshSupply() {
    $('#supplyDiv').load('getSupplyDiv.html')
}

function refreshCardAction() {
    $('#cardActionDiv').load('getCardActionDiv.html')
}

function refreshChat() {
    $('#chatDiv').load('getChatDiv.html')
}

function refreshHistory() {
    $('#historyDiv').load('getHistoryDiv.html')
}

// Credit David Walsh (https://davidwalsh.name/javascript-debounce-function)

// Returns a function, that, as long as it continues to be invoked, will not
// be triggered. The function will be called after it stops being called for
// N milliseconds. If `immediate` is passed, trigger the function on the
// leading edge, instead of the trailing.
function debounce(func, wait, immediate) {
  var timeout;

  return function executedFunction() {
    var context = this;
    var args = arguments;

    var later = function() {
      timeout = null;
      if (!immediate) func.apply(context, args);
    };

    var callNow = immediate && !timeout;

    clearTimeout(timeout);

    timeout = setTimeout(later, wait);

    if (callNow) func.apply(context, args);
  };
};

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

function refreshFinished() {
    refreshingGame = false;
    closeLoadingDialog();
}

function endTurnRefreshFinished() {
    refreshingGame = false;
}

function showLoadingDialog() {
    loadingDialog.dialog("open");
}

function closeLoadingDialog() {
    loadingDialog.dialog("close");
}

function clickCard(clickType, cardName, cardId, special){
    if (!clickingCard && gameStatus == "InProgress" && (clickType == "supply" || clickType == "hand" || clickType == "cardAction")){
        clickingCard = true;
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
            refreshGame(data);
        });
    }
}

function submitCardActionChooseNumberBetween(numberChosen){
    if(!submittingCardAction) {
        submittingCardAction = true;
        $.get("submitCardAction.html", {numberChosen: numberChosen}, function(data) {
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
                    refreshGame(data);
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
            refreshGame(data);
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
            refreshGame(data);
        });
    }
}