package homework03

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls

@JsonIgnoreProperties(ignoreUnknown = true)
data class TopicMessage(
    @JsonAlias("author") val authorName: String,
    val title: String,
    val score: Long,
    @JsonAlias("selftext") val messageText: String,
    @JsonSetter("selftext_html", nulls = Nulls.AS_EMPTY) val messageTextHtml: String,
    @JsonAlias("created") val timeStamp: Time,
    val permalink: String,
    val ups: Long,
    val downs: Long,
    @JsonAlias("id") val uniqueTopicId: String
)
