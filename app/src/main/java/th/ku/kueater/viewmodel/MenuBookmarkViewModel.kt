package th.ku.kueater.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import th.ku.kueater.model.MenuItem
import th.ku.kueater.network.RetrofitInstance

class BookmarkViewModel : ViewModel() {

    private val _savedMenus = MutableStateFlow<List<MenuItem>>(emptyList())
    val savedMenus: StateFlow<List<MenuItem>> = _savedMenus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val TAG = "BookmarkViewModel"

    fun fetchSavedMenus(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val bookmarkResponse = RetrofitInstance.menuBookmarkApi.getMenuBookmarksByUser(userId = userId)

                Log.d(TAG, "Fetched ${bookmarkResponse.bookmarks.size} bookmarks for user $userId")

                _savedMenus.value = bookmarkResponse.bookmarks.map { bookmark ->
                    val details = bookmark.menuDetails

                    Log.d(
                        TAG, "Bookmark: menuId=${bookmark.menuId}, " +
                                "name=${details.menuName}, price=${details.price}, " +
                                "stall=${details.stallName}, location=${details.stallLocation}"
                    )

                    MenuItem(
                        id = details.id,
                        menuName = details.menuName,
                        price = details.price,
                        stallName = details.stallName,
                        stallLocation = details.stallLocation,
                        imageUrl = details.imageUrl,
                        isBookmarked = details.isBookmarked,
                        like = details.like,
                        dislike = details.dislike,
                        userFeedback = details.userFeedback
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e(TAG, "Failed to fetch saved menus: ${e.message}", e)
                _savedMenus.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleBookmarkState(menuId: String) {
        _savedMenus.value = _savedMenus.value.map { item ->
            if (item.id == menuId) {
                item.copy(isBookmarked = !item.isBookmarked)
            } else item
        }
    }

    fun updateFeedbackState(menuId: String, newLikeCount: Int, newDislikeCount: Int, newUserFeedback: String?) {
        _savedMenus.value = _savedMenus.value.map { item ->
            if (item.id == menuId) {
                item.copy(
                    like = newLikeCount,
                    dislike = newDislikeCount,
                    userFeedback = newUserFeedback
                )
            } else {
                item
            }
        }
    }
}