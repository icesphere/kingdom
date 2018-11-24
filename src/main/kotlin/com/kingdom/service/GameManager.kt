package com.kingdom.service

import com.kingdom.model.GameError
import org.springframework.stereotype.Service

@Service
class GameManager {

    fun logError(error: GameError) {
        if (error.error!!.length > 20000) {
            error.error = error.error!!.substring(0, 19990) + "..."
        }
        //todo show error somewhere
    }

}
