package homework03

import com.soywiz.korio.async.launch
import kotlinx.coroutines.runBlocking

suspend fun getAndSaveTopic(title: String) {
    val path = "C:\\viajero\\kotlin\\kotlin-hse-2022\\homework03\\app\\src\\var\\"
    println("Collecting info about $title.")
    try {
        with(RedditClient()) {
            val topic = getTopic(title)
            val allComments = getCommentsByTopic(topic)
            saveData(topic, allComments, title, path)
        }
    } catch (e: Exception) {
        println("Error occurred in $title.")
        println(e.message)
        return
    }
    println("All finished with exit code 0 in $title.")
}

fun main(args: Array<String>): Unit = runBlocking {
    args.forEach {
        launch {
            getAndSaveTopic(it)
        }
    }
}
