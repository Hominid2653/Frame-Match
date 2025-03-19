package com.app.fm001.ui.screens.photographer.dashboard

import androidx.lifecycle.ViewModel
import com.app.fm001.model.Comment
import com.app.fm001.model.PhotoPost
import com.app.fm001.model.PhotographerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class PortfolioViewModel : ViewModel() {
    private val _photographerProfile = MutableStateFlow(
        PhotographerProfile(
            id = "1",
            name = "John Doe",
            bio = "Professional photographer specializing in weddings and events",
            profileImage = "https://i.pravatar.cc/150",
            rating = 5.0f,
            reviewCount = 124,
            verified = true,
            specialties = listOf("Wedding", "Event", "Portrait")
        )
    )
    val photographerProfile = _photographerProfile.asStateFlow()

    private val _posts = MutableStateFlow<List<PhotoPost>>(emptyList())
    val posts = _posts.asStateFlow()

    init {
        _posts.value = getDummyPosts()
    }

    private fun getDummyPosts(): List<PhotoPost> {
        return List(15) { index ->
            PhotoPost(
                id = index.toString(),
                photographerId = "1",
                images = listOf("https://picsum.photos/seed/$index/500"),
                description = "Sample photo $index",
                hashtags = listOf("Photography", "Portfolio"),
                timestamp = System.currentTimeMillis() - (index * 86400000),
                likes = (10..100).random(),
                comments = List((1..5).random()) { commentIndex ->
                    Comment(
                        id = "$index-$commentIndex",
                        userId = "user$commentIndex",
                        username = "User $commentIndex",
                        content = "Great photo! Love the composition.",
                        timestamp = System.currentTimeMillis() - ((1..10).random() * 3600000)
                    )
                }
            )
        }
    }

    fun toggleLike(postId: String) {
        val currentPosts = _posts.value.toMutableList()
        val index = currentPosts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = currentPosts[index]
            currentPosts[index] = post.copy(
                isLiked = !post.isLiked,
                likes = if (post.isLiked) post.likes - 1 else post.likes + 1
            )
            _posts.value = currentPosts
        }
    }

    fun addComment(postId: String, content: String) {
        val currentPosts = _posts.value.toMutableList()
        val index = currentPosts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = currentPosts[index]
            val newComment = Comment(
                id = UUID.randomUUID().toString(),
                userId = "current_user", // TODO: Get from auth
                username = "Current User", // TODO: Get from auth
                content = content,
                timestamp = System.currentTimeMillis()
            )
            currentPosts[index] = post.copy(
                comments = post.comments + newComment
            )
            _posts.value = currentPosts
        }
    }
}
