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
var divsToLoad = 0;
var clickingCard = false;
var submittingCardAction = false;
var cardActionOpen = false;

var stompClient = null;

var infoMessageSection = 1

$(document).ready(function() {
    $.ajaxSetup({ cache: false });

    connect()

    resizeSupplyCardsDiv();

    $(document).bind('keyup', 't', function(){
        playAllTreasureCards();
    });

    $(document).bind('keyup', 'e', function(){
        endTurn();
    });

    if (localStorage.muteSound == "true") {
        $("#soundImage").attr("src", "images/soundoff.png");
    }

    setTimeout(function() { refreshGameInfo(); }, 2000);
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
            if (!document.location.pathname.includes("showGameResults")) {
                document.location = "showGameResults.html";
            }
        } else if (currentPlayer) {
            if (!(localStorage.muteSound == "true")) {
                playBeep()
            }
            showInfoMessage("Your turn", 1200)
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
            refreshGame()
        }, 200)

        let debouncedHandAreaRefresh = debounce(function(data) {
            refreshHandArea()
        }, 200)

        let debouncedCardsPlayedRefresh = debounce(function(data) {
            refreshCardsPlayed()
        }, 200)

        let debouncedCardsBoughtRefresh = debounce(function(data) {
            refreshCardsBought()
        }, 200)

        let debouncedSupplyRefresh = debounce(function(data) {
            refreshSupply()
        }, 200)

        let debouncedCardActionRefresh = debounce(function(data) {
            refreshCardAction()
        }, 200)

        let debouncedPlayersRefresh = debounce(function(data) {
            refreshPlayers()
        }, 200)

        let debouncedChatRefresh = debounce(function(data) {
            refreshChat()
        }, 200)

        let debouncedHistoryRefresh = debounce(function(data) {
            refreshHistory()
        }, 500)

        var socket = new SockJS('/kingdom-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/queue/refresh-game/' + userId, debouncedGameRefresh);
            stompClient.subscribe('/queue/refresh-hand-area/' + userId, debouncedHandAreaRefresh);
            stompClient.subscribe('/queue/refresh-cards-played/' + userId, debouncedCardsPlayedRefresh);
            stompClient.subscribe('/queue/refresh-cards-bought/' + userId, debouncedCardsBoughtRefresh);
            stompClient.subscribe('/queue/refresh-previous-player-cards-bought/' + userId, refreshPreviousPlayerCardsBought);
            stompClient.subscribe('/queue/refresh-supply/' + userId, debouncedSupplyRefresh);
            stompClient.subscribe('/queue/refresh-card-action/' + userId, debouncedCardActionRefresh);
            stompClient.subscribe('/queue/refresh-players/' + userId, debouncedPlayersRefresh);
            stompClient.subscribe('/queue/refresh-chat/' + userId, debouncedChatRefresh);
            stompClient.subscribe('/queue/refresh-history/' + userId, debouncedHistoryRefresh);
            stompClient.subscribe('/queue/show-info-message/' + userId, function(data) { showInfoMessage(data.body, 2500) });
        });
    });
}

function refreshGame() {

    console.log("refreshing game")

    refreshGameInfo();

    refreshHandArea();

    refreshCardsPlayed();

    refreshCardsBought();

    refreshSupply();

    refreshCardAction();

    refreshPlayers();
}

function showInfoMessage(message, length) {
    if (infoMessageSection == 1) {
        infoMessageSection = 6
    } else {
        infoMessageSection = infoMessageSection - 1
    }
    
    let sectionNum = infoMessageSection

    $('#infoMessageText' + sectionNum).html(message)
    $('#infoMessageDiv' + sectionNum).show()
    setTimeout(function() {
        $('#infoMessageDiv' + sectionNum).fadeOut(1000)
    }, length)
}

function refreshPreviousPlayerCardsBought() {
    console.log("refreshing previous player cards bought")
    $('#cardsBoughtDiv').load('getPreviousPlayerCardsBoughtDiv.html')
}

function refreshHandArea() {
    console.log("refreshing hand area")
    $('#handAreaDiv').load('getHandAreaDiv.html')
}

function refreshCardsPlayed() {
    console.log("refreshing cards played")
    $('#cardsPlayedDiv').load('getCardsPlayedDiv.html')
}

function refreshCardsBought() {
    console.log("refreshing cards bought")
    $('#cardsBoughtDiv').load('getCardsBoughtDiv.html')
}

function refreshSupply() {
    console.log("refreshing supply")
    $('#supplyDiv').load('getSupplyDiv.html')
}

function refreshCardAction() {
    console.log("refreshing card action")
    $('#cardActionDiv').load('getCardActionDiv.html')
}

function refreshPlayers() {
    console.log("refreshing players")
    $('#playersDiv').load('getPlayersDiv.html')
}

function refreshChat() {
    console.log("refreshing chat")
    $('#chatDiv').load('getChatDiv.html', function() {
      var chatDiv = document.getElementById("chatDiv");
      chatDiv.scrollTop = chatDiv.scrollHeight;
    });
}

function refreshHistory() {
    console.log("refreshing history")
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

function showNativeVillageCards(){

    $('#showCardsDiv').load('showNativeVillageCards.html')

    $('#showCardsDiv').show()
}

function showIslandCards() {

    $('#showCardsDiv').load('showIslandCards.html')

    $('#showCardsDiv').show()
}

function showExileCards() {

    $('#showCardsDiv').load('showExileCards.html')

    $('#showCardsDiv').show()
}

function showCardsNotInSupply() {

    $('#showCardsDiv').load('showCardsNotInSupply.html')

    $('#showCardsDiv').show()
}

function hideShowCardsDiv() {
    $('#showCardsDiv').hide()
}

function showGameInfo() {
    $('#gameInfoDiv').load('getGameInfoDiv.html')

    $('#gameInfoDiv').show()
}

function hideGameInfoDiv() {
    $('#gameInfoDiv').hide()
}

function reloadPage() {
    document.location = "showGame.html";
}

function refreshFinished() {
    refreshingGame = false;
}

function endTurnRefreshFinished() {
    refreshingGame = false;
}

function clickCard(clickType, cardName, cardId, special){
    if (!clickingCard && gameStatus == "InProgress" && (clickType == "supply" || clickType == "hand" || clickType == "cardAction" || clickType == "event" || clickType == "landmark" || clickType == "project")){
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
            });
        }
        else {
            submittingCardAction = false;
        }
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
        });
    }
}

function submitCardActionChooseNumberBetween(numberChosen){
    if(!submittingCardAction) {
        submittingCardAction = true;
        $.get("submitCardAction.html", {numberChosen: numberChosen}, function(data) {
            submittingCardAction = false;
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

function playBeep() {
    try{
        document.getElementById("beepAudio").play()
    }
    catch(err){
    }
}

function toggleSound() {
    if (localStorage.muteSound == "true") {
        $("#soundImage").attr("src", "images/soundon.png");
        localStorage.muteSound = "false"
    }
    else {
        $("#soundImage").attr("src", "images/soundoff.png");
        localStorage.muteSound = "true"
    }
}

function useCoffers(){
    if(gameStatus == "InProgress"){
        $.post("useCoffers", function(data) {
        });
    }
}

function useVillagers(){
    if(gameStatus == "InProgress"){
        $.post("useVillagers", function(data) {
        });
    }
}

function payOffDebt(){
    if(gameStatus == "InProgress"){
        $.post("payOffDebt", function(data) {
        });
    }
}

function showTavernCards() {
    if(gameStatus == "InProgress"){
        $.post("showTavernCards", function(data) {
        });
    }
}
