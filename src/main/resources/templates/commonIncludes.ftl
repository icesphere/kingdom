<#if mobile>
    <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
</#if>

<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />

<script src="/webjars/jquery/jquery.min.js"></script>
<script src="/webjars/jquery-ui/jquery-ui.min.js"></script>
<script src="/webjars/sockjs-client/sockjs.min.js"></script>
<script src="/webjars/stomp-websocket/stomp.min.js"></script>
<script type="text/javascript" src="js/jquery.hotkeys.js"></script>

<link href="css/main.css" rel="stylesheet" type="text/css">

<script type="text/javascript">
    function switchSite() {
        $.get("switchSite.html", function(data) {
            window.location.reload(true);
        });
    }
</script>