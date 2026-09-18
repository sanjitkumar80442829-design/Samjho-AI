package com.example.data

import android.content.Context
import android.util.Log
import com.example.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class KnowledgeRepository(private val context: Context) {

    private val tag = "KnowledgeRepository"

    private val firestore: FirebaseFirestore?
        get() = try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else null
        } catch (e: Exception) {
            Log.w(tag, "Firestore not available: ${e.message}")
            null
        }

    // Cached in-memory storage for offline / quick responsiveness and fallback
    private val localSubjects = DefaultKnowledgeSeed.subjects.toMutableList()
    private val localChapters = DefaultKnowledgeSeed.chapters.toMutableList()
    private val localTopics = DefaultKnowledgeSeed.topics.toMutableList()
    private val localConcepts = DefaultKnowledgeSeed.concepts.toMutableList()
    private val localPrerequisites = DefaultKnowledgeSeed.prerequisites.toMutableList()

    suspend fun getSubjects(): List<SubjectEntity> {
        val db = firestore
        if (db == null) return localSubjects.sortedBy { it.order }

        return try {
            val snapshot = db.collection(FirebaseSchema.COLLECTION_SUBJECTS)
                .orderBy("order")
                .get()
                .await()

            if (!snapshot.isEmpty) {
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(SubjectDto::class.java)?.toEntity(doc.id)
                }
            } else {
                // Seed default educational knowledge structure to Firestore
                seedFirestoreKnowledgeBase(db)
                localSubjects.sortedBy { it.order }
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to load subjects from Firestore, falling back to dynamic local: ${e.message}")
            localSubjects.sortedBy { it.order }
        }
    }

    suspend fun getChapters(subjectId: String): List<ChapterEntity> {
        val db = firestore
        if (db == null) return localChapters.filter { it.subjectId == subjectId }.sortedBy { it.order }

        return try {
            val snapshot = db.collection(FirebaseSchema.COLLECTION_CHAPTERS)
                .whereEqualTo("subjectId", subjectId)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ChapterDto::class.java)?.toEntity(doc.id)
                }.sortedBy { it.order }
            } else {
                localChapters.filter { it.subjectId == subjectId }.sortedBy { it.order }
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to load chapters: ${e.message}")
            localChapters.filter { it.subjectId == subjectId }.sortedBy { it.order }
        }
    }

    suspend fun getTopics(chapterId: String): List<TopicEntity> {
        val db = firestore
        if (db == null) return localTopics.filter { it.chapterId == chapterId }.sortedBy { it.order }

        return try {
            val snapshot = db.collection(FirebaseSchema.COLLECTION_TOPICS)
                .whereEqualTo("chapterId", chapterId)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TopicDto::class.java)?.toEntity(doc.id)
                }.sortedBy { it.order }
            } else {
                localTopics.filter { it.chapterId == chapterId }.sortedBy { it.order }
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to load topics: ${e.message}")
            localTopics.filter { it.chapterId == chapterId }.sortedBy { it.order }
        }
    }

    suspend fun getConcepts(topicId: String): List<ConceptEntity> {
        val db = firestore
        if (db == null) return localConcepts.filter { it.topicId == topicId }.sortedBy { it.order }

        return try {
            val snapshot = db.collection(FirebaseSchema.COLLECTION_CONCEPTS)
                .whereEqualTo("topicId", topicId)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ConceptDto::class.java)?.toEntity(doc.id)
                }.sortedBy { it.order }
            } else {
                localConcepts.filter { it.topicId == topicId }.sortedBy { it.order }
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to load concepts: ${e.message}")
            localConcepts.filter { it.topicId == topicId }.sortedBy { it.order }
        }
    }

    suspend fun getConcept(conceptId: String): ConceptEntity? {
        val db = firestore
        if (db == null) return localConcepts.find { it.conceptId == conceptId }

        return try {
            val doc = db.collection(FirebaseSchema.COLLECTION_CONCEPTS)
                .document(conceptId)
                .get()
                .await()

            if (doc.exists()) {
                doc.toObject(ConceptDto::class.java)?.toEntity(doc.id)
            } else {
                localConcepts.find { it.conceptId == conceptId }
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to load concept $conceptId: ${e.message}")
            localConcepts.find { it.conceptId == conceptId }
        }
    }

    suspend fun getPrerequisitesForConcept(conceptId: String): List<ConceptPrerequisiteEntity> {
        val db = firestore
        if (db == null) return localPrerequisites.filter { it.targetConceptId == conceptId }

        return try {
            val snapshot = db.collection(FirebaseSchema.COLLECTION_CONCEPT_PREREQUISITES)
                .whereEqualTo("targetConceptId", conceptId)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PrerequisiteDto::class.java)?.toEntity(doc.id)
                }
            } else {
                localPrerequisites.filter { it.targetConceptId == conceptId }
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to load prerequisites: ${e.message}")
            localPrerequisites.filter { it.targetConceptId == conceptId }
        }
    }

    suspend fun getConceptsByIds(ids: List<String>): List<ConceptEntity> {
        if (ids.isEmpty()) return emptyList()
        return ids.mapNotNull { id -> getConcept(id) }
    }

    private suspend fun seedFirestoreKnowledgeBase(db: FirebaseFirestore) {
        try {
            val batch = db.batch()

            // 1. Subjects
            localSubjects.forEach { sub ->
                val ref = db.collection(FirebaseSchema.COLLECTION_SUBJECTS).document(sub.subjectId)
                batch.set(ref, sub.toMap())
            }

            // 2. Chapters
            localChapters.forEach { ch ->
                val ref = db.collection(FirebaseSchema.COLLECTION_CHAPTERS).document(ch.chapterId)
                batch.set(ref, ch.toMap())
            }

            // 3. Topics
            localTopics.forEach { top ->
                val ref = db.collection(FirebaseSchema.COLLECTION_TOPICS).document(top.topicId)
                batch.set(ref, top.toMap())
            }

            // 4. Concepts
            localConcepts.forEach { c ->
                val ref = db.collection(FirebaseSchema.COLLECTION_CONCEPTS).document(c.conceptId)
                batch.set(ref, c.toMap())
            }

            // 5. Prerequisites
            localPrerequisites.forEach { pre ->
                val ref = db.collection(FirebaseSchema.COLLECTION_CONCEPT_PREREQUISITES).document(pre.prerequisiteId)
                batch.set(ref, pre.toMap())
            }

            batch.commit().await()
            Log.d(tag, "Successfully seeded Firestore knowledge base with ${localConcepts.size} concepts.")
        } catch (e: Exception) {
            Log.w(tag, "Seeding Firestore failed: ${e.message}")
        }
    }
}

