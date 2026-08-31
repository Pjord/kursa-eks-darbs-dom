package com.example.dom_app

data class Post(
    val id: Int,
    val username: String,
    val content: String,
    val timestamp: String,
    val likes: Int
)

val mockPosts = listOf(
    Post(1, "alice_j", "Just started building my social app with Compose Multiplatform! #AndroidDev", "2h ago", 12),
    Post(2, "bob_smith", "Anyone else excited for the new Kotlin features?", "4h ago", 8),
    Post(3, "charlie_k", "Beautiful day for a walk. ☀️", "5h ago", 15),
    Post(4, "diana_v", "Just finished a great workout. Feeling productive!", "8h ago", 20),
    Post(5, "ethan_w", "Exploring new coffee shops in the city. Any recommendations?", "10h ago", 5),
    Post(6, "fiona_m", "The mountains are calling. 🏔️", "12h ago", 25),
    Post(7, "george_h", "Reading a fascinating book on software architecture.", "1d ago", 10),
    Post(8, "hannah_l", "Baked some fresh cookies today! 🍪", "1d ago", 30),
    Post(9, "ian_r", "Debugging can be tough, but so rewarding.", "2d ago", 7),
    Post(10, "jenny_c", "Happy weekend everyone!", "2d ago", 18)
)
