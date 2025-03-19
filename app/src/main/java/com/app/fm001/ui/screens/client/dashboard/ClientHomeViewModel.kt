package com.app.fm001.ui.screens.client.dashboard

import androidx.lifecycle.ViewModel
import com.app.fm001.model.PhotoPost
import com.app.fm001.model.PhotographerProfile
import com.app.fm001.utils.random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class ClientHomeViewModel : ViewModel() {
    private val _feedPosts = MutableStateFlow<List<FeedPost>>(getDummyFeed())
    val feedPosts = _feedPosts.asStateFlow()

    private fun getDummyFeed(): List<FeedPost> {
        val photographers = List(5) { index ->
            PhotographerProfile(
                id = "photographer_$index",
                name = "Photographer ${index + 1}",
                bio = "Professional photographer specializing in various styles",
                profileImage = "https://i.pravatar.cc/150?img=$index",
                rating = (3.5f..5.0f).random(),
                reviewCount = Random.nextInt(10, 201),
                verified = index % 2 == 0
            )
        }

        return List(20) { index ->
            FeedPost(
                post = PhotoPost(
                    id = "post_$index",
                    photographerId = photographers[index % photographers.size].id,
                    images = listOf("https://picsum.photos/seed/${index + 100}/500"),
                    description = "Amazing photo shoot #${index + 1}",
                    hashtags = listOf("Photography", "Portfolio", "Professional"),
                    timestamp = System.currentTimeMillis() - (index * 3600000),
                    likes = Random.nextInt(50, 501)
                ),
                photographer = photographers[index % photographers.size]
            )
        }
    }

    fun likePost(postId: String) {
        val currentPosts = _feedPosts.value.toMutableList()
        val index = currentPosts.indexOfFirst { it.post.id == postId }
        if (index != -1) {
            val feedPost = currentPosts[index]
            currentPosts[index] = feedPost.copy(
                post = feedPost.post.copy(
                    isLiked = !feedPost.post.isLiked,
                    likes = if (feedPost.post.isLiked) 
                        feedPost.post.likes - 1 
                    else 
                        feedPost.post.likes + 1
                )
            )
            _feedPosts.value = currentPosts
        }
    }
}

data class FeedPost(
    val post: PhotoPost,
    val photographer: PhotographerProfile
) 