package com.kingdom.util

fun String.plural(num: Int): String {
    var wordCopy = this
    if (num == 1 || num == -1 || wordCopy.endsWith("s")) {
        return num.toString() + " " + wordCopy
    } else {
        return when {
            wordCopy == "Envoy" -> num.toString() + " " + "Envoys"
            wordCopy.endsWith("y") -> {
                wordCopy = wordCopy.substring(0, wordCopy.length - 1) + "ies"
                num.toString() + " " + wordCopy
            }
            wordCopy == "Witch" -> num.toString() + " " + "Witches"
            else -> num.toString() + " " + wordCopy + "s"
        }
    }
}