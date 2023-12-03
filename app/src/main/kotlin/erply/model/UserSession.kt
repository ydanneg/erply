package erply.model

data class UserSession(
    val userId: String,
    val username: String,
    val token: String,
    val clientCode: String
)

fun UserSession.isLoggedIn() = token.isNotBlank()