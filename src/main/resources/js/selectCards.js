var showOptions = false;
var showDeckFrequency = false;
var showRandomizingOptions = false;
var showExcludedCards = false;

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
    if(decksSelected == 0){
        alert("You need to select at least one deck");
    }
    else if(generateType == "custom" && cardsSelected > 10) {
        alert("You can't select more than 10 cards");
    }
    else{
        document.forms["selectCardOptionsForm"].submit();
    }
}
function saveOptions() {
    if(decksSelected == 1 && $('#deck_proletariat').attr('checked')) {
        alert("Proletariat does not have 10 cards yet, select another deck to go with it");
    }
    else if(generateType == "custom" && cardsSelected > 10) {
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
        $("#annotatedGames").hide();
        $("#cardSelectedRow").show();
        $("#randomizingOptions").hide();
        $("#recommendedSets").hide();
        if(createGame) {
            $("#recentGames").hide();
            $("#decksWeight").show();
        }
    }
    else if(generateType == "annotatedGame") {
        $("#customCards").hide();
        $("#decks").show();
        $("#annotatedGames").show();
        $("#cardSelectedRow").hide();
        $("#randomizingOptions").hide();
        $("#recommendedSets").hide();
        if(createGame) {
            $("#recentGames").hide();
            $("#decksWeight").hide();
        }
    }
    else if(generateType == "recentGame") {
        $("#customCards").hide();
        $("#decks").show();
        $("#annotatedGames").hide();
        $("#cardSelectedRow").hide();
        $("#randomizingOptions").hide();
        $("#recommendedSets").hide();
        if(createGame) {
            $("#recentGames").show();
            $("#decksWeight").hide();
        }
    }
    else if(generateType == "recommendedSet") {
        $("#customCards").hide();
        $("#decks").show();
        $("#annotatedGames").hide();
        $("#cardSelectedRow").hide();
        $("#randomizingOptions").hide();
        $("#recommendedSets").show();
        if(createGame) {
            $("#recentGames").hide();
            $("#decksWeight").hide();
        }
    }
    else{
        $("#customCards").hide();
        $("#decks").show();
        $("#annotatedGames").hide();
        $("#cardSelectedRow").hide();
        $("#randomizingOptions").show();
        $("#recommendedSets").hide();
        if(createGame) {
            $("#recentGames").hide();
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