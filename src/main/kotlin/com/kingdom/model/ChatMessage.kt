package com.kingdom.model

import java.io.Serializable

class ChatMessage(val message: String, val color: String, var userId: String? = null) : Serializable
