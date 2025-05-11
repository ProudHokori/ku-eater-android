package th.ku.kueater.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        _isLoggedIn.value = auth.currentUser != null
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _error.value = null
        _isLoading.value = true

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    _error.value = task.exception?.localizedMessage ?: "Login failed"
                }
            }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        _error.value = null
        _isLoading.value = true

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    _error.value = task.exception?.localizedMessage ?: "Registration failed"
                }
            }
    }

    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
    }

    /**
     * ✅ Returns the current Firebase user ID (UID) or null if not signed in.
     */
    fun getUserId(): String? {
        return auth.currentUser?.uid
    }
}
