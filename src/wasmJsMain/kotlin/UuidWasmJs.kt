package uuid

import org.khronos.webgl.Int8Array
import org.khronos.webgl.get

internal actual fun secureRandomUuid(): UUID {
    val uuidString = crypto.randomUUID()
    return UUID.parse(uuidString)
}

@Suppress("ClassName")
private external object crypto {
    fun getRandomValues(bytes: Int8Array)
    fun randomUUID(): String
}

@Suppress("FunctionName")
internal fun secureRandomUUID_getRandomValues(): UUID {
    val jsRandomBytes = Int8Array(16)
    crypto.getRandomValues(jsRandomBytes)
    val randomBytes = ByteArray(16) { jsRandomBytes[it] } // so, we just copy the JS provided Int8Array into Kotlin ByteArray
    return uuidFromRandomBytes(randomBytes)
}