// Firestore DTOs for safe deserialization
internal data class SubjectDto(
    val subjectId: String = "",
    val name: String = "",
    val nameEn: String = "",
    val nameHi: String = "",
    val category: String = "",
    val description: String = "",
    val iconKey: String = "science",
    val order: Int = 0,
    val chaptersCount: Int = 0,
    val conceptsCount: Int = 0,
    val accentHex: Long = 0xFF1976D2,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    fun toEntity(docId: String): SubjectEntity = SubjectEntity(
        subjectId = docId.ifBlank { subjectId },
        name = name,
        nameEn = nameEn,
        nameHi = nameHi,
        category = category,
        description = description,
        iconKey = iconKey,
        order = order,
        chaptersCount = chaptersCount,
        conceptsCount = conceptsCount,
        accentHex = accentHex,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

internal data class ChapterDto(
    val chapterId: String = "",
    val subjectId: String = "",
    val title: String = "",
    val titleEn: String = "",
    val titleHi: String = "",
    val description: String = "",
    val order: Int = 0,
    val topicsCount: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    fun toEntity(docId: String): ChapterEntity = ChapterEntity(
        chapterId = docId.ifBlank { chapterId },
        subjectId = subjectId,
        title = title,
        titleEn = titleEn,
        titleHi = titleHi,
        description = description,
        order = order,
        topicsCount = topicsCount,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

internal data class TopicDto(
    val topicId: String = "",
    val chapterId: String = "",
    val subjectId: String = "",
    val title: String = "",
    val titleEn: String = "",
    val titleHi: String = "",
    val description: String = "",
    val order: Int = 0,
    val estimatedMinutes: Int = 15,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    fun toEntity(docId: String): TopicEntity = TopicEntity(
        topicId = docId.ifBlank { topicId },
        chapterId = chapterId,
        subjectId = subjectId,
        title = title,
        titleEn = titleEn,
        titleHi = titleHi,
        description = description,
        order = order,
        estimatedMinutes = estimatedMinutes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

internal data class ConceptDto(
    val conceptId: String = "",
    val subjectId: String = "",
    val chapterId: String = "",
    val topicId: String = "",
    val title: String = "",
    val description: String = "",
    val difficulty: String = "Standard",
    val prerequisites: List<String> = emptyList(),
    val order: Int = 0,
    val estimatedMinutes: Int = 12,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val whyThisMatters: String = "",
    val explanationPlaceholder: String = "",
    val examplePlaceholder: String = "",
    val practice: String = "",
    val understandingCheck: Map<String, Any?> = emptyMap(),
    val commonMistakes: List<String> = emptyList(),
    val examAnswer: String = "",
    val revision: String = ""
) {
    fun toEntity(docId: String): ConceptEntity {
        val checkQuestion = understandingCheck["question"] as? String ?: ""
        @Suppress("UNCHECKED_CAST")
        val checkOptions = (understandingCheck["options"] as? List<String>) ?: emptyList()
        val checkCorrect = (understandingCheck["correctOptionIndex"] as? Number)?.toInt() ?: 0
        val checkExpl = understandingCheck["explanation"] as? String ?: ""

        return ConceptEntity(
            conceptId = docId.ifBlank { conceptId },
            subjectId = subjectId,
            chapterId = chapterId,
            topicId = topicId,
            title = title,
            description = description,
            difficulty = difficulty,
            prerequisites = prerequisites,
            order = order,
            estimatedMinutes = estimatedMinutes,
            createdAt = createdAt,
            updatedAt = updatedAt,
            whyThisMatters = whyThisMatters,
            explanationPlaceholder = explanationPlaceholder,
            examplePlaceholder = examplePlaceholder,
            practice = practice,
            understandingCheck = UnderstandingCheckData(
                question = checkQuestion,
                options = checkOptions,
                correctOptionIndex = checkCorrect,
                explanation = checkExpl
            ),
            commonMistakes = commonMistakes,
            examAnswer = examAnswer,
            revision = revision
        )
    }
}

internal data class PrerequisiteDto(
    val prerequisiteId: String = "",
    val sourceConceptId: String = "",
    val targetConceptId: String = "",
    val sourceTitle: String = "",
    val targetTitle: String = "",
    val relationshipType: String = "DIRECT_PREREQUISITE",
    val reason: String = "",
    val createdAt: Long = 0L
) {
    fun toEntity(docId: String): ConceptPrerequisiteEntity = ConceptPrerequisiteEntity(
        prerequisiteId = docId.ifBlank { prerequisiteId },
        sourceConceptId = sourceConceptId,
        targetConceptId = targetConceptId,
        sourceTitle = sourceTitle,
        targetTitle = targetTitle,
        relationshipType = relationshipType,
        reason = reason,
        createdAt = createdAt
    )
}
