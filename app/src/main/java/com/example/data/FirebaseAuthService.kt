package com.example.data

import android.content.Context
import android.util.Log
import com.example.model.StudentProfileDocument
import com.example.model.UserDocument
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(private val context: Context) {

    private val tag = "FirebaseAuthService"

    init {
        ensureFirebaseInitialized(context)
    }

    private fun ensureFirebaseInitialized(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("com.aistudio.samjhoai.edufdn")
                    .setApiKey("AIzaSyFakeDevPlaceholderForLocalBuild")
                    .setProjectId("samjho-ai-phase2")
                    .build()
                FirebaseApp.initializeApp(context, options)
                Log.d(tag, "FirebaseApp initialized with fallback development configuration.")
            }
        } catch (e: Exception) {
            Log.w(tag, "FirebaseApp initialization check: ${e.message}")
        }
    }

    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(tag, "FirebaseAuth unavailable: ${e.message}")
            null
        }

    private val firestore: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(tag, "FirebaseFirestore unavailable: ${e.message}")
            null
        }

    val currentFirebaseUser: FirebaseUser?
        get() = auth?.currentUser

    val isUserLoggedIn: Boolean
        get() = auth?.currentUser != null

    suspend fun signUpWithEmail(name: String, email: String, pass: String): Result<UserDocument> {
        val fbAuth = auth
        if (fbAuth == null) {
            // Local fallback simulation when Firebase cloud credentials are not linked
            val fallbackUid = "user_${System.currentTimeMillis()}"
            val userDoc = UserDocument(
                userId = fallbackUid,
                name = name,
                email = email,
                photoUrl = null,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            return Result.success(userDoc)
        }

        return try {
            val authResult = fbAuth.createUserWithEmailAndPassword(email, pass).await()
            val fbUser = authResult.user ?: throw IllegalStateException("Firebase user creation failed")
            val userDoc = UserDocument(
                userId = fbUser.uid,
                name = name.ifBlank { fbUser.displayName ?: "Student" },
                email = fbUser.email ?: email,
                photoUrl = fbUser.photoUrl?.toString(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            // Persist to Firestore users collection
            saveUserDocument(userDoc)
            Result.success(userDoc)
        } catch (e: Exception) {
            Log.e(tag, "Sign up failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<UserDocument> {
        val fbAuth = auth
        if (fbAuth == null) {
            val fallbackUid = "user_${email.hashCode().toUInt()}"
            val userDoc = UserDocument(
                userId = fallbackUid,
                name = email.substringBefore("@"),
                email = email,
                photoUrl = null
            )
            return Result.success(userDoc)
        }

        return try {
            val authResult = fbAuth.signInWithEmailAndPassword(email, pass).await()
            val fbUser = authResult.user ?: throw IllegalStateException("Sign in failed")
            val existingDoc = getUserDocument(fbUser.uid)
            val userDoc = existingDoc ?: UserDocument(
                userId = fbUser.uid,
                name = fbUser.displayName ?: email.substringBefore("@"),
                email = fbUser.email ?: email,
                photoUrl = fbUser.photoUrl?.toString()
            )
            Result.success(userDoc)
        } catch (e: Exception) {
            Log.e(tag, "Sign in failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun saveUserDocument(user: UserDocument): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return try {
            db.collection(FirebaseSchema.COLLECTION_USERS)
                .document(user.userId)
                .set(user.toMap(), SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(tag, "Failed to save user document: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getUserDocument(userId: String): UserDocument? {
        val db = firestore ?: return null
        return try {
            val snapshot = db.collection(FirebaseSchema.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            if (snapshot.exists()) {
                UserDocument(
                    userId = snapshot.getString("userId") ?: userId,
                    name = snapshot.getString("name") ?: "",
                    email = snapshot.getString("email") ?: "",
                    photoUrl = snapshot.getString("photoUrl"),
                    createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis(),
                    updatedAt = snapshot.getLong("updatedAt") ?: System.currentTimeMillis()
                )
            } else null
        } catch (e: Exception) {
            Log.w(tag, "Failed to get user doc: ${e.message}")
            null
        }
    }

    suspend fun saveStudentProfile(profile: StudentProfileDocument): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return try {
            db.collection("studentProfiles")
                .document(profile.userId)
                .set(profile.toMap(), SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(tag, "Failed to save student profile: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getStudentProfile(userId: String): StudentProfileDocument? {
        val db = firestore ?: return null
        return try {
            val snapshot = db.collection("studentProfiles")
                .document(userId)
                .get()
                .await()
            if (snapshot.exists()) {
                @Suppress("UNCHECKED_CAST")
                StudentProfileDocument(
                    userId = snapshot.getString("userId") ?: userId,
                    educationLevel = snapshot.getString("educationLevel") ?: "School",
                    classOrCourse = snapshot.getString("classOrCourse") ?: "Class 10",
                    board = snapshot.getString("board"),
                    university = snapshot.getString("university"),
                    exam = snapshot.getString("exam"),
                    subjects = (snapshot.get("subjects") as? List<String>) ?: emptyList(),
                    preferredLanguage = snapshot.getString("preferredLanguage") ?: "Hinglish",
                    learningPreferences = (snapshot.get("learningPreferences") as? List<String>) ?: emptyList(),
                    difficultyAreas = (snapshot.get("difficultyAreas") as? List<String>) ?: emptyList(),
                    createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis(),
                    updatedAt = snapshot.getLong("updatedAt") ?: System.currentTimeMillis()
                )
            } else null
        } catch (e: Exception) {
            Log.w(tag, "Failed to load student profile: ${e.message}")
            null
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w(tag, "Sign out error: ${e.message}")
        }
    }
}
