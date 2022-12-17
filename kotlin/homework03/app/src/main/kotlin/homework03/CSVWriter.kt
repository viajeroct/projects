package homework03

import com.soywiz.korio.file.VfsOpenMode
import com.soywiz.korio.file.std.localVfs
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

suspend fun writeDataToFile(path: String, fileName: String, data: ByteArray) {
    println("Saving $fileName...")
    val cwd = localVfs(path)
    val file = cwd[fileName].open(VfsOpenMode.CREATE_OR_TRUNCATE)
    file.write(data)
    file.close()
    println("Saved $fileName.")
}

fun <T : Any> csvSerialize(
    data: Iterable<T>, klass: KClass<T>, props: HashSet<String>? = null,
    rename: Map<String, String>? = null
) = buildString { serializeObject(data, klass, props, rename) }

private fun <T : Any> StringBuilder.serializeObject(
    data: Iterable<T>, klass: KClass<T>,
    props: HashSet<String>? = null,
    rename: Map<String, String>? = null
) {
    println("Serializing...")
    serializeHeader(klass, props, rename)
    append("\n")

    if (data.any { it.javaClass.kotlin != klass })
        throw IllegalArgumentException("not all types match")

    data.forEach {
        serializeObject(it, props)
        append("\n")
    }
}

private fun StringBuilder.serializeNumber(value: Number) = apply { append(value) }

private fun StringBuilder.serializeString(value: String) = apply {
    append('"')
    append(value)
    append('"')
}

private fun StringBuilder.serializeList(value: List<Any>) = apply {
    append(value.joinToString(separator = ",", prefix = "\"[", postfix = "]\""))
}

private fun StringBuilder.serializeValue(value: Any) = apply {
    when (value::class) {
        String::class -> serializeString(value as String)
        ArrayList::class -> serializeList(value as ArrayList<*>)
        Integer::class, Short::class, Long::class, Byte::class, Float::class, Double::class ->
            serializeNumber(value as Number)
    }
}

private fun <T : Any> StringBuilder.serializeHeader(
    klass: KClass<T>, props: HashSet<String>? = null,
    rename: Map<String, String>? = null
) = apply {
    val properties = klass.memberProperties

    when (klass) {
        String::class -> serializeString("value")
        else -> {
            properties.filter { props == null || props.contains(it.name) }
                .joinTo(this, ",") { p ->
                    serializeString(if (rename != null && rename.containsKey(p.name)) rename[p.name]!! else p.name)
                    ""
                }
        }
    }
}

private fun StringBuilder.serializeObject(value: Any, props: HashSet<String>? = null) {
    val kClass = value.javaClass.kotlin
    val properties = kClass.memberProperties

    when (kClass) {
        String::class, Integer::class, Short::class, Long::class, Byte::class, Float::class, Double::class,
        List::class -> serializeValue(value)
        else -> {
            properties.filter { props == null || props.contains(it.name) }
                .joinTo(this, ",") { p ->
                    serializeValue(p.get(value) ?: "")
                    ""
                }
        }
    }
}
