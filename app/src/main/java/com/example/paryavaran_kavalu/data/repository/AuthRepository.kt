package com.example.paryavaran_kavalu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun getUserRole(uid: String): String? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.getString("role")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserData(uid: String): Map<String, Any>? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.data
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createUserIfNotExists(user: FirebaseUser) {
        val docRef = firestore.collection("users").document(user.uid)
        val snapshot = docRef.get().await()
        if (!snapshot.exists()) {
            val userData = mapOf(
                "uid" to user.uid,
                "name" to (user.displayName ?: ""),
                "email" to (user.email ?: ""),
                "role" to "",
                "points" to 0,
                "medal" to "Eco Beginner"
            )
            docRef.set(userData).await()
        }
    }

    suspend fun saveUserRole(uid: String, name: String, email: String, role: String) {
        val docRef = firestore.collection("users").document(uid)
        val snapshot = docRef.get().await()
        
        if (snapshot.exists()) {
            docRef.update(
                "name", name,
                "email", email,
                "role", role
            ).await()
        } else {
            val user = mapOf(
                "uid" to uid,
                "name" to name,
                "email" to email,
                "role" to role,
                "points" to 0,
                "medal" to "Eco Beginner"
            )
            docRef.set(user).await()
        }
    }

    suspend fun addPoints(uid: String, points: Int) {
        firestore.collection("users").document(uid).update(
            "points", FieldValue.increment(points.toLong())
        ).await()
    }

    suspend fun updateMedal(uid: String, medal: String) {
        firestore.collection("users").document(uid).update(
            "medal", medal
        ).await()
    }

    suspend fun updateKarma(uid: String, points: Int, medal: String) {
        firestore.collection("users").document(uid).update(
            "points", points,
            "medal", medal
        ).await()
    }

    suspend fun updateUserData(uid: String, updates: Map<String, Any>) {
        firestore.collection("users").document(uid).update(updates).await()
    }

    suspend fun registerWithEmail(name: String, email: String, password: String, role: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("Registration failed")
        
        saveUserRole(user.uid, name, email, role)
        return user.uid
    }

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    fun signOut() {
        auth.signOut()
    }
}