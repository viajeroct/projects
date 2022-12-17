package homework03

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AboutCommunity(
    @JsonAlias("accounts_active") val online: Long,
    @JsonAlias("created") val timeStamp: Time,
    val public_description: String,
    val subscribers: Long
)
