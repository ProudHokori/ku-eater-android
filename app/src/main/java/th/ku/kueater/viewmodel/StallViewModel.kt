package th.ku.kueater.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import th.ku.kueater.model.StallItem
import th.ku.kueater.network.RetrofitInstance
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class StallViewModel : ViewModel() {

    private val _stalls = MutableStateFlow<List<StallItem>>(emptyList())
    val stalls: StateFlow<List<StallItem>> = _stalls

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var userId: String? = null

    fun setUserId(uid: String) {
        userId = uid
        fetchStalls()
    }

    private fun fetchStalls() {
        if (_isLoading.value || userId == null) return

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.stallApi.getAllStalls(userId = userId!!)

                val formatted = response.data.map { stall ->
                    stall.copy(
                        openTimeFormatted = formatTime(stall.openTimeFormatted),
                        closeTimeFormatted = formatTime(stall.closeTimeFormatted)
                    )
                }

                _stalls.value = formatted
            } catch (e: Exception) {
                e.printStackTrace()
                _stalls.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleBookmarkState(stallId: String, userId: String) {
        val currentUserId = userId

        viewModelScope.launch {
            val previousState = _stalls.value

            // Optimistically update UI
            _stalls.value = _stalls.value.map {
                if (it.location == stallId) it.copy(isBookmarked = !it.isBookmarked) else it
            }

            try {
                val response = RetrofitInstance.stallBookmarkApi.toggleStallBookmark(
                    th.ku.kueater.model.PostStallBookmarkPayload(
                        userId = currentUserId,
                        stallId = stallId
                    )
                )

                if (!response.isSuccessful) {
                    // Revert if failed
                    _stalls.value = previousState
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Revert on error
                _stalls.value = previousState
            }
        }
    }

    private fun formatTime(raw: String?): String {
        return try {
            if (raw.isNullOrBlank()) return ""
            val time = LocalTime.parse(raw.substringAfter("T").substringBefore("."))
            time.format(DateTimeFormatter.ofPattern("HH.mm"))
        } catch (e: Exception) {
            Log.w("StallViewModel", "Invalid time: $raw", e)
            ""
        }
    }
}
