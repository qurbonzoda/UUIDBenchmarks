package uuid

import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.SecureRandom

public fun ByteBuffer.getUUID_Long(): UUID {
    if (position() + 15 >= limit()) {
        throw BufferUnderflowException() // otherwise a partial read could occur
    }
    var msb = getLong()
    var lsb = getLong()
    if (order() == ByteOrder.LITTLE_ENDIAN) {
        msb = msb.reverseBytes()
        lsb = lsb.reverseBytes()
    }
    return UUID.fromLongs(msb, lsb)
}

public fun ByteBuffer.getUUID_ByteArray(): UUID {
    val bytes = ByteArray(16)
    get(bytes)
    return UUID.fromByteArray(bytes)
}

@PublishedApi
@Suppress("NOTHING_TO_INLINE")
internal inline fun Long.reverseBytes(): Long = java.lang.Long.reverseBytes(this)


private object SecureRandomHolder {
    val instance = SecureRandom()
}

internal actual fun secureRandomUuid(): UUID {
    val randomBytes = ByteArray(UUID.SIZE_BYTES)
    SecureRandomHolder.instance.nextBytes(randomBytes)
    return uuidFromRandomBytes(randomBytes)
}

internal actual fun ByteArray.toLong(startIndex: Int): Long =
    toLongCommonImpl(startIndex)

internal actual fun Long.formatBytesInto(dst: ByteArray, dstOffset: Int, startIndex: Int, endIndex: Int) =
    formatBytesIntoCommonImpl(dst, dstOffset, startIndex, endIndex)

internal actual fun Long.toByteArray(dst: ByteArray, dstOffset: Int) =
    toByteArrayCommonImpl(dst, dstOffset)

internal actual fun uuidParseHexDash(hexDashString: String): UUID =
    uuidParseHexDashCommonImpl(hexDashString)

internal actual fun uuidParseHex(hexString: String): UUID =
    uuidParseHexCommonImpl(hexString)
