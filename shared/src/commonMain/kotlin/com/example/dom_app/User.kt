package com.example.dom_app

data class User(
    val username: String,
    val password: String // In a real app, never store passwords like this!
)

val premadeUsers = listOf(
    User("alice", "1234"),
    User("bob", "password")
)
