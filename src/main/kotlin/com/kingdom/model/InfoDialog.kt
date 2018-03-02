package com.kingdom.model

import java.util.ArrayList

class InfoDialog {
    var message: String? = null
    var cards: List<Card> = ArrayList()
    var hideMethod: String? = null
    var width: Int = 0
        get() = if (field == 0) {
            300
        } else field
    var timeout: Int = 0
    var messageFontSize = 12
    var height = "auto"
    var messageAlign = "left"
    var isError: Boolean = false

    companion object {

        val yourTurnInfoDialog: InfoDialog
            get() {
                return InfoDialog().apply {
                    message = "Your Turn"
                    hideMethod = "puff"
                    width = 200
                    timeout = 1000
                    messageFontSize = 20
                    height = "110"
                    messageAlign = "center"
                }
            }

        fun getErrorDialog(message: String): InfoDialog {
            return getInfoDialog(message, true)
        }

        fun getInfoDialog(message: String): InfoDialog {
            return getInfoDialog(message, false)
        }

        private fun getInfoDialog(message: String, error: Boolean): InfoDialog {
            return InfoDialog().apply {
                this.message = message
                hideMethod = "slide"
                width = 400
                timeout = 1500
                messageFontSize = 16
                messageAlign = "center"
                isError = error
            }
        }
    }
}
