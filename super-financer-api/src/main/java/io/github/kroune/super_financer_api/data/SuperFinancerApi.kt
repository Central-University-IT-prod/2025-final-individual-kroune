package io.github.kroune.super_financer_api.data

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol

class SuperFinancerApi : SuperFinancerApiI {
    override val url
        get() = URLBuilder(
            protocol = URLProtocol.HTTPS,
            "nine-men-s-morris.me",
            pathSegments = listOf("api")
        )
}

interface SuperFinancerApiI {
    val url: URLBuilder
}
