package th.ku.kueater.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import th.ku.kueater.model.MenuItem
import th.ku.kueater.model.PostMenuBookmarkPayload
import th.ku.kueater.model.PostMenuFeedbackPayload
import th.ku.kueater.network.RetrofitInstance

class MenuViewModel : ViewModel() {
    // State for paginated menu items (existing)
    private val _menus = MutableStateFlow<List<MenuItem>>(emptyList())
    val menus: StateFlow<List<MenuItem>> = _menus

    // State for top 10 menu items (new)
    private val _top10Menus = MutableStateFlow<List<MenuItem>>(emptyList())
    val top10Menus: StateFlow<List<MenuItem>> = _top10Menus

    // State for random menu item (new)
    private val _randomMenu = MutableStateFlow<MenuItem?>(null)
    val randomMenu: StateFlow<MenuItem?> = _randomMenu

    // Loading state for paginated menus (existing)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Loading state for top 10 menus (new)
    private val _isLoadingTop10 = MutableStateFlow(false)
    val isLoadingTop10: StateFlow<Boolean> = _isLoadingTop10

    // Error state for all operations (new)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentPage = 1
    private val perPage = 10
    private var isLastPage = false
    private var userId: String? = null

    fun setUserId(uid: String) {
        userId = uid
        refreshMenus()
        fetchTop10Menus() // Fetch top 10 menus when userId is set
    }

    private fun refreshMenus() {
        _menus.value = emptyList()
        currentPage = 1
        isLastPage = false
        loadNextPage()
    }

    fun loadNextPage() {
        if (_isLoading.value || isLastPage || userId == null) return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.menuApi.getMenus(
                    page = currentPage,
                    perPage = perPage,
                    userId = userId!!
                )

                val newData = response.data

                if (newData.size < perPage) {
                    isLastPage = true
                }

                _menus.value += newData
                currentPage++
            } catch (e: Exception) {
                _error.value = e.message
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTop10Menus() {
        if (_isLoadingTop10.value || userId == null) return

        _isLoadingTop10.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.menuApi.getTop10Menus(
                    userId = userId!!
                )

                _top10Menus.value = response.data
            } catch (e: Exception) {
                _error.value = e.message
                e.printStackTrace()
            } finally {
                _isLoadingTop10.value = false
            }
        }
    }

    /**
     * Calculates the updated state for a menu list when a user feedback occurs.
     *
     * This helper method creates a new list with updated like/dislike counts and userFeedback.
     */
    private fun updateMenuListFeedback(
        originalList: List<MenuItem>,
        menuId: String,
        feedbackType: String
    ): List<MenuItem> {
        return originalList.map { menuItem ->
            if (menuItem.id == menuId) {
                val newLikeCount = when {
                    feedbackType == "like" && menuItem.userFeedback != "like" -> menuItem.like + 1
                    feedbackType != "like" && menuItem.userFeedback == "like" -> menuItem.like - 1
                    feedbackType == "like" && menuItem.userFeedback == "like" -> menuItem.like - 1
                    else -> menuItem.like
                }

                val newDislikeCount = when {
                    feedbackType == "dislike" && menuItem.userFeedback != "dislike" -> menuItem.dislike + 1
                    feedbackType != "dislike" && menuItem.userFeedback == "dislike" -> menuItem.dislike - 1
                    feedbackType == "dislike" && menuItem.userFeedback == "dislike" -> menuItem.dislike - 1
                    else -> menuItem.dislike
                }

                val newUserFeedback = when {
                    menuItem.userFeedback == "like" && feedbackType == "like" -> null
                    menuItem.userFeedback == "like" && feedbackType != "like" -> feedbackType
                    menuItem.userFeedback == "dislike" && feedbackType == "dislike" -> null
                    menuItem.userFeedback == "dislike" && feedbackType != "dislike" -> feedbackType
                    else -> feedbackType
                }

                menuItem.copy(
                    like = newLikeCount,
                    dislike = newDislikeCount,
                    userFeedback = newUserFeedback
                )
            } else {
                menuItem
            }
        }
    }

    /**
     * Optimistic update for like/dislike feedback.
     *
     * The method applies the update immediately to the state and then attempts the API call.
     * If the API call fails, it rolls back to the previous state.
     */
    fun postMenuFeedback(
        menuId: String,
        feedbackType: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        userId: String
    ) {
        // Save the current state for rollback.
        val previousMenus = _menus.value
        val previousTop10Menus = _top10Menus.value

        // Apply optimistic update to both lists.
        _menus.value = updateMenuListFeedback(_menus.value, menuId, feedbackType)
        _top10Menus.value = updateMenuListFeedback(_top10Menus.value, menuId, feedbackType)

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.menuFeedbackApi.postMenuFeedback(
                    PostMenuFeedbackPayload(
                        userId = userId,
                        menuId = menuId,
                        feedbackType = feedbackType
                    )
                )
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    // Rollback the optimistic update if the response is not successful.
                    _menus.value = previousMenus
                    _top10Menus.value = previousTop10Menus
                    onError("Failed to update feedback")
                }
            } catch (e: Exception) {
                // On exception, rollback to previous states.
                _menus.value = previousMenus
                _top10Menus.value = previousTop10Menus
                onError("Error: ${e.message}")
            }
        }
    }

    /**
     * Optimistic update for bookmark toggling.
     *
     * This function immediately updates the bookmarked state, then attempts the API call.
     * If the API call fails, the original states are restored.
     */
    fun postMenuBookmark(
        menuId: String,
        isCurrentlyBookmarked: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Save the current state for rollback.
        val previousMenus = _menus.value
        val previousTop10Menus = _top10Menus.value

        // Optimistically update the state.
        _menus.value = _menus.value.map {
            if (it.id == menuId) it.copy(isBookmarked = !isCurrentlyBookmarked) else it
        }
        _top10Menus.value = _top10Menus.value.map {
            if (it.id == menuId) it.copy(isBookmarked = !isCurrentlyBookmarked) else it
        }

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.menuBookmarkApi.toggleMenuBookmark(
                    PostMenuBookmarkPayload(userId = userId ?: return@launch, menuId = menuId)
                )
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    // Rollback the optimistic update if unsuccessful.
                    _menus.value = previousMenus
                    _top10Menus.value = previousTop10Menus
                    onError("Failed to toggle bookmark")
                }
            } catch (e: Exception) {
                // On exception, rollback to previous states.
                _menus.value = previousMenus
                _top10Menus.value = previousTop10Menus
                onError("Error: ${e.message}")
            }
        }
    }

    /**
     * Retrieves a random menu item filtered by the provided foodType.
     *
     * This function makes a network request through Retrofit, updates the _randomMenu state,
     * and optionally calls onSuccess/onError callbacks.
     */
    fun getRandomMenu(
        foodType: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.menuApi.getRandomMenu(foodType = foodType, userId = userId!!)
                _randomMenu.value = response.data
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
                onError("Error fetching random menu: ${e.message}")
            }
        }
    }
}
