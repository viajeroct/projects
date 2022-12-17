package homework03

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CommentMessage(
    @JsonAlias("name") val uniqueId: String,
    @JsonAlias("parent_id") val replyTo: String,
    val depth: Int,
    val author: String,
    val score: Long,
    val body: String,
    val ups: Long,
    val downs: Long,
    @JsonAlias("created") val timeStamp: Time,
    var idFromTopicMessage: String? = ""
)
