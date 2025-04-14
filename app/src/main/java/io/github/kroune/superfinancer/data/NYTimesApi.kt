package io.github.kroune.superfinancer.data

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol

class NYTimesApi : NYTimesApiI {
    override val token: String = "G1VlI6Umapn12hyDWR3lau3YpGMlAker"
    override val url
        get() = URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = "api.nytimes.com",
            pathSegments = listOf("svc")
        )
}

interface NYTimesApiI {
    val token: String
    val url: URLBuilder
}
