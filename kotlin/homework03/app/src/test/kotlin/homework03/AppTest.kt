package homework03

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class AppTest {
    @Test
    fun testComments() = runBlocking {
        val urlToComments = "https://www.reddit.com/r/Kotlin/comments/z3tw2r/tfidf"
        val redditClient = RedditClient()
        val res = redditClient.getComments(urlToComments)
        println(res)
    }

    private fun dfs(v: CommentsTreeNode?) {
        if (v == null) {
            return
        }
        println(v)
        v.children.forEach { dfs(it) }
    }

    @Test
    fun testCommentsTree() = runBlocking {
        val urlToComments = "https://www.reddit.com/r/Kotlin/comments/z3tw2r/tfidf"
        val redditClient = RedditClient()
        val res = redditClient.getComments(urlToComments)
        dfs(res.root)
    }

    @Test
    fun testCommentsByTopic() = runBlocking {
        val topic = "Kotlin"
        val redditClient = RedditClient()
        val res = redditClient.getCommentsByTopic(redditClient.getTopic(topic))
        println(res)
    }

    @Test
    fun testTopic() = runBlocking {
        val topic = "Kotlin"
        val redditClient = RedditClient()
        val res = redditClient.getTopic(topic)
        println(res)
    }

    @Test
    fun testCSV_1() = runBlocking {
        val res = csvSerialize(listOf(1, 2, 3), Int::class)
        println(res)
    }

    @Test
    fun testCSV_2() = runBlocking {
        val topic = "Kotlin"
        val redditClient = RedditClient()
        val res = redditClient.getTopic(topic)
        println(csvSerialize(res.listOfPosts, TopicMessage::class, hashSetOf("uniqueTopicId", "title")))
    }

    @Test
    fun testCSV_3() = runBlocking {
        val dt = Pair(arrayListOf("1", "2", "3"), "nikita")
        val res = csvSerialize(
            listOf(dt), Pair::class, hashSetOf("first"),
            rename = hashMapOf("first" to "label")
        )
        print(res)
    }

    @Test
    fun testFilesKorio() = runBlocking {
        val path = "C:\\viajero\\kotlin\\kotlin-hse-2022\\homework03\\app\\src\\var"
        writeDataToFile(path, "file.txt", "Hello, world!\n".toByteArray())
    }

    @Test
    fun testTime() {
        println(Time(1.66932749E9F))
        println(Time.getCurrentTime())
    }
}
