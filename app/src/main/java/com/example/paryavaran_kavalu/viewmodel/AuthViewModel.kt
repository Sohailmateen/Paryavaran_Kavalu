package com.example.paryavaran_kavalu.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paryavaran_kavalu.data.repository.AuthRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            viewModelScope.launch {
                _authState.value = AuthState.Loading
                val role = repository.getUserRole(user.uid)
                _authState.value = AuthState.Success(user.uid, role)
            }
        }
    }

    fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
                val user = authResult.user
                if (user != null) {
                    repository.createUserIfNotExists(user)
                    val role = repository.getUserRole(user.uid)
                    _authState.value = AuthState.Success(user.uid, role)
                } else {
                    _authState.value = AuthState.Error("Sign in failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun completeRoleSelection(role: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            viewModelScope.launch {
                _authState.value = AuthState.Loading
                try {
                    repository.saveUserRole(
                        uid = user.uid,
                        name = user.displayName ?: "",
                        email = user.email ?: "",
                        role = role
                    )
                    _authState.value = AuthState.Success(user.uid, role)
                } catch (e: Exception) {
                    _authState.value = AuthState.Error(e.message ?: "Failed to save role")
                }
            }
        }
    }

    fun register(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val uid = repository.registerWithEmail(name, email, password, role)
                _authState.value = AuthState.Success(uid, role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = repository.signInWithEmail(email, password)
                if (user != null) {
                    repository.createUserIfNotExists(user)
                    val role = repository.getUserRole(user.uid)
                    _authState.value = AuthState.Success(user.uid, role)
                } else {
                    _authState.value = AuthState.Error("Sign in failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun updateProfile(name: String, password: String, imageUri: Uri?) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Update Name in Firestore
                val updates = mutableMapOf<String, Any>("name" to name)
                repository.updateUserData(user.uid, updates)

                // Update Firebase Auth Profile
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                user.updateProfile(profileUpdates).await()

                // Update Password if provided
                if (password.isNotEmpty()) {
                    user.updatePassword(password).await()
                }
                
                val role = repository.getUserRole(user.uid)
                _authState.value = AuthState.Success(user.uid, role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Update failed")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val uid: String, val role: String?) : AuthState()
    data class Error(val message: String) : AuthState()
}