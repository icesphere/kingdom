package com.kingdom.util

fun String.plural(num: Int): String {
    var wordCopy = this
    return if (num == 1 || num == -1 || wordCopy.endsWith("s")) {
        "$num $wordCopy"
    } else {
        when {
            wordCopy == "Envoy" -> "$num Envoys"
            wordCopy == "Highway" -> "$num Highways"
            wordCopy.endsWith("y") -> {
                wordCopy = wordCopy.substring(0, wordCopy.length - 1) + "ies"
                "$num $wordCopy"
            }
            wordCopy == "Witch" -> "$num Witches"
            wordCopy == "Golden Touch" -> "$num Golden Touches"
            else -> num.toString() + " " + wordCopy + "s"
        }
    }
}