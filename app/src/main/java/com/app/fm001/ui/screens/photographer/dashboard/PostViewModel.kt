package com.app.fm001.ui.screens.photographer.dashboard
import androidx.lifecycle.ViewModel
import com.app.fm001.model.PhotoPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isPosting = MutableStateFlow(false)
    val isPosting = _isPosting.asStateFlow()

    private val _posts = MutableStateFlow<List<PhotoPost>>(emptyList())
    val posts = _posts.asStateFlow()

    init {
        fetchPosts()
    }

    fun createPost(images: List<String>, description: String, hashtags: List<String>) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            return
        }

        val newPost = PhotoPost(
            id = System.currentTimeMillis().toString(),
            photographerId = currentUser.uid,  // Assign logged-in user ID
            images = images,
            description = description,
            hashtags = hashtags
        )

        _isPosting.value = true

        db.collection("posts").document(newPost.id)
            .set(newPost)
            .addOnSuccessListener {
                _posts.value = _posts.value + newPost
            }
            .addOnFailureListener {
                // Handle failure
            }
            .addOnCompleteListener {
                _isPosting.value = false
            }
    }

    private fun fetchPosts() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            return
        }

        db.collection("posts")
            .whereEqualTo("photographerId", currentUser.uid) // Fetch posts for the logged-in user
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    _posts.value = snapshot.documents.mapNotNull { it.toObject(PhotoPost::class.java) }
                }
            }
    }
}
