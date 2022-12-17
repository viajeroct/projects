package homework03

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.soywiz.korio.async.async
import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.lang.Charsets.UTF8
import com.soywiz.korio.lang.toByteArray
import com.soywiz.korio.net.http.Http
import com.soywiz.korio.net.http.createHttpClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Time(timeStamp: Float) : Date(timeStamp.toLong() * 1000) {
    companion object {
        private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        fun getCurrentTime(): String = sdf.format(Date()).toString()
    }

    override fun toString(): String {
        return sdf.format(this)
    }
}

class RedditClient {
    private val urlPrefix = "https://www.reddit.com/r/"
    private val mapper = jacksonObjectMapper()

    data class TopicSnapshot(
        val createTime: String, val aboutCommunity: AboutCommunity,
        val listOfPosts: List<TopicMessage>
    )

    data class CommentsSnapshot(
        val createTime: String, val listOfComments: List<CommentMessage>,
        val idFromTopicMessage: String,
        val root: CommentsTreeNode
    )

    private fun String.toTopicMessage() = mapper.readValue(this, TopicMessage::class.java)
    private fun String.toCommentMessage() = mapper.readValue(this, CommentMessage::class.java)
    private fun String.toAboutInfo() = mapper.readValue(this, AboutCommunity::class.java)

    private suspend fun getJSONAsString(link: String): String = createHttpClient().request(
        Http.Method.GET, link, Http.Headers.build {
            put("Accept-Encoding", "application/json")
        }).readAllString(UTF8)

    suspend fun getTopic(name: String): TopicSnapshot {
        val link = "${urlPrefix}${name}/.json"
        val aboutLink = "${urlPrefix}${name}/about.json"
        val resJSONString = getJSONAsString(link)
        val treeJSON = mapper.readTree(resJSONString)
        val listOfPosts = treeJSON.path("data").path("children").toList()
        val list = listOfPosts.map { it.path("data").toString().toTopicMessage() }
        val aboutJSONString = getJSONAsString(aboutLink)
        val about = mapper.readTree(aboutJSONString).path("data").toString().toAboutInfo()
        return TopicSnapshot(Time.getCurrentTime(), about, list)
    }

    private fun analyzeReplies(
        node: JsonNode,
        list: ArrayList<CommentMessage>
    ): CommentsTreeNode? {
        if (node.path("data").path("author").toString().isEmpty()) {
            return null
        }
        val curReply = node.path("data").toString().toCommentMessage()
        list.add(curReply)
        if (!node.path("data").path("replies").has("data")) {
            return CommentsTreeNode(curReply)
        }
        val listOfReplies = node.path("data").path("replies").path("data")
            .path("children")
        val listOfNodes = listOfReplies.map { analyzeReplies(it, list) }
        return CommentsTreeNode(curReply, listOfNodes)
    }

    private suspend fun getCommentsByTopicMessage(topic: TopicMessage): CommentsSnapshot {
        val baseURL = "https://www.reddit.com"
        val url = "${baseURL}${topic.permalink}"
        return getComments(url, topic.uniqueTopicId)
    }

    suspend fun getCommentsByTopic(topic: TopicSnapshot): List<CommentsSnapshot> =
        topic.listOfPosts.map { getCommentsByTopicMessage(it) }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun saveData(topic: TopicSnapshot, comments: List<CommentsSnapshot>, title: String, path: String) {
        val topicFileName = "subjects.csv"
        val commentsFileName = "comments.csv"

        val topicsInfo = GlobalScope.async {
            csvSerialize(topic.listOfPosts, TopicMessage::class, hashSetOf("title", "authorName", "uniqueTopicId"))
        }
//        val commentInfo = { comment: CommentMessage -> "(${comment.author},${comment.timeStamp})" }
//        val commentsInfo = GlobalScope.async {
//            csvSerialize(comments.map { it.idFromTopicMessage }
//                .zip(comments.map { snapshot -> snapshot.listOfComments.joinToString { commentInfo(it) } }),
//                Pair::class, rename = mapOf("first" to "topic id", "second" to "author,timeStamp")
//            )
//        }

        val commentsInfo = GlobalScope.async {
            csvSerialize(
                comments.flatMap { it.listOfComments },
                CommentMessage::class,
                hashSetOf("uniqueId", "author", "depth", "idFromTopicMessage")
            )
        }

        val cwd = localVfs(path)
        cwd[title].mkdir()
        writeDataToFile("${path}${title}\\", topicFileName, topicsInfo.await().toByteArray())
        writeDataToFile("${path}${title}\\", commentsFileName, commentsInfo.await().toByteArray())
    }

    suspend fun getComments(url: String, idFromTopicMessage: String = ""): CommentsSnapshot {
        val link = "${url}/.json"
        val resJSONString = getJSONAsString(link)

        val treeJSON = mapper.readTree(resJSONString)
        val listOfComments = treeJSON.last().path("data").path("children")
        val list = arrayListOf<CommentMessage>()
        val listOfNodes = listOfComments.map { analyzeReplies(it, list) }
        list.forEach { it.idFromTopicMessage = idFromTopicMessage }

        return CommentsSnapshot(
            Time.getCurrentTime(), list, idFromTopicMessage, CommentsTreeNode(null, listOfNodes)
        )
    }
}
