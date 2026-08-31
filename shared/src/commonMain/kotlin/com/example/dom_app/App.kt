package com.example.dom_app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class AppState {
    Login,
    Main
}

enum class SocialScreen(val title: String, val icon: ImageVector) {
    Feed("Feed", Icons.Default.Home),
    Search("Search", Icons.Default.Search),
    Notifications("Notifications", Icons.Default.Notifications),
    Profile("Profile", Icons.Default.Person)
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var appState by remember { mutableStateOf(AppState.Login) }
        var currentUser by remember { mutableStateOf<User?>(null) }
        var currentScreen by remember { mutableStateOf(SocialScreen.Feed) }
        val posts = remember { mutableStateListOf<Post>().apply { addAll(mockPosts) } }
        var showCreatePostDialog by remember { mutableStateOf(false) }

        if (appState == AppState.Login) {
            LoginScreen(onLoginSuccess = { user ->
                currentUser = user
                appState = AppState.Main
            })
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        SocialScreen.entries.forEach { screen ->
                            NavigationBarItem(
                                selected = currentScreen == screen,
                                onClick = { currentScreen = screen },
                                label = { Text(screen.title) },
                                icon = { Icon(screen.icon, contentDescription = screen.title) },
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (currentScreen == SocialScreen.Feed) {
                        FloatingActionButton(onClick = { showCreatePostDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Create Post")
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    when (currentScreen) {
                        SocialScreen.Feed -> FeedScreen(posts)
                        SocialScreen.Search -> SearchScreen()
                        SocialScreen.Notifications -> NotificationsScreen()
                        SocialScreen.Profile -> ProfileScreen(currentUser, onLogout = {
                            appState = AppState.Login
                            currentUser = null
                        })
                    }
                }
            }

            if (showCreatePostDialog) {
                CreatePostDialog(
                    onDismiss = { showCreatePostDialog = false },
                    onPost = { content ->
                        val newPost = Post(
                            id = (posts.maxOfOrNull { it.id } ?: 0) + 1,
                            username = currentUser?.username ?: "unknown",
                            content = content,
                            timestamp = "Just now",
                            likes = 0
                        )
                        posts.add(0, newPost)
                        showCreatePostDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (User) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = "Welcome to DOM",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val user = premadeUsers.find { it.username == username && it.password == password }
                if (user != null) {
                    onLoginSuccess(user)
                } else {
                    errorMessage = "Invalid username or password"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Try 'alice' / '1234' or 'bob' / 'password'",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CreatePostDialog(onDismiss: () -> Unit, onPost: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Post") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onPost(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FeedScreen(posts: List<Post>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        items(posts) { post ->
            PostItem(post)
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = post.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = post.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Likes",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = post.likes.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp))
        Text("Search", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun NotificationsScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(64.dp))
        Text("Notifications", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun ProfileScreen(user: User?, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = user?.username ?: "Guest", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}
