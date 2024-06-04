package uuid

import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.SecureRandom

@Suppress("NOTHING_TO_INLINE")
public inline fun ByteBuffer.getUUID_Long(): UUID {
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

@Suppress("NOTHING_TO_INLINE")
public inline fun ByteBuffer.getUUID_ByteArray(): UUID {
    val bytes = ByteArray(16)
    get(bytes)
    return UUID.fromByteArray(bytes)
}

private val secureRandom by lazy { SecureRandom() }

internal actual fun secureRandomUuid(): UUID {
    val randomBytes = ByteArray(UUID.SIZE_BYTES)
    secureRandom.nextBytes(randomBytes)
    return uuidFromRandomBytes(randomBytes)
}

@PublishedApi
@Suppress("NOTHING_TO_INLINE")
internal inline fun Long.reverseBytes(): Long = java.lang.Long.reverseBytes(this)