package com.example.dom_app.db

import com.example.dom_app.Post

class PostDatabase(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQueries = database.appDatabaseQueries

    fun getAllPosts(): List<PostEntity> {
        return dbQueries.selectAllPosts().executeAsList()
    }

    fun insertPost(post: PostEntity) {
        dbQueries.insertPost(
            id = post.id,
            username = post.username,
            content = post.content,
            timestamp = post.timestamp,
            likes = post.likes
        )
    }

    fun deleteAllPosts() {
        dbQueries.deleteAllPosts()
    }
}
