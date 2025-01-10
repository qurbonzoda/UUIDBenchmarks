/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package uuid

internal actual fun secureRandomUuid(): UUID {
    val randomBytes = ByteArray(16)
    js("crypto").getRandomValues(randomBytes)
    return uuidFromRandomBytes(randomBytes)
}

internal actual fun ByteArray.toLong(startIndex: Int): Long {
    return Long(
        high = this.toInt(startIndex),
        low = this.toInt(startIndex + 4),
    )
}

private fun ByteArray.toInt(startIndex: Int): Int {
    return ((this[startIndex + 0].toInt() and 0xFF) shl 24) or
            ((this[startIndex + 1].toInt() and 0xFF) shl 16) or
            ((this[startIndex + 2].toInt() and 0xFF) shl 8) or
            (this[startIndex + 3].toInt() and 0xFF)
}

// Avoid bitwise operations with Longs in JS
internal actual fun Long.formatBytesInto(dst: ByteArray, dstOffset: Int, startIndex: Int, endIndex: Int) {
    if (startIndex < 4) {
        this.low.formatBytesInto(dst, dstOffset, startIndex, endIndex.coerceAtMost(4))
    }
    if (endIndex > 4) {
        val lowDigitsCount = 2 * (4 - startIndex).coerceAtLeast(0)
        this.high.formatBytesInto(dst, dstOffset - lowDigitsCount, (startIndex - 4).coerceAtLeast(0), endIndex - 4)
    }
}

private fun Int.formatBytesInto(dst: ByteArray, dstOffset: Int, startIndex: Int, endIndex: Int) {
    var dstIndex = dstOffset
    for (index in startIndex until endIndex) {
        val shift = 8 * index
        val byte = (this shr shift) and 0xFF
        val byteDigits = BYTE_TO_LOWER_CASE_HEX_DIGITS[byte]
        dst[dstIndex--] = byteDigits.toByte()
        dst[dstIndex--] = (byteDigits shr 8).toByte()
    }
}

internal actual fun Long.toByteArray(dst: ByteArray, dstOffset: Int) {
    this.low.toByteArray(dst, dstOffset)
    this.high.toByteArray(dst, dstOffset - 4)
}

private fun Int.toByteArray(dst: ByteArray, dstOffset: Int) {
    for (index in 0 until 4) {
        val shift = 8 * index
        dst[dstOffset - index] = (this shr shift).toByte()
    }
}

// Avoid bitwise operations with Longs in JS
internal actual fun uuidParseHexDash(hexDashString: String): UUID {
    // xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
    // 8 hex digits fit into an Int
    val part1 = hexDashString.hexToInt(startIndex = 0, endIndex = 8)
    hexDashString.checkHyphenAt(8)
    val part2 = hexDashString.hexToInt(startIndex = 9, endIndex = 13)
    hexDashString.checkHyphenAt(13)
    val part3 = hexDashString.hexToInt(startIndex = 14, endIndex = 18)
    hexDashString.checkHyphenAt(18)
    val part4 = hexDashString.hexToInt(startIndex = 19, endIndex = 23)
    hexDashString.checkHyphenAt(23)
    val part5a = hexDashString.hexToInt(startIndex = 24, endIndex = 28)
    val part5b = hexDashString.hexToInt(startIndex = 28, endIndex = 36)

    val msb = Long(
        high = part1,
        low = (part2 shl 16) or part3
    )
    val lsb = Long(
        high = (part4 shl 16) or part5a,
        low = part5b
    )
    return UUID.fromLongs(msb, lsb)
}

// Avoid bitwise operations with Longs in JS
internal actual fun uuidParseHex(hexString: String): UUID {
    // 8 hex digits fit into an Int
    val msb = Long(
        high = hexString.hexToInt(startIndex = 0, endIndex = 8),
        low = hexString.hexToInt(startIndex = 8, endIndex = 16)
    )
    val lsb = Long(
        high = hexString.hexToInt(startIndex = 16, endIndex = 24),
        low = hexString.hexToInt(startIndex = 24, endIndex = 32)
    )
    return UUID.fromLongs(msb, lsb)
}

// These members are internal in Long, so implement them here,
// even though with additional overhead (allocations + operations)

private fun Long(low: Int, high: Int): Long {
    return (high.toLong() shl 32) + low
}

private val Long.high: Int
    get() = (this shr 32).toInt()

private val Long.low: Int
    get() = this.toInt()