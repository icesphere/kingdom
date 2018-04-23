<div style="<#if player.currentAction??>display:block<#else>display:none</#if>;position:absolute; top: 0; z-index: 1; background-color: rgba(220, 240, 255, 0.9); margin-left: 15px; padding: 15px;">
    <div style="font-size: 20px; padding-bottom: 10px;">
        Action
    </div>
    <div>
        ${player.currentAction.text}
    </div>
</div>
