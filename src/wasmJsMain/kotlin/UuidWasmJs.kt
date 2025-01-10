package uuid

internal actual fun secureRandomUuid(): UUID =
    secureRandomUUID_getRandomValues()

internal fun secureRandomUuid_randomUUID(): UUID {
    val uuidString = crypto.randomUUID()
    return UUID.parse(uuidString)
}

@Suppress("ClassName")
private external object crypto {
    fun randomUUID(): String
}


private fun cryptoGetRandomValues(size: Int): JsAny =
    js("crypto.getRandomValues(new Int8Array(size))")
private fun get(array: JsAny, index: Int): Byte =
    js("array[index]")

@Suppress("FunctionName")
internal fun secureRandomUUID_getRandomValues(): UUID {
    val int8Array = cryptoGetRandomValues(UUID.SIZE_BYTES)
    val randomBytes = ByteArray(UUID.SIZE_BYTES) { get(int8Array, it) }
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
