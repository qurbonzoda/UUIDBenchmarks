package uuid

internal fun String.hexToInt(startIndex: Int = 0, endIndex: Int = length): Int =
    hexToIntImpl(startIndex, endIndex, typeHexLength = 8)

private fun String.hexToIntImpl(startIndex: Int, endIndex: Int, typeHexLength: Int): Int {
    checkBoundsIndexes(startIndex, endIndex, length)

    checkNumberOfDigits(startIndex, endIndex, typeHexLength)
    return parseInt(startIndex, endIndex)
}

internal fun String.hexToLong(startIndex: Int = 0, endIndex: Int = length): Long =
    hexToLongImpl(startIndex, endIndex, typeHexLength = 16)

private fun String.hexToLongImpl(startIndex: Int, endIndex: Int, typeHexLength: Int): Long {
    checkBoundsIndexes(startIndex, endIndex, length)

    checkNumberOfDigits(startIndex, endIndex, typeHexLength)
    return parseLong(startIndex, endIndex)
}

internal fun checkBoundsIndexes(startIndex: Int, endIndex: Int, size: Int) {
    if (startIndex < 0 || endIndex > size) {
        throw IndexOutOfBoundsException("startIndex: $startIndex, endIndex: $endIndex, size: $size")
    }
    if (startIndex > endIndex) {
        throw IllegalArgumentException("startIndex: $startIndex > endIndex: $endIndex")
    }
}

private fun String.checkNumberOfDigits(startIndex: Int, endIndex: Int, typeHexLength: Int) {
    val digits = endIndex - startIndex
    if (digits < 1) {
        throwInvalidNumberOfDigits(startIndex, endIndex, "at least", 1)
    } else if (digits > typeHexLength) {
        checkZeroDigits(startIndex, startIndex + digits - typeHexLength)
    }
}

private fun String.parseInt(startIndex: Int, endIndex: Int): Int {
    var result = 0
    for (i in startIndex until endIndex) {
        result = (result shl 4) or decimalFromHexDigitAt(i)
    }
    return result
}

private fun String.parseLong(startIndex: Int, endIndex: Int): Long {
    var result = 0L
    for (i in startIndex until endIndex) {
        result = (result shl 4) or longDecimalFromHexDigitAt(i)
    }
    return result
}

private fun String.throwInvalidNumberOfDigits(startIndex: Int, endIndex: Int, specifier: String, expected: Int) {
    val substring = substring(startIndex, endIndex)
    throw NumberFormatException(
        "Expected $specifier $expected hexadecimal digits at index $startIndex, but was \"$substring\" of length ${endIndex - startIndex}"
    )
}

private fun String.checkZeroDigits(startIndex: Int, endIndex: Int) {
    for (index in startIndex until endIndex) {
        if (this[index] != '0') {
            throw NumberFormatException(
                "Expected the hexadecimal digit '0' at index $index, but was '${this[index]}'.\n" +
                        "The result won't fit the type being parsed."
            )
        }
    }
}


@Suppress("NOTHING_TO_INLINE")
private inline fun String.decimalFromHexDigitAt(index: Int): Int {
    val code = this[index].code
    if (code ushr 8 == 0 && HEX_DIGITS_TO_DECIMAL[code] >= 0) {
        return HEX_DIGITS_TO_DECIMAL[code]
    }
    throwInvalidDigitAt(index)
}

@Suppress("NOTHING_TO_INLINE")
private inline fun String.longDecimalFromHexDigitAt(index: Int): Long {
    val code = this[index].code
    if (code ushr 8 == 0 && HEX_DIGITS_TO_LONG_DECIMAL[code] >= 0) {
        return HEX_DIGITS_TO_LONG_DECIMAL[code]
    }
    throwInvalidDigitAt(index)
}

private fun String.throwInvalidDigitAt(index: Int): Nothing {
    throw NumberFormatException("Expected a hexadecimal digit at index $index, but was ${this[index]}")
}

private val HEX_DIGITS_TO_DECIMAL = IntArray(256) { -1 }.apply {
    LOWER_CASE_HEX_DIGITS.forEachIndexed { index, char -> this[char.code] = index }
    UPPER_CASE_HEX_DIGITS.forEachIndexed { index, char -> this[char.code] = index }
}

private val HEX_DIGITS_TO_LONG_DECIMAL = LongArray(256) { -1 }.apply {
    LOWER_CASE_HEX_DIGITS.forEachIndexed { index, char -> this[char.code] = index.toLong() }
    UPPER_CASE_HEX_DIGITS.forEachIndexed { index, char -> this[char.code] = index.toLong() }
}

private const val LOWER_CASE_HEX_DIGITS = "0123456789abcdef"
private const val UPPER_CASE_HEX_DIGITS = "0123456789ABCDEF"

internal val BYTE_TO_LOWER_CASE_HEX_DIGITS = IntArray(256) {
    (LOWER_CASE_HEX_DIGITS[(it shr 4)].code shl 8) or LOWER_CASE_HEX_DIGITS[(it and 0xF)].code
}