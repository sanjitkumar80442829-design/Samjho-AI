package com.example.data

/**
 * Firebase Architecture & Firestore Document Contracts for Samjho AI
 * Designed for scalable Firebase / FlutterFlow backend compatibility.
 */

object FirebaseSchema {
    // Firestore Collection Names
    const val COLLECTION_USERS = "users"
    const val COLLECTION_STUDENT_PROFILES = "studentProfiles"
    const val COLLECTION_PROFILES = "education_profiles"
    const val COLLECTION_SUBJECTS = "subjects"
    const val COLLECTION_CHAPTERS = "chapters"
    const val COLLECTION_TOPICS = "topics"
    const val COLLECTION_CONCEPTS = "concepts"
    const val COLLECTION_CONCEPT_PREREQUISITES = "conceptPrerequisites"
    const val COLLECTION_DOUBTS = "student_doubts"
    const val COLLECTION_PRACTICE_SESSIONS = "practice_sessions"
    const val COLLECTION_PROGRESS = "student_progress"
    const val COLLECTION_MISTAKES = "common_mistakes"
    const val COLLECTION_REVISIONS = "revision_schedule"

    // Storage Paths
    const val STORAGE_QUESTION_IMAGES = "question_images/{userId}/"
    const val STORAGE_DIAGRAMS = "curriculum_diagrams/"
}

/**
 * Firestore-ready DTO representation of user educational profile
 */
data class FirestoreUserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val grade: String = "Class 10",
    val board: String = "CBSE",
    val languageCode: String = "hinglish",
    val learningStyle: String = "Real-world Analogies",
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

/**
 * Firestore-ready DTO for doubt submissions
 */
data class FirestoreDoubtEntry(
    val doubtId: String = "",
    val userId: String = "",
    val questionText: String = "",
    val mediaUrl: String? = null,
    val explanationMode: String = "INTUITION",
    val status: String = "PENDING_AI", // PENDING_AI, PROCESSED, RESOLVED
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Firestore-ready DTO for concept mastery tracking
 */
data class FirestoreMasteryEntry(
    val userId: String = "",
    val conceptId: String = "",
    val masteryLevel: String = "NOT_STARTED",
    val correctChecksCount: Int = 0,
    val lastPracticedAt: Long = 0L
)
