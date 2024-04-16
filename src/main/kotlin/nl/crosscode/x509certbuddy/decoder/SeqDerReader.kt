package nl.crosscode.x509certbuddy.decoder

import java.math.BigInteger

class SeqDerReader {
    enum class Mode {
        TAG,
        FIRST_LENGTH,
        EXTENDED_LENGTH,
        VALUE,
        DONE,
        ERROR
    }

    private var mode: Mode = Mode.TAG

    private var tag: Byte = 0
    private var firstLength: Byte = 0

    private var lengthBytes = 0
    private var length: ByteArray? = null
    private var lengthIndex = 0
    private var valueLength = 0
    private lateinit var value: ByteArray
    private var valueIndex = 0

    private var result: ByteArray? = null

    @Throws(Exception::class)
    fun read(b: Byte) {
        when (mode) {
            Mode.TAG -> {
                handleTag(b)
                return
            }

            Mode.FIRST_LENGTH -> {
                handleFirstLength(b)
                return
            }

            Mode.EXTENDED_LENGTH -> {
                handleExtendedLength(b)
                return
            }

            Mode.VALUE -> handleValue(b)
            Mode.DONE -> return
            Mode.ERROR -> error("cannot read more bytes")
        }
    }

    private fun handleValue(b: Byte) {
        value[valueIndex++] = b
        if (valueIndex == valueLength) {
            mode = Mode.DONE
        }
    }

    @Throws(Exception::class)
    private fun handleExtendedLength(b: Byte) {
        length!![lengthIndex++] = b
        if (lengthBytes > 2) {
            throw Exception("Not a cert") // Not sure, but use this as an optimisation for now.
        }
        if (lengthIndex == lengthBytes) {
            mode = Mode.VALUE
            valueLength = BigInteger(length).toInt()
            value = ByteArray(valueLength)
        }
    }

    private fun handleTag(b: Byte) {
        tag = b
        mode = Mode.FIRST_LENGTH
    }

    private fun handleFirstLength(b: Byte) {
        firstLength = b
        if (b.toInt() == 0) {
            mode = Mode.ERROR
            return
        }
        if (b >= 0) {
            valueLength = b.toInt()
            mode = Mode.VALUE
            value = ByteArray(valueLength)
            return
        }
        lengthBytes = b.toInt() and 0x7F
        if (lengthBytes == 0) {
            mode = Mode.ERROR
            return
        }
        mode = Mode.EXTENDED_LENGTH
        length = ByteArray(lengthBytes)
    }

    fun getResult(): ByteArray? {
        if (mode != Mode.DONE) {
            return null
        }
        if (result == null) {
            val bytes = 2 + lengthBytes + valueLength
            var result = ByteArray(bytes)
            result[0] = tag
            result[1] = firstLength
            this.result = result
            length?.let {
                System.arraycopy(it, 0, result, 2, lengthBytes)
            }
            System.arraycopy(value, 0, result, 2 + lengthBytes, valueLength)
        }
        return result
    }

    fun setEof() {
        if (mode != Mode.DONE && mode != Mode.ERROR) {
            mode = Mode.ERROR
        }
    }

    val isDone: Boolean
        get() = mode == Mode.DONE || mode == Mode.ERROR

    val isError: Boolean
        get() = mode == Mode.ERROR
}
