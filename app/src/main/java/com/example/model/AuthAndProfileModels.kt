package com.example.model

/**
 * User and Student Profile models for Phase 2 Firebase Architecture
 */
data class UserDocument(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "name" to name,
        "email" to email,
        "photoUrl" to photoUrl,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

data class StudentProfileDocument(
    val userId: String = "",
    val educationLevel: String = "School",
    val classOrCourse: String = "Class 10",
    val board: String? = "CBSE",
    val university: String? = null,
    val exam: String? = null,
    val subjects: List<String> = listOf("Physics", "Chemistry", "Mathematics"),
    val preferredLanguage: String = "Hinglish",
    val learningPreferences: List<String> = listOf("Simple explanation", "Real-life examples"),
    val difficultyAreas: List<String> = listOf("Concept समझना", "Formula"),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "educationLevel" to educationLevel,
        "classOrCourse" to classOrCourse,
        "board" to board,
        "university" to university,
        "exam" to exam,
        "subjects" to subjects,
        "preferredLanguage" to preferredLanguage,
        "learningPreferences" to learningPreferences,
        "difficultyAreas" to difficultyAreas,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

// Fixed option constants required by user specifications
object ProfileOptions {
    val educationLevels = listOf(
        "School",
        "Polytechnic/Diploma",
        "College",
        "Competitive Exam",
        "Other"
    )

    val languages = listOf(
        "Hinglish",
        "Hindi",
        "English"
    )

    val difficultyOptions = listOf(
        "Concept समझना",
        "English समझना",
        "Formula",
        "Mathematics",
        "याद रखना",
        "Questions solve करना",
        "शुरुआत कहाँ से करें",
        "Other"
    )

    val learningPreferences = listOf(
        "Simple explanation",
        "Real-life examples",
        "Diagram",
        "Step-by-step",
        "Listening",
        "Practice",
        "Mixed"
    )

    val schoolClasses = listOf(
        "Class 6", "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12"
    )

    val schoolBoards = listOf(
        "CBSE", "ICSE", "State Board (राज्य बोर्ड)", "Other"
    )

    val schoolSubjects = listOf(
        "Physics", "Chemistry", "Mathematics", "Biology", "Science", "English", "Social Science"
    )

    val polytechnicYears = listOf(
        "1st Year (Diploma)", "2nd Year (Diploma)", "3rd Year (Diploma)"
    )

    val collegeCourses = listOf(
        "B.Tech / B.E.", "B.Sc", "BCA", "B.Com", "B.A.", "M.Tech / M.Sc / MCA", "Other Degree"
    )

    val competitiveExams = listOf(
        "JEE Main / Advanced", "NEET", "CUET", "NDA / Defence", "SSC / Banking", "UPSC / State PSC", "Other Exam"
    )
}
