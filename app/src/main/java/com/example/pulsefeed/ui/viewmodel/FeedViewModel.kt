package com.example.pulsefeed.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefeed.data.model.PostWithUser
import com.example.pulsefeed.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()
    
    private var currentPage = 1
    
    init {
        loadFeed()
    }
    
    fun loadFeed(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _uiState.value = _uiState.value.copy(isRefreshing = true)
                currentPage = 1
            } else if (_uiState.value.isLoading) {
                return@launch
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }
            
            postRepository.getFeed(page = currentPage, limit = 20)
                .onSuccess { feedResponse ->
                    val newPosts = if (refresh) {
                        feedResponse.posts
                    } else {
                        _uiState.value.posts + feedResponse.posts
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        posts = newPosts,
                        hasMore = feedResponse.hasMore,
                        error = null
                    )
                    
                    if (feedResponse.hasMore) {
                        currentPage++
                    }
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = error.message
                    )
                }
        }
    }
    
    fun likePost(postId: Int) {
        viewModelScope.launch {
            // Optimistically update UI
            updatePostLikeState(postId, true)
            
            postRepository.likePost(postId)
                .onFailure { error ->
                    // Revert on failure
                    updatePostLikeState(postId, false)
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }
    
    fun unlikePost(postId: Int) {
        viewModelScope.launch {
            // Optimistically update UI
            updatePostLikeState(postId, false)
            
            postRepository.unlikePost(postId)
                .onFailure { error ->
                    // Revert on failure
                    updatePostLikeState(postId, true)
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }
    
    private fun updatePostLikeState(postId: Int, isLiked: Boolean) {
        val updatedPosts = _uiState.value.posts.map { postWithUser ->
            if (postWithUser.post.id == postId) {
                postWithUser.copy(
                    post = postWithUser.post.copy(
                        isLiked = isLiked,
                        likesCount = if (isLiked) {
                            postWithUser.post.likesCount + 1
                        } else {
                            maxOf(0, postWithUser.post.likesCount - 1)
                        }
                    )
                )
            } else {
                postWithUser
            }
        }
        _uiState.value = _uiState.value.copy(posts = updatedPosts)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class FeedUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val posts: List<PostWithUser> = emptyList(),
    val hasMore: Boolean = false,
    val error: String? = null
)
