package uuid


internal fun String.hexToLong(startIndex: Int = 0, endIndex: Int = length): Long =
    hexToLongImpl(startIndex, endIndex, maxDigits = 16)

private fun String.hexToLongImpl(startIndex: Int, endIndex: Int, maxDigits: Int): Long {
    checkBoundsIndexes(startIndex, endIndex, length)

    checkMaxDigits(startIndex, endIndex, maxDigits)
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

private fun String.checkMaxDigits(startIndex: Int, endIndex: Int, maxDigits: Int) {
    if (startIndex >= endIndex || endIndex - startIndex > maxDigits) {
        throwInvalidNumberOfDigits(startIndex, endIndex, maxDigits, requireMaxLength = false)
    }
}

private fun String.parseLong(startIndex: Int, endIndex: Int): Long {
    var result = 0L
    for (i in startIndex until endIndex) {
        result = (result shl 4) or longDecimalFromHexDigitAt(i)
    }
    return result
}

private fun String.throwInvalidNumberOfDigits(startIndex: Int, endIndex: Int, maxDigits: Int, requireMaxLength: Boolean) {
    val specifier = if (requireMaxLength) "exactly" else "at most"
    val substring = substring(startIndex, endIndex)
    throw NumberFormatException(
        "Expected $specifier $maxDigits hexadecimal digits at index $startIndex, but was $substring of length ${endIndex - startIndex}"
    )
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

private val HEX_DIGITS_TO_LONG_DECIMAL = LongArray(256) { -1 }.apply {
    LOWER_CASE_HEX_DIGITS.forEachIndexed { index, char -> this[char.code] = index.toLong() }
    UPPER_CASE_HEX_DIGITS.forEachIndexed { index, char -> this[char.code] = index.toLong() }
}

private const val LOWER_CASE_HEX_DIGITS = "0123456789abcdef"
private const val UPPER_CASE_HEX_DIGITS = "0123456789ABCDEF"

internal val BYTE_TO_LOWER_CASE_HEX_DIGITS = IntArray(256) {
    (LOWER_CASE_HEX_DIGITS[(it shr 4)].code shl 8) or LOWER_CASE_HEX_DIGITS[(it and 0xF)].code
}