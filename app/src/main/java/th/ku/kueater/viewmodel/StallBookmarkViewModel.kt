package th.ku.kueater.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import th.ku.kueater.model.StallItem
import th.ku.kueater.network.RetrofitInstance

class StallBookmarkViewModel : ViewModel() {

    private val _savedStalls = MutableStateFlow<List<StallItem>>(emptyList())
    val savedStalls: StateFlow<List<StallItem>> = _savedStalls

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val TAG = "StallBookmarkViewModel"

    fun fetchSavedStalls(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val bookmarkResponse = RetrofitInstance.stallBookmarkApi.getStallBookmarksByUser(userId = userId)

                Log.d(TAG, "Fetched ${bookmarkResponse.bookmarks.size} stall bookmarks for user $userId")

                _savedStalls.value = bookmarkResponse.bookmarks.map { bookmark ->
                    val details = bookmark.stallDetails

                    Log.d(
                        TAG, "Bookmark: stallId=${bookmark.stallId}, " +
                                "name=${details.nameEN}, location=${details.location}"
                    )

                    details.copy(isBookmarked = true)
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e(TAG, "Failed to fetch saved stalls: ${e.message}", e)
                _savedStalls.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleBookmarkState(stallId: String) {
        _savedStalls.value = _savedStalls.value.map { stall ->
            if (stall.location == stallId) {
                stall.copy(isBookmarked = !stall.isBookmarked)
            } else stall
        }
    }
}
