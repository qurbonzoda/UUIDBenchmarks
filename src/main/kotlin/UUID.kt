package uuid

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.SecureRandom

public class UUID private constructor(
    val mostSignificantBits: Long,
    val leastSignificantBits: Long
) {

    override fun toString(): String {
        return uuidToString(this)
    }

    public fun toHexString(): String {
        return uuidToHexString(this)
    }

    public fun toByteArray(): ByteArray {
        return uuidToByteArray(this)
    }

    companion object {
        public fun fromLongs(mostSignificantBits: Long, leastSignificantBits: Long): UUID =
            UUID(mostSignificantBits, leastSignificantBits)

        public fun fromByteArray(byteArray: ByteArray): UUID =
            UUID(byteArray.toLong(startIndex = 0), byteArray.toLong(startIndex = 8))

        public const val SIZE_BYTES: Int = 16

        public fun parse(uuidString: String): UUID =
            uuidFromString(uuidString)

        public fun parseHex(hexString: String): UUID =
            uuidFromHexString(hexString)

        public fun random(): UUID {
            return secureRandomUUID()
        }
    }
}

private fun ByteArray.toLong(startIndex: Int): Long {
    return ((this[startIndex + 0].toLong() and 0xFF) shl 56) or
            ((this[startIndex + 1].toLong() and 0xFF) shl 48) or
            ((this[startIndex + 2].toLong() and 0xFF) shl 40) or
            ((this[startIndex + 3].toLong() and 0xFF) shl 32) or
            ((this[startIndex + 4].toLong() and 0xFF) shl 24) or
            ((this[startIndex + 5].toLong() and 0xFF) shl 16) or
            ((this[startIndex + 6].toLong() and 0xFF) shl 8) or
            (this[startIndex + 7].toLong() and 0xFF)
}

@Suppress("NOTHING_TO_INLINE")
public inline fun ByteBuffer.getUUID_Long(): UUID {
    val msb: Long
    val lsb: Long
    if (order() == ByteOrder.BIG_ENDIAN) {
        msb = getLong()
        lsb = getLong()
    } else {
        lsb = getLong()
        msb = getLong()
    }
    return UUID.fromLongs(msb, lsb)
}

@Suppress("NOTHING_TO_INLINE")
public inline fun ByteBuffer.getUUID_ByteArray(): UUID {
    val bytes = ByteArray(16)
    get(bytes)
    if (order() == ByteOrder.LITTLE_ENDIAN) {
        bytes.reverse()
    }
    return UUID.fromByteArray(bytes)
}

internal fun uuidToString(uuid: UUID): String = with(uuid) {
    val bytes = ByteArray(36)
    leastSignificantBits.formatBytesInto(bytes, 24, 6)
    bytes[23] = '-'.code.toByte()
    (leastSignificantBits ushr 48).formatBytesInto(bytes, 19, 2)
    bytes[18] = '-'.code.toByte()
    mostSignificantBits.formatBytesInto(bytes, 14, 2)
    bytes[13] = '-'.code.toByte()
    (mostSignificantBits ushr 16).formatBytesInto(bytes, 9, 2)
    bytes[8] = '-'.code.toByte()
    (mostSignificantBits ushr 32).formatBytesInto(bytes, 0, 4)

    return bytes.decodeToString()
}

private fun Long.formatBytesInto(dst: ByteArray, dstOffset: Int, count: Int) {
    var long = this
    var dstIndex = dstOffset + 2 * count
    repeat(count) {
        val byte = (long and 0xFF).toInt()
        val byteDigits = BYTE_TO_LOWER_CASE_HEX_DIGITS[byte]
        dst[--dstIndex] = byteDigits.toByte()
        dst[--dstIndex] = (byteDigits shr 8).toByte()
        long = long shr 8
    }
}

internal fun uuidToHexString(uuid: UUID): String = with(uuid) {
    val bytes = ByteArray(32)
    leastSignificantBits.formatBytesInto(bytes, 16, 8)
    mostSignificantBits.formatBytesInto(bytes, 0, 8)
    return bytes.decodeToString()
}

internal fun uuidToByteArray(uuid: UUID): ByteArray = with(uuid) {
    val bytes = ByteArray(UUID.SIZE_BYTES)
    mostSignificantBits.toByteArray(bytes, 0)
    leastSignificantBits.toByteArray(bytes, 8)
    return bytes
}

private fun Long.toByteArray(dst: ByteArray, dstOffset: Int) {
    for (index in 0 until 8) {
        val shift = 8 * (7 - index)
        dst[dstOffset + index] = (this ushr shift).toByte()
    }
}

internal fun uuidFromString(uuidString: String): UUID {
    require(uuidString.length == 36) { "Expected a 36-char string in the standard UUID format." }

    val part1 = uuidString.hexToLong(startIndex = 0, endIndex = 8)
    uuidString.checkHyphenAt(8)
    val part2 = uuidString.hexToLong(startIndex = 9, endIndex = 13)
    uuidString.checkHyphenAt(13)
    val part3 = uuidString.hexToLong(startIndex = 14, endIndex = 18)
    uuidString.checkHyphenAt(18)
    val part4 = uuidString.hexToLong(startIndex = 19, endIndex = 23)
    uuidString.checkHyphenAt(23)
    val part5 = uuidString.hexToLong(startIndex = 24, endIndex = 36)

    val msb = (part1 shl 32) or (part2 shl 16) or part3
    val lsb = (part4 shl 48) or part5
    return UUID.fromLongs(msb, lsb)
}

internal fun uuidFromHexString(hexString: String): UUID {
    require(hexString.length == 32) { "Expected a 32-char hexadecimal string." }
    val msb = hexString.hexToLong(startIndex = 0, endIndex = 16)
    val lsb = hexString.hexToLong(startIndex = 16, endIndex = 32)
    return UUID.fromLongs(msb, lsb)
}

private fun String.checkHyphenAt(index: Int) {
    require(this[8] == '-') { "Expected '-' (hyphen) at index 8, but was ${this[index]}" }
}

private val secureRandom by lazy { SecureRandom() }

internal fun secureRandomUUID(): UUID {
    val randomBytes = ByteArray(UUID.SIZE_BYTES)
    secureRandom.nextBytes(randomBytes)
    return uuidFromRandomBytes(randomBytes)
}

internal fun uuidFromRandomBytes(randomBytes: ByteArray): UUID {
    randomBytes[6] = (randomBytes[6].toInt() and 0x0f).toByte() /* clear version        */
    randomBytes[6] = (randomBytes[6].toInt() or 0x40).toByte()  /* set to version 4     */
    randomBytes[8] = (randomBytes[8].toInt() and 0x3f).toByte() /* clear variant        */
    randomBytes[8] = (randomBytes[8].toInt() or 0x80).toByte()  /* set to IETF variant  */
    return UUID.fromByteArray(randomBytes)
}