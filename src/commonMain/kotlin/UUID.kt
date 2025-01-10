package uuid

public class UUID private constructor(
    private val mostSignificantBits: Long,
    private val leastSignificantBits: Long
) {

    override fun toString(): String {
        val bytes = ByteArray(36)
        leastSignificantBits.formatBytesInto(bytes, 35, startIndex = 0, endIndex = 6)
        bytes[23] = '-'.code.toByte()
        leastSignificantBits.formatBytesInto(bytes, 22, startIndex = 6, endIndex = 8)
        bytes[18] = '-'.code.toByte()
        mostSignificantBits.formatBytesInto(bytes, 17, startIndex = 0, endIndex = 2)
        bytes[13] = '-'.code.toByte()
        mostSignificantBits.formatBytesInto(bytes, 12, startIndex = 2, endIndex = 4)
        bytes[8] = '-'.code.toByte()
        mostSignificantBits.formatBytesInto(bytes, 7, startIndex = 4, endIndex = 8)
        return bytes.decodeToString()
    }

    public fun toHexString(): String {
        val bytes = ByteArray(32)
        leastSignificantBits.formatBytesInto(bytes, 31, startIndex = 0, endIndex = 8)
        mostSignificantBits.formatBytesInto(bytes, 15, startIndex = 0, endIndex = 8)
        return bytes.decodeToString()
    }

    public fun toByteArray(): ByteArray {
        val bytes = ByteArray(SIZE_BYTES)
        leastSignificantBits.toByteArray(bytes, 15)
        mostSignificantBits.toByteArray(bytes, 7)
        return bytes
    }

    companion object {
        public fun fromLongs(mostSignificantBits: Long, leastSignificantBits: Long): UUID =
            UUID(mostSignificantBits, leastSignificantBits)

        public fun fromByteArray(byteArray: ByteArray): UUID {
            require(byteArray.size == SIZE_BYTES) {
                "Expected exactly $SIZE_BYTES bytes, but was ${byteArray.truncateForErrorMessage(32)} of size ${byteArray.size}"
            }

            return fromLongs(byteArray.toLong(startIndex = 0), byteArray.toLong(startIndex = 8))
        }

        public const val SIZE_BYTES: Int = 16

        public fun parse(uuidString: String): UUID {
            require(uuidString.length == 36) {
                "Expected a 36-char string in the standard hex-and-dash UUID format, but was \"${uuidString.truncateForErrorMessage(64)}\" of length ${uuidString.length}"
            }
            return uuidParseHexDash(uuidString)
        }

        public fun parseHex(hexString: String): UUID {
            require(hexString.length == 32) {
                "Expected a 32-char hexadecimal string, but was \"${hexString.truncateForErrorMessage(64)}\" of length ${hexString.length}"
            }
            return uuidParseHex(hexString)
        }

        public fun random(): UUID =
            secureRandomUuid()
    }
}

internal expect fun secureRandomUuid(): UUID

internal fun uuidFromRandomBytes(randomBytes: ByteArray): UUID {
    randomBytes[6] = (randomBytes[6].toInt() and 0x0f).toByte() /* clear version        */
    randomBytes[6] = (randomBytes[6].toInt() or 0x40).toByte()  /* set to version 4     */
    randomBytes[8] = (randomBytes[8].toInt() and 0x3f).toByte() /* clear variant        */
    randomBytes[8] = (randomBytes[8].toInt() or 0x80).toByte()  /* set to IETF variant  */
    return UUID.fromByteArray(randomBytes)
}

// Implement differently in JS to avoid bitwise operations with Longs
internal expect fun ByteArray.toLong(startIndex: Int): Long

internal fun ByteArray.toLongCommonImpl(startIndex: Int): Long {
    return ((this[startIndex + 0].toLong() and 0xFF) shl 56) or
            ((this[startIndex + 1].toLong() and 0xFF) shl 48) or
            ((this[startIndex + 2].toLong() and 0xFF) shl 40) or
            ((this[startIndex + 3].toLong() and 0xFF) shl 32) or
            ((this[startIndex + 4].toLong() and 0xFF) shl 24) or
            ((this[startIndex + 5].toLong() and 0xFF) shl 16) or
            ((this[startIndex + 6].toLong() and 0xFF) shl 8) or
            (this[startIndex + 7].toLong() and 0xFF)
}

// Implement differently in JS to avoid bitwise operations with Longs
internal expect fun Long.formatBytesInto(dst: ByteArray, dstOffset: Int, startIndex: Int, endIndex: Int)

internal fun Long.formatBytesIntoCommonImpl(dst: ByteArray, dstOffset: Int, startIndex: Int, endIndex: Int) {
    var dstIndex = dstOffset
    for (index in startIndex until endIndex) {
        val shift = 8 * index
        val byte = ((this shr shift) and 0xFF).toInt()
        val byteDigits = BYTE_TO_LOWER_CASE_HEX_DIGITS[byte]
        dst[dstIndex--] = byteDigits.toByte()
        dst[dstIndex--] = (byteDigits shr 8).toByte()
    }
}

internal fun String.checkHyphenAt(index: Int) {
    require(this[index] == '-') { "Expected '-' (hyphen) at index $index, but was '${this[index]}'" }
}

// Implement differently in JS to avoid bitwise operations with Longs
internal expect fun Long.toByteArray(dst: ByteArray, dstOffset: Int)

internal fun Long.toByteArrayCommonImpl(dst: ByteArray, dstOffset: Int) {
    for (index in 0 until 8) {
        val shift = 8 * index
        dst[dstOffset - index] = (this shr shift).toByte()
    }
}

// Implement differently in JS to avoid bitwise operations with Longs
internal expect fun uuidParseHexDash(hexDashString: String): UUID

internal fun uuidParseHexDashCommonImpl(hexDashString: String): UUID {
    // xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
    // 16 hex digits fit into a Long
    val part1 = hexDashString.hexToLong(startIndex = 0, endIndex = 8)
    hexDashString.checkHyphenAt(8)
    val part2 = hexDashString.hexToLong(startIndex = 9, endIndex = 13)
    hexDashString.checkHyphenAt(13)
    val part3 = hexDashString.hexToLong(startIndex = 14, endIndex = 18)
    hexDashString.checkHyphenAt(18)
    val part4 = hexDashString.hexToLong(startIndex = 19, endIndex = 23)
    hexDashString.checkHyphenAt(23)
    val part5 = hexDashString.hexToLong(startIndex = 24, endIndex = 36)

    val msb = (part1 shl 32) or (part2 shl 16) or part3
    val lsb = (part4 shl 48) or part5
    return UUID.fromLongs(msb, lsb)
}

// Implement differently in JS to avoid bitwise operations with Longs
internal expect fun uuidParseHex(hexString: String): UUID

internal fun uuidParseHexCommonImpl(hexString: String): UUID {
    // 16 hex digits fit into a Long
    val msb = hexString.hexToLong(startIndex = 0, endIndex = 16)
    val lsb = hexString.hexToLong(startIndex = 16, endIndex = 32)
    return UUID.fromLongs(msb, lsb)
}

private fun String.truncateForErrorMessage(maxLength: Int): String {
    return if (length <= maxLength) this else substring(0, maxLength) + "..."
}

private fun ByteArray.truncateForErrorMessage(maxSize: Int): String {
    return joinToString(prefix = "[", postfix = "]", limit = maxSize)
}
