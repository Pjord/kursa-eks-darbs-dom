package com.example.dom_app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform