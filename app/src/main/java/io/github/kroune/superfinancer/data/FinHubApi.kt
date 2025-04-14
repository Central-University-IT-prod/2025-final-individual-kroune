package io.github.kroune.superfinancer.data

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol

class FinHubApi : FinHubApiI {
    override val token: String = "cumjc39r01qsaphvt690cumjc39r01qsaphvt69g"
    override val url
        get() = URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = "finnhub.io",
            pathSegments = listOf("api", "v1")
        )
}

interface FinHubApiI {
    val token: String
    val url: URLBuilder
}
