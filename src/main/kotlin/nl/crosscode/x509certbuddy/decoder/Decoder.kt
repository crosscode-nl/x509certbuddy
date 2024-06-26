package nl.crosscode.x509certbuddy.decoder

import java.util.*

class Decoder(val originalOffset: Int) {
    private val base64Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    private var data = ""
    private var readPadding = false

    private var count = 0

    var isDone: Boolean = false
        private set

    private var escapeMode = false

    fun add(ic: Char): Boolean {
        // TODO: Alternative alphabets
        var c = ic
        if (isDone) return true
        count++
        if (escapeMode) {
            escapeMode = false
            c = unescape(c) ?: return false
        } else if (c == '\\') {
            escapeMode = true
            return false
        }

        if (c == ' ' || c == '\n' || c == '\r' || c == '\t') return false

        if (base64Alphabet.indexOf(c) == -1) {
            isDone = true
            count--
            return true
        }

        if (readPadding && c != '=') {
            count--
            isDone = true
            return true
        }
        if (c == '=') readPadding = true
        data += c
        return false
    }

    fun tryDecode(): ByteArray = Base64.getDecoder().decode(data)

    fun isOffsetInsideRange(offset: Int): Boolean = offset >= originalOffset && offset < originalOffset + count

    private fun unescape(c: Char): Char? = when (c) {
        'n' -> '\n'
        'r' -> '\r'
        't' -> '\t'
        '\\' -> '\\'
        else -> null
    }
}
