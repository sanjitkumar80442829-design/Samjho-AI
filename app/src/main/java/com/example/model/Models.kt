package com.example.model

/**
 * Educational domain models for Samjho AI
 * Tagline: “रटने के लिए नहीं, समझने के लिए।”
 */

enum class AppLanguage(val code: String, val title: String, val subtitle: String) {
    HINGLISH("hinglish", "Hinglish (हिंग्लिश)", "Natural mix of Hindi & English concepts"),
    HINDI("hi", "हिंदी (Hindi)", "शुद्ध एवं सहज हिंदी में स्पष्टीकरण"),
    ENGLISH("en", "English", "Clear, intuitive explanations in English")
}

data class EducationProfile(
    val grade: String = "Class 10",
    val board: String = "CBSE",
    val targetExam: String = "Board Exams & Foundation",
    val targetStream: String = "Science & Mathematics"
)

data class LearningPreference(
    val primaryStyle: String = "Real-world Analogies (रोजमर्रा के उदाहरण)",
    val explanationDepth: String = "Deep Conceptual Intuition (मूल तर्क)",
    val preferVisuals: Boolean = true,
    val showCommonMistakes: Boolean = true
)

data class Subject(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val category: String,
    val chaptersCount: Int,
    val conceptsCount: Int,
    val iconKey: String,
    val accentHex: Long
)

data class Chapter(
    val id: String,
    val subjectId: String,
    val number: Int,
    val titleEn: String,
    val titleHi: String,
    val description: String,
    val topicsCount: Int
)

data class Topic(
    val id: String,
    val chapterId: String,
    val titleEn: String,
    val titleHi: String,
    val estimatedMinutes: Int
)

enum class MasteryLevel(val labelEn: String, val labelHi: String, val scorePercentage: Int) {
    NOT_STARTED("Not Started", "शुरू नहीं हुआ", 0),
    EXPLORING("Exploring", "समझना शुरू", 30),
    GRASPING("Grasping Intuition", "मूल समझ बनी", 65),
    MASTERED("Concept Mastered", "पूरी पकड़", 95)
}

data class Concept(
    val id: String,
    val topicId: String,
    val subjectName: String,
    val titleEn: String,
    val titleHi: String,
    val oneLinerIntuition: String,
    val realWorldExample: String,
    val difficulty: String = "Standard",
    val mastery: MasteryLevel = MasteryLevel.NOT_STARTED
)

enum class ExplanationMode(val key: String, val labelEn: String, val labelHi: String, val iconDescription: String) {
    INTUITION("intuition", "Concept Intuition", "मूल भावना", "Why does it work?"),
    STEP_BY_STEP("steps", "Step-by-Step", "क्रमवार हल", "Logical sequence"),
    ANALOGY("analogy", "Real-life Analogy", "उदाहरण", "Daily life connection"),
    EXAM_VIEW("exam", "Exam View", "परीक्षा दृष्टि", "How questions appear")
}

data class DoubtMessage(
    val id: String,
    val studentQuestion: String,
    val hasImageAttachment: Boolean = false,
    val imageDescription: String? = null,
    val mode: ExplanationMode = ExplanationMode.INTUITION,
    val timestamp: String = "Just now"
)

data class TeacherResponse(
    val id: String,
    val doubtId: String,
    val conceptTitle: String,
    val coreIntuition: String,
    val analogy: String,
    val commonPitfall: String,
    val checkQuestion: String
)

data class QuizQuestion(
    val id: String,
    val conceptTitle: String,
    val questionEn: String,
    val questionHi: String,
    val options: List<String>,
    val correctIndex: Int,
    val intuitionExplanation: String
)

data class SubjectProgress(
    val subjectId: String,
    val subjectName: String,
    val totalConcepts: Int,
    val masteredConcepts: Int,
    val accuracy: Int
)

data class MistakeInsight(
    val id: String,
    val conceptTitle: String,
    val subject: String,
    val misconception: String,
    val clarification: String,
    val frequencyTag: String = "Frequent Doubt"
)

data class RevisionItem(
    val id: String,
    val conceptId: String,
    val conceptTitle: String,
    val subject: String,
    val keyRule: String,
    val dueInDays: Int = 1
)

data class StudyPlanItem(
    val id: String,
    val title: String,
    val subject: String,
    val targetMinutes: Int,
    val isCompleted: Boolean = false
)
