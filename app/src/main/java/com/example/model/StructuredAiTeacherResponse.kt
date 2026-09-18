package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Phase 5: Adaptive Teaching Loop Strategies
 * Solve the core problem: “Student पढ़ रहा है लेकिन concept समझ नहीं आ रहा।”
 *
 * Progression order:
 * Attempt 1: SIMPLE (Simple explanation)
 * Attempt 2: ANALOGY (Real-life analogy)
 * Attempt 3: REAL_LIFE_EXAMPLE (Concrete example)
 * Attempt 4: STEP_BY_STEP (Step-by-step breakdown)
 * Attempt 5: VISUAL (Visual/diagrammatic explanation)
 * Attempt 6: PREREQUISITE (Prerequisite diagnosis & teaching prerequisite first)
 */
enum class ExplanationStrategy(
    val key: String,
    val titleEn: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val iconEmoji: String
) {
    SIMPLE(
        key = "simple",
        titleEn = "Simple Explanation",
        titleHi = "सरल व्याख्या",
        descriptionEn = "Intuition-first non-jargon overview",
        descriptionHi = "बिना कठिन शब्दों के सीधी और स्पष्ट समझ",
        iconEmoji = "💡"
    ),
    ANALOGY(
        key = "analogy",
        titleEn = "Real-Life Analogy",
        titleHi = "दैनिक जीवन की उपमा (Analogy)",
        descriptionEn = "Relatable analogy (e.g. water pipe for electricity)",
        descriptionHi = "दैनिक जीवन से जुड़ी ऐसी उपमा जिसे कोई भी समझ सके",
        iconEmoji = "🔄"
    ),
    REAL_LIFE_EXAMPLE(
        key = "realLifeExample",
        titleEn = "Concrete Example",
        titleHi = "ठोस व्यावहारिक उदाहरण",
        descriptionEn = "Tangible, observable real-world scenario with numbers/context",
        descriptionHi = "वास्तविक जीवन में दिखने वाला स्पष्ट और साकार उदाहरण",
        iconEmoji = "🌍"
    ),
    STEP_BY_STEP(
        key = "stepByStep",
        titleEn = "Step-by-Step Breakdown",
        titleHi = "कदम-दर-कदम विभाजन (Step-by-Step)",
        descriptionEn = "Micro-steps isolating each logical jump",
        descriptionHi = "हर एक छोटे तर्क को अलग-अलग करके चरणबद्ध समझना",
        iconEmoji = "🪜"
    ),
    VISUAL(
        key = "visual",
        titleEn = "Visual Explanation",
        titleHi = "दृश्य/चित्रात्मक व्याख्या (Visual Diagram)",
        descriptionEn = "Mental diagram, layout, flow and spatial metaphor",
        descriptionHi = "मन में बनने वाला स्पष्ट चित्र, प्रवाह और संरचना",
        iconEmoji = "📊"
    ),
    PREREQUISITE(
        key = "prerequisite",
        titleEn = "Prerequisite Diagnosis",
        titleHi = "बुनियादी पूर्व-शर्त (Prerequisite Gap)",
        descriptionEn = "Identifies the underlying foundation gap and teaches it first",
        descriptionHi = "जड़ की कमजोरी पहचान कर पहले बुनियादी बात सिखाना",
        iconEmoji = "🧱"
    );

    companion object {
        fun fromKey(key: String): ExplanationStrategy {
            return entries.find { it.key.equals(key, ignoreCase = true) } ?: SIMPLE
        }

        fun nextStrategy(current: ExplanationStrategy, failedStrategies: Set<ExplanationStrategy>): ExplanationStrategy {
            val sequence = listOf(SIMPLE, ANALOGY, REAL_LIFE_EXAMPLE, STEP_BY_STEP, VISUAL, PREREQUISITE)
            for (candidate in sequence) {
                if (candidate != current && !failedStrategies.contains(candidate)) {
                    return candidate
                }
            }
            return PREREQUISITE
        }
    }
}

/**
 * Tracks the adaptive teaching attempts across strategies
 */
data class AdaptiveTeachingSession(
    val conceptId: String = "",
    val conceptTitle: String = "",
    val question: String = "",
    val explanationAttempts: Int = 1,
    val currentStrategy: ExplanationStrategy = ExplanationStrategy.SIMPLE,
    val failedStrategies: List<ExplanationStrategy> = emptyList(),
    val successfulStrategy: ExplanationStrategy? = null,
    val identifiedMissingPrerequisite: String? = null,
    val isTeachingPrerequisiteFirst: Boolean = false,
    val originalConceptTitle: String? = null,
    val isUnderstoodConfirmed: Boolean = false
)

/**
 * Phase 4 & 5: Structured AI Teacher Response Schema for Samjho AI
 * "रटने के लिए नहीं, समझने के लिए"
 */
@JsonClass(generateAdapter = true)
data class StructuredAiTeacherResponse(
    @Json(name = "intent") val intent: String = "concept_explanation",
    @Json(name = "subject") val subject: String = "",
    @Json(name = "chapter") val chapter: String = "",
    @Json(name = "topic") val topic: String = "",
    @Json(name = "concept") val concept: String = "",
    @Json(name = "difficulty") val difficulty: String = "Medium",
    @Json(name = "prerequisites") val prerequisites: List<String> = emptyList(),
    @Json(name = "explanationStrategy") val explanationStrategy: String = "simple",
    @Json(name = "shortAnswer") val shortAnswer: String = "",
    @Json(name = "easyExplanation") val easyExplanation: String = "",
    @Json(name = "stepByStep") val stepByStep: List<String> = emptyList(),
    @Json(name = "example") val example: String = "",
    @Json(name = "visualNeeded") val visualNeeded: Boolean = false,
    @Json(name = "visualType") val visualType: String = "none",
    @Json(name = "visualDescription") val visualDescription: String = "",
    @Json(name = "understandingCheck") val understandingCheck: String = "",
    @Json(name = "expectedAnswer") val expectedAnswer: String = "",
    @Json(name = "commonMistakes") val commonMistakes: List<String> = emptyList(),
    @Json(name = "examAnswer") val examAnswer: String = "",
    @Json(name = "nextStep") val nextStep: String = "",
    // Phase 5 additions for adaptive tutor loop
    @Json(name = "encouragementMessage") val encouragementMessage: String = "कोई बात नहीं। इसे और basic तरीके से देखते हैं।",
    @Json(name = "whyPreviousStrategyFailed") val whyPreviousStrategyFailed: String = "",
    @Json(name = "prerequisiteIdentified") val prerequisiteIdentified: String? = null,
    @Json(name = "strategyTitleHi") val strategyTitleHi: String = ""
)

/**
 * State container for Ask AI / AI Teacher interactions with Phase 5 adaptive loop
 */
sealed class AiTeacherUiState {
    object Idle : AiTeacherUiState()
    data class Loading(
        val message: String,
        val strategy: ExplanationStrategy = ExplanationStrategy.SIMPLE,
        val attempt: Int = 1
    ) : AiTeacherUiState()
    data class Success(
        val question: String,
        val response: StructuredAiTeacherResponse,
        val currentFocusSection: TeacherFocusSection = TeacherFocusSection.EASY_EXPLANATION,
        val studentCheckAnswerInput: String = "",
        val isAnswerRevealed: Boolean = false,
        val isSimplifiedMore: Boolean = false,
        // Phase 5 Adaptive Session state
        val adaptiveSession: AdaptiveTeachingSession = AdaptiveTeachingSession()
    ) : AiTeacherUiState()
    data class Error(
        val errorType: AiErrorType,
        val userFriendlyMessage: String,
        val technicalDetails: String? = null
    ) : AiTeacherUiState()
}

enum class TeacherFocusSection {
    EASY_EXPLANATION,
    EXAMPLE,
    STEP_BY_STEP,
    EXAM_ANSWER,
    SIMPLER_INTUITION,
    UNDERSTANDING_CHECK
}

enum class AiErrorType {
    API_ERROR,
    NETWORK_TIMEOUT,
    NO_INTERNET,
    EMPTY_RESPONSE,
    CONFIG_MISSING
}
