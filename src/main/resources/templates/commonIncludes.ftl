<#if mobile>
    <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
</#if>
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<link href="css/main.css" rel="stylesheet" type="text/css">

<script type="text/javascript">
    function switchSite() {
        $.getJSON("switchSite.html", function(data) {
            window.location.reload(true);
        });
    }
</script>