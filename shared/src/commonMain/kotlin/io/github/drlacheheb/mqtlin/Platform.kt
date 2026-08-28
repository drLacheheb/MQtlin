package io.github.drlacheheb.mqtlin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform