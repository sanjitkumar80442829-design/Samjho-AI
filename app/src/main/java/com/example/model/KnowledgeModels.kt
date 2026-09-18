package com.example.model

/**
 * Educational Knowledge Structure Models for Samjho AI (Phase 3)
 * Structure: Subject -> Chapter -> Topic -> Concept -> Prerequisites
 */

data class SubjectEntity(
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
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "subjectId" to subjectId,
        "name" to name,
        "nameEn" to nameEn,
        "nameHi" to nameHi,
        "category" to category,
        "description" to description,
        "iconKey" to iconKey,
        "order" to order,
        "chaptersCount" to chaptersCount,
        "conceptsCount" to conceptsCount,
        "accentHex" to accentHex,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

data class ChapterEntity(
    val chapterId: String = "",
    val subjectId: String = "",
    val title: String = "",
    val titleEn: String = "",
    val titleHi: String = "",
    val description: String = "",
    val order: Int = 0,
    val topicsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "chapterId" to chapterId,
        "subjectId" to subjectId,
        "title" to title,
        "titleEn" to titleEn,
        "titleHi" to titleHi,
        "description" to description,
        "order" to order,
        "topicsCount" to topicsCount,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

data class TopicEntity(
    val topicId: String = "",
    val chapterId: String = "",
    val subjectId: String = "",
    val title: String = "",
    val titleEn: String = "",
    val titleHi: String = "",
    val description: String = "",
    val order: Int = 0,
    val estimatedMinutes: Int = 15,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "topicId" to topicId,
        "chapterId" to chapterId,
        "subjectId" to subjectId,
        "title" to title,
        "titleEn" to titleEn,
        "titleHi" to titleHi,
        "description" to description,
        "order" to order,
        "estimatedMinutes" to estimatedMinutes,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

data class UnderstandingCheckData(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val explanation: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "question" to question,
        "options" to options,
        "correctOptionIndex" to correctOptionIndex,
        "explanation" to explanation
    )
}

data class ConceptEntity(
    val conceptId: String = "",
    val subjectId: String = "",
    val chapterId: String = "",
    val topicId: String = "",
    val title: String = "",
    val description: String = "",
    val difficulty: String = "Standard", // Beginner, Standard, Advanced
    val prerequisites: List<String> = emptyList(), // Concept IDs
    val order: Int = 0,
    val estimatedMinutes: Int = 12,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    // Content for the Comprehensive Concept Page
    val whyThisMatters: String = "",
    val explanationPlaceholder: String = "",
    val examplePlaceholder: String = "",
    val practice: String = "",
    val understandingCheck: UnderstandingCheckData = UnderstandingCheckData(),
    val commonMistakes: List<String> = emptyList(),
    val examAnswer: String = "",
    val revision: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "conceptId" to conceptId,
        "subjectId" to subjectId,
        "chapterId" to chapterId,
        "topicId" to topicId,
        "title" to title,
        "description" to description,
        "difficulty" to difficulty,
        "prerequisites" to prerequisites,
        "order" to order,
        "estimatedMinutes" to estimatedMinutes,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "whyThisMatters" to whyThisMatters,
        "explanationPlaceholder" to explanationPlaceholder,
        "examplePlaceholder" to examplePlaceholder,
        "practice" to practice,
        "understandingCheck" to understandingCheck.toMap(),
        "commonMistakes" to commonMistakes,
        "examAnswer" to examAnswer,
        "revision" to revision
    )
}

data class ConceptPrerequisiteEntity(
    val prerequisiteId: String = "",
    val sourceConceptId: String = "", // The concept you need first
    val targetConceptId: String = "", // The concept that requires it
    val sourceTitle: String = "",
    val targetTitle: String = "",
    val relationshipType: String = "DIRECT_PREREQUISITE",
    val reason: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "prerequisiteId" to prerequisiteId,
        "sourceConceptId" to sourceConceptId,
        "targetConceptId" to targetConceptId,
        "sourceTitle" to sourceTitle,
        "targetTitle" to targetTitle,
        "relationshipType" to relationshipType,
        "reason" to reason,
        "createdAt" to createdAt
    )
}
