var showOptions = false;
var showDeckFrequency = false;
var showRandomizingOptions = false;
var showExcludedCards = false;

$(document).ready(function() {
    loadSelectedValuesFromLocalStorage();
});

function loadSelectedValuesFromLocalStorage() {
    var decks = $("input[name^='deck_']");
    $.makeArray(decks).forEach(d => {
        if (localStorage.getItem(d.name) == "false") {
            d.checked = false;
        }
    });

    var deckWeights = $("select[name^='deck_weight_']");
    $.makeArray(deckWeights).forEach(dw => {
        let deckWeight = localStorage.getItem(dw.name);
        if (deckWeight && deckWeight != "3") {
            dw.value = deckWeight;
        }
    });

    var playerChoices = $("select[name^='player']");
    $.makeArray(playerChoices).forEach(pc => {
        let playerChoice = localStorage.getItem(pc.name);
        if (playerChoice) {
            pc.value = playerChoice;
        }
    });

    $("#playTreasureCards").prop('checked', localStorage.getItem("playTreasureCards") == "true");
    $("#showVictoryPoints").prop('checked', localStorage.getItem("showVictoryPoints") == "true");
    $("#identicalStartingHands").prop('checked', localStorage.getItem("identicalStartingHands") == "true");
}

function createGame() {
    var decksSelected = $("input[name^='deck_']:checked").length;
    var generateType = $('input[name=generateType]:checked').val();
    var cardsSelected = $("input[name^='card_']:checked:visible").length;
    var numHumanPlayers = 0;
    $('[value*="human"]').each(function () {
        if($(this).attr('selected')) {
            numHumanPlayers++;
        }
    });
    if(decksSelected == 0) {
        alert("You need to select at least one deck");
    }
    else if(generateType == "custom" && cardsSelected > 10) {
        alert("You can't select more than 10 cards");
    }
    else {
        saveSelectedValuesToLocalStorage();

        document.forms["selectCardOptionsForm"].submit();
    }
}

function saveSelectedValuesToLocalStorage() {
    var decks = $("input[name^='deck_']");
    $.makeArray(decks).forEach(d => localStorage.setItem(d.name, d.checked));

    var deckWeights = $("select[name^='deck_weight_']");
    $.makeArray(deckWeights).forEach(dw => localStorage.setItem(dw.name, dw.value));

    var playerChoices = $("select[name^='player']");
    $.makeArray(playerChoices).forEach(pc => localStorage.setItem(pc.name, pc.value));

    localStorage.setItem("playTreasureCards", $("#playTreasureCards").prop('checked'));
    localStorage.setItem("showVictoryPoints", $("#showVictoryPoints").prop('checked'));
    localStorage.setItem("identicalStartingHands", $("#identicalStartingHands").prop('checked'));
}

function saveOptions() {
    if(generateType == "custom" && cardsSelected > 10) {
        alert("You can't select more than 10 cards");
    }
    else{
        document.forms["selectCardOptionsForm"].submit();
    }
}
function selectDeck(checkbox) {
    var deckName = checkbox.value;
    var cardsDiv = $('#'+deckName+'CardsDiv');
    if(checkbox.checked){
        cardsDiv.show();
    } else {
        cardsDiv.hide();
    }
}

function selectCard() {
    var cardsSelected = $("input[name^='card_']:checked:visible").length;
    $("#numCardsSelected").html(cardsSelected);
}

function toggleGenerateType(){
    var generateType = $('input[name=generateType]:checked').val();
    if(generateType == "custom"){
        $("#customCards").show();
        $("#decks").show();
        $("#cardSelectedRow").show();
        $("#randomizingOptions").hide();
        if(createGame) {
            $("#decksWeight").show();
        }
    }
    else{
        $("#customCards").hide();
        $("#decks").show();
        $("#cardSelectedRow").hide();
        $("#randomizingOptions").show();
        if(createGame) {
            $("#decksWeight").show();
        }
    }
}

function togglePrivateGame(checkbox) {
    if(checkbox.checked){
        $("#gamePasswordDiv").show();
    }
    else{
        $("#gamePasswordDiv").hide();
    }
}
function toggleOptions() {
    if(showOptions) {
        $("#options").hide();
        $("#optionsLink").text("Show");
        showOptions = false;
    }
    else {
        $("#options").show();
        $("#optionsLink").text("Hide");
        showOptions = true;
    }
}
function toggleDeckFrequency() {
    if(showDeckFrequency) {
        $("#deckFrequency").hide();
        $("#deckFrequencyLink").text("Show");
        showDeckFrequency = false;
    }
    else {
        $("#deckFrequency").show();
        $("#deckFrequencyLink").text("Hide");
        showDeckFrequency = true;
    }
}
function toggleRandomizingOptions() {
    if(showRandomizingOptions) {
        $("#randomizingOptionsContent").hide();
        $("#randomizingOptionsLink").text("Show");
        showRandomizingOptions = false;
    }
    else {
        $("#randomizingOptionsContent").show();
        $("#randomizingOptionsLink").text("Hide");
        showRandomizingOptions = true;
    }
}
function toggleExcludedCards() {
    if(showExcludedCards) {
        $("#excludedCardsContent").hide();
        $("#excludedCardsLink").text("Show");
        showExcludedCards = false;
    }
    else {
        $("#excludedCardsContent").show();
        $("#excludedCardsLink").text("Hide");
        showExcludedCards = true;
    }
}