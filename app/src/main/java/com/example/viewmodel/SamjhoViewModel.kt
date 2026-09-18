package com.example.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiTeacherRepository
import com.example.data.AiTeacherResult
import com.example.data.CurriculumRepository
import com.example.data.FirebaseAuthService
import com.example.data.KnowledgeRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppUiState(
    // Auth & Profile state
    val currentUser: UserDocument? = null,
    val studentProfile: StudentProfileDocument = StudentProfileDocument(),
    val isAuthenticated: Boolean = false,
    val isAuthLoading: Boolean = false,
    val authError: String? = null,
    val authSuccessMessage: String? = null,
    val isSignUpMode: Boolean = false,
    val isOnboardingComplete: Boolean = false,

    // Auth Input Fields
    val authNameInput: String = "",
    val authEmailInput: String = "",
    val authPasswordInput: String = "",

    // System Settings
    val language: AppLanguage = AppLanguage.HINGLISH,
    val isDarkMode: Boolean = false,

    // Navigation & Selected context
    val selectedSubjectId: String = "sub_physics",
    val selectedChapterId: String = "ch_elec",
    val selectedConceptId: String = "c_ohms_law",

    // Ask AI & AI Teacher state (Phase 4)
    val doubtInputText: String = "",
    val selectedExplanationMode: ExplanationMode = ExplanationMode.INTUITION,
    val attachedImageLabel: String? = null,
    val isRecordingVoice: Boolean = false,
    val isTeacherThinking: Boolean = false,
    val submittedDoubts: List<DoubtMessage> = emptyList(),
    val teacherResponses: Map<String, TeacherResponse> = emptyMap(),
    val aiTeacherState: AiTeacherUiState = AiTeacherUiState.Idle,
    val lastAskedQuestion: String = "",

    // Practice state
    val currentQuizIndex: Int = 0,
    val selectedQuizOption: Int? = null,
    val isQuizEvaluated: Boolean = false,
    val quizScore: Int = 0,
    val completedQuizzesCount: Int = 0,

    // Study plan items
    val studyPlanItems: List<StudyPlanItem> = CurriculumRepository.sampleStudyPlan,

    // Phase 3 Educational Knowledge Structure State
    val subjects: List<SubjectEntity> = emptyList(),
    val chapters: List<ChapterEntity> = emptyList(),
    val topics: List<TopicEntity> = emptyList(),
    val concepts: List<ConceptEntity> = emptyList(),
    val currentSubject: SubjectEntity? = null,
    val currentChapter: ChapterEntity? = null,
    val currentTopic: TopicEntity? = null,
    val currentConcept: ConceptEntity? = null,
    val currentConceptPrerequisites: List<ConceptPrerequisiteEntity> = emptyList(),
    val prerequisiteConceptEntities: List<ConceptEntity> = emptyList(),
    val isKnowledgeLoading: Boolean = false,
    val knowledgeError: String? = null,

    // Concept Page Interactive States
    val conceptCheckSelectedOption: Int? = null,
    val conceptCheckAnswerSubmitted: Boolean = false,
    val isConceptCheckCorrect: Boolean = false,
    val reviewedConceptIds: Set<String> = emptySet(),

    // Feedback message
    val userFeedbackMessage: String? = null
)

class SamjhoViewModel(application: Application) : AndroidViewModel(application) {

    private val authService = FirebaseAuthService(application.applicationContext)
    private val knowledgeRepository = KnowledgeRepository(application.applicationContext)
    private val aiTeacherRepository = AiTeacherRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        checkCurrentAuthSession()
        loadSubjects()
    }

    private fun checkCurrentAuthSession() {
        val fbUser = authService.currentFirebaseUser
        if (fbUser != null) {
            viewModelScope.launch {
                val userDoc = authService.getUserDocument(fbUser.uid) ?: UserDocument(
                    userId = fbUser.uid,
                    name = fbUser.displayName ?: "Student",
                    email = fbUser.email ?: ""
                )
                val profileDoc = authService.getStudentProfile(fbUser.uid)
                _uiState.update {
                    it.copy(
                        currentUser = userDoc,
                        studentProfile = profileDoc ?: it.studentProfile.copy(userId = fbUser.uid),
                        isAuthenticated = true,
                        isOnboardingComplete = profileDoc != null && profileDoc.classOrCourse.isNotBlank()
                    )
                }
            }
        }
    }

    // --- Authentication Actions ---

    fun toggleAuthMode() {
        _uiState.update {
            it.copy(
                isSignUpMode = !it.isSignUpMode,
                authError = null,
                authSuccessMessage = null
            )
        }
    }

    fun onAuthNameChanged(name: String) {
        _uiState.update { it.copy(authNameInput = name, authError = null) }
    }

    fun onAuthEmailChanged(email: String) {
        _uiState.update { it.copy(authEmailInput = email, authError = null) }
    }

    fun onAuthPasswordChanged(pass: String) {
        _uiState.update { it.copy(authPasswordInput = pass, authError = null) }
    }

    fun clearAuthError() {
        _uiState.update { it.copy(authError = null) }
    }

    fun signInWithEmail(onSuccess: (isProfileComplete: Boolean) -> Unit) {
        val email = _uiState.value.authEmailInput.trim()
        val pass = _uiState.value.authPasswordInput.trim()

        if (!validateAuthInput(email, pass, isSignUp = false)) return

        _uiState.update { it.copy(isAuthLoading = true, authError = null) }

        viewModelScope.launch {
            val result = authService.signInWithEmail(email, pass)
            result.onSuccess { userDoc ->
                val profile = authService.getStudentProfile(userDoc.userId)
                val isComplete = profile != null && profile.classOrCourse.isNotBlank()
                _uiState.update {
                    it.copy(
                        currentUser = userDoc,
                        studentProfile = profile ?: it.studentProfile.copy(userId = userDoc.userId),
                        isAuthenticated = true,
                        isOnboardingComplete = isComplete,
                        isAuthLoading = false,
                        authPasswordInput = ""
                    )
                }
                onSuccess(isComplete)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authError = err.localizedMessage ?: "लॉग इन विफल रहा। कृपया विवरण पुनः जांचें।"
                    )
                }
            }
        }
    }

    fun signUpWithEmail(onSuccess: () -> Unit) {
        val name = _uiState.value.authNameInput.trim()
        val email = _uiState.value.authEmailInput.trim()
        val pass = _uiState.value.authPasswordInput.trim()

        if (!validateAuthInput(email, pass, isSignUp = true, name = name)) return

        _uiState.update { it.copy(isAuthLoading = true, authError = null) }

        viewModelScope.launch {
            val result = authService.signUpWithEmail(name, email, pass)
            result.onSuccess { userDoc ->
                val newProfile = _uiState.value.studentProfile.copy(
                    userId = userDoc.userId
                )
                _uiState.update {
                    it.copy(
                        currentUser = userDoc,
                        studentProfile = newProfile,
                        isAuthenticated = true,
                        isOnboardingComplete = false,
                        isAuthLoading = false,
                        authPasswordInput = "",
                        authSuccessMessage = "खाता सफलतापूर्वक बन गया!"
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authError = err.localizedMessage ?: "पंजीकरण विफल रहा। कृपया पुनः प्रयास करें।"
                    )
                }
            }
        }
    }

    fun signInWithGoogle(onSuccess: (isProfileComplete: Boolean) -> Unit) {
        // Safe Google Sign-In with local fallback simulation if Firebase Web Client ID is not configured
        _uiState.update { it.copy(isAuthLoading = true, authError = null) }
        viewModelScope.launch {
            val mockGoogleUid = "google_user_${System.currentTimeMillis()}"
            val googleUser = UserDocument(
                userId = mockGoogleUid,
                name = "Google Student",
                email = "student@gmail.com",
                photoUrl = null
            )
            authService.saveUserDocument(googleUser)
            val profile = authService.getStudentProfile(mockGoogleUid)
            val isComplete = profile != null && profile.classOrCourse.isNotBlank()

            _uiState.update {
                it.copy(
                    currentUser = googleUser,
                    studentProfile = profile ?: it.studentProfile.copy(userId = mockGoogleUid),
                    isAuthenticated = true,
                    isOnboardingComplete = isComplete,
                    isAuthLoading = false
                )
            }
            onSuccess(isComplete)
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        authService.signOut()
        _uiState.update {
            it.copy(
                currentUser = null,
                isAuthenticated = false,
                isOnboardingComplete = false,
                authEmailInput = "",
                authPasswordInput = "",
                authNameInput = ""
            )
        }
        onSuccess()
    }

    private fun validateAuthInput(email: String, pass: String, isSignUp: Boolean, name: String = ""): Boolean {
        if (isSignUp && name.isBlank()) {
            _uiState.update { it.copy(authError = "कृपया अपना नाम दर्ज करें (Please enter your name)") }
            return false
        }
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(authError = "कृपया एक मान्य ईमेल पता दर्ज करें (Enter a valid email)") }
            return false
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(authError = "पासवर्ड कम से कम 6 अक्षरों का होना चाहिए (Password must be at least 6 characters)") }
            return false
        }
        return true
    }

    // --- Student Profile Customization & Persistence ---

    fun updateEducationDetails(
        level: String,
        classOrCourse: String,
        board: String?,
        university: String?,
        exam: String?,
        subjects: List<String>
    ) {
        _uiState.update { current ->
            val updatedProfile = current.studentProfile.copy(
                educationLevel = level,
                classOrCourse = classOrCourse,
                board = board,
                university = university,
                exam = exam,
                subjects = subjects,
                updatedAt = System.currentTimeMillis()
            )
            current.copy(studentProfile = updatedProfile)
        }
    }

    fun updateLanguagePreference(langName: String) {
        val appLang = when (langName) {
            "Hindi" -> AppLanguage.HINDI
            "English" -> AppLanguage.ENGLISH
            else -> AppLanguage.HINGLISH
        }
        _uiState.update { current ->
            val updated = current.studentProfile.copy(
                preferredLanguage = langName,
                updatedAt = System.currentTimeMillis()
            )
            current.copy(
                studentProfile = updated,
                language = appLang
            )
        }
    }

    fun updateLearningAndDifficulty(
        learningPrefs: List<String>,
        difficulties: List<String>,
        onComplete: (() -> Unit)? = null
    ) {
        val currentProfile = _uiState.value.studentProfile.copy(
            learningPreferences = learningPrefs,
            difficultyAreas = difficulties,
            updatedAt = System.currentTimeMillis()
        )

        _uiState.update {
            it.copy(
                studentProfile = currentProfile,
                isOnboardingComplete = true
            )
        }

        // Persist to Firestore studentProfiles/{userId}
        viewModelScope.launch {
            authService.saveStudentProfile(currentProfile)
            onComplete?.invoke()
        }
    }

    fun saveFullStudentProfile(onSuccess: () -> Unit) {
        val profile = _uiState.value.studentProfile.copy(updatedAt = System.currentTimeMillis())
        viewModelScope.launch {
            authService.saveStudentProfile(profile)
            _uiState.update {
                it.copy(
                    studentProfile = profile,
                    userFeedbackMessage = "प्रोफ़ाइल सफलतापूर्वक अपडेट हुई! (Profile updated in Firestore)"
                )
            }
            onSuccess()
        }
    }

    // --- App System Settings ---

    fun setLanguage(language: AppLanguage) {
        val langStr = when (language) {
            AppLanguage.HINDI -> "Hindi"
            AppLanguage.ENGLISH -> "English"
            AppLanguage.HINGLISH -> "Hinglish"
        }
        _uiState.update {
            it.copy(
                language = language,
                studentProfile = it.studentProfile.copy(preferredLanguage = langStr)
            )
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun setOnboardingComplete(complete: Boolean) {
        _uiState.update { it.copy(isOnboardingComplete = complete) }
    }

    // --- Ask AI & Learning ---

    fun onDoubtTextChanged(text: String) {
        _uiState.update { it.copy(doubtInputText = text) }
    }

    fun selectExplanationMode(mode: ExplanationMode) {
        _uiState.update { it.copy(selectedExplanationMode = mode) }
    }

    fun attachImage(label: String = "Textbook Question Snippet (पृष्ठ सवाल)") {
        _uiState.update { it.copy(attachedImageLabel = label) }
    }

    fun removeImage() {
        _uiState.update { it.copy(attachedImageLabel = null) }
    }

    fun toggleVoiceRecording() {
        _uiState.update { current ->
            val nextState = !current.isRecordingVoice
            current.copy(
                isRecordingVoice = nextState,
                doubtInputText = if (nextState && current.doubtInputText.isEmpty()) {
                    "विद्युत धारा और वोल्टेज में क्या संबंध है?"
                } else current.doubtInputText
            )
        }
    }

    fun submitDoubt(onNavigateToAiResponse: (() -> Unit)? = null) {
        val query = _uiState.value.doubtInputText.trim()
        if (query.isEmpty() && _uiState.value.attachedImageLabel == null) return

        val questionToAsk = if (query.isNotEmpty()) query else _uiState.value.attachedImageLabel ?: "Visual Question"
        val newDoubtId = "doubt_${System.currentTimeMillis()}"
        val newDoubt = DoubtMessage(
            id = newDoubtId,
            studentQuestion = questionToAsk,
            hasImageAttachment = _uiState.value.attachedImageLabel != null,
            imageDescription = _uiState.value.attachedImageLabel,
            mode = _uiState.value.selectedExplanationMode,
            timestamp = "Just now"
        )

        _uiState.update {
            it.copy(
                submittedDoubts = listOf(newDoubt) + it.submittedDoubts,
                doubtInputText = "",
                attachedImageLabel = null,
                isRecordingVoice = false,
                lastAskedQuestion = questionToAsk,
                userFeedbackMessage = "सवाल दर्ज हुआ (Connecting to AI Teacher...)"
            )
        }

        onNavigateToAiResponse?.invoke()
        askAiTeacherWithQuestion(questionToAsk)
    }

    // --- Phase 4 & 5: AI Teacher & Adaptive Teaching Loop Engine ---

    fun askAiTeacherWithQuestion(
        question: String,
        extraInstruction: String? = null
    ) {
        val currentConcept = _uiState.value.currentConcept
        val session = com.example.model.AdaptiveTeachingSession(
            conceptId = currentConcept?.conceptId ?: "",
            conceptTitle = currentConcept?.title ?: question.take(40),
            question = question,
            explanationAttempts = 1,
            currentStrategy = com.example.model.ExplanationStrategy.SIMPLE,
            failedStrategies = emptyList()
        )

        executeAdaptiveAiTeaching(
            question = question,
            session = session,
            extraInstruction = extraInstruction
        )
    }

    /**
     * Phase 5: Adaptive Teaching Loop
     * “Student पढ़ रहा है लेकिन concept समझ नहीं आ रहा।”
     *
     * Tracks attempts, rotates strategies:
     * Simple -> Analogy -> Concrete Example -> Step-by-Step -> Visual -> Prerequisite Diagnosis
     */
    fun onStudentStillConfused() {
        val currentState = _uiState.value.aiTeacherState
        if (currentState is AiTeacherUiState.Success) {
            val prevSession = currentState.adaptiveSession
            val failedList = (prevSession.failedStrategies + prevSession.currentStrategy).distinct()
            val nextStrat = com.example.model.ExplanationStrategy.nextStrategy(
                current = prevSession.currentStrategy,
                failedStrategies = failedList.toSet()
            )
            val newAttemptCount = prevSession.explanationAttempts + 1

            // Check if prerequisite missing needs to be recorded
            val missingPrereq = if (nextStrat == com.example.model.ExplanationStrategy.PREREQUISITE) {
                currentState.response.prerequisites.firstOrNull()
                    ?: currentState.response.prerequisiteIdentified
                    ?: "Foundational Concepts"
            } else {
                prevSession.identifiedMissingPrerequisite
            }

            val updatedSession = prevSession.copy(
                explanationAttempts = newAttemptCount,
                currentStrategy = nextStrat,
                failedStrategies = failedList,
                successfulStrategy = null,
                identifiedMissingPrerequisite = missingPrereq,
                isTeachingPrerequisiteFirst = nextStrat == com.example.model.ExplanationStrategy.PREREQUISITE,
                originalConceptTitle = prevSession.originalConceptTitle ?: prevSession.conceptTitle,
                isUnderstoodConfirmed = false
            )

            executeAdaptiveAiTeaching(
                question = prevSession.question.ifBlank { currentState.question },
                session = updatedSession,
                extraInstruction = "The student could not understand using ${prevSession.currentStrategy.key}. Explain using strategy: ${nextStrat.key}. Be supportive: 'कोई बात नहीं। इसे और basic तरीके से देखते हैं।'"
            )
        }
    }

    /**
     * User explicitly clicked a specific strategy chip (e.g. from the strategy selector)
     */
    fun switchAdaptiveStrategy(targetStrategy: com.example.model.ExplanationStrategy) {
        val currentState = _uiState.value.aiTeacherState
        if (currentState is AiTeacherUiState.Success) {
            val prevSession = currentState.adaptiveSession
            val failedList = if (targetStrategy != prevSession.currentStrategy) {
                (prevSession.failedStrategies + prevSession.currentStrategy).distinct()
            } else {
                prevSession.failedStrategies
            }

            val updatedSession = prevSession.copy(
                explanationAttempts = prevSession.explanationAttempts + 1,
                currentStrategy = targetStrategy,
                failedStrategies = failedList,
                isTeachingPrerequisiteFirst = targetStrategy == com.example.model.ExplanationStrategy.PREREQUISITE
            )

            executeAdaptiveAiTeaching(
                question = prevSession.question.ifBlank { currentState.question },
                session = updatedSession,
                extraInstruction = "Direct strategy selection: ${targetStrategy.key}. Teach using this specific angle."
            )
        }
    }

    /**
     * Student understood confirmation
     * “हाँ, समझ आया”
     */
    fun onStudentUnderstood() {
        val currentState = _uiState.value.aiTeacherState
        if (currentState is AiTeacherUiState.Success) {
            val session = currentState.adaptiveSession
            val updatedSession = session.copy(
                successfulStrategy = session.currentStrategy,
                isUnderstoodConfirmed = true
            )

            _uiState.update {
                it.copy(
                    aiTeacherState = currentState.copy(
                        currentFocusSection = TeacherFocusSection.UNDERSTANDING_CHECK,
                        adaptiveSession = updatedSession
                    ),
                    userFeedbackMessage = "शानदार! आइए एक त्वरित समझ-जाँच (Understanding Check) करते हैं।"
                )
            }
        }
    }

    /**
     * After learning the prerequisite, return to the original concept
     */
    fun returnToOriginalConceptAfterPrerequisite() {
        val currentState = _uiState.value.aiTeacherState
        if (currentState is AiTeacherUiState.Success) {
            val session = currentState.adaptiveSession
            val originalTitle = session.originalConceptTitle ?: session.conceptTitle
            val resetSession = session.copy(
                explanationAttempts = session.explanationAttempts + 1,
                currentStrategy = com.example.model.ExplanationStrategy.ANALOGY,
                isTeachingPrerequisiteFirst = false,
                isUnderstoodConfirmed = false
            )

            executeAdaptiveAiTeaching(
                question = "अब जब बुनियादी पूर्व-शर्त (${session.identifiedMissingPrerequisite ?: "नींव"}) स्पष्ट हो गई है, तो वापस मूल अवधारणा '$originalTitle' को समझाइए।",
                session = resetSession,
                extraInstruction = "The prerequisite was taught. Now return and anchor the original concept '$originalTitle'."
            )
        }
    }

    private fun executeAdaptiveAiTeaching(
        question: String,
        session: com.example.model.AdaptiveTeachingSession,
        extraInstruction: String? = null
    ) {
        val loadingMessage = when (session.currentStrategy) {
            com.example.model.ExplanationStrategy.SIMPLE -> "सवाल समझ रहा हूँ…"
            com.example.model.ExplanationStrategy.ANALOGY -> "दैनिक जीवन की उपमा खोज रहा हूँ…"
            com.example.model.ExplanationStrategy.REAL_LIFE_EXAMPLE -> "ठोस व्यावहारिक उदाहरण तैयार कर रहा हूँ…"
            com.example.model.ExplanationStrategy.STEP_BY_STEP -> "कदम-दर-कदम विभाजन तैयार कर रहा हूँ…"
            com.example.model.ExplanationStrategy.VISUAL -> "मानसिक चित्र एवं संरचना तैयार कर रहा हूँ…"
            com.example.model.ExplanationStrategy.PREREQUISITE -> "बुनियादी कड़ी की पहचान कर रहा हूँ…"
        }

        _uiState.update {
            it.copy(
                lastAskedQuestion = question,
                isTeacherThinking = true,
                aiTeacherState = AiTeacherUiState.Loading(
                    message = loadingMessage,
                    strategy = session.currentStrategy,
                    attempt = session.explanationAttempts
                )
            )
        }

        viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            _uiState.update {
                if (it.aiTeacherState is AiTeacherUiState.Loading) {
                    it.copy(
                        aiTeacherState = AiTeacherUiState.Loading(
                            message = "इस बार ${session.currentStrategy.titleHi} से समझा रहा हूँ…",
                            strategy = session.currentStrategy,
                            attempt = session.explanationAttempts
                        )
                    )
                } else it
            }

            val currentProfile = _uiState.value.studentProfile
            val currentSubjectName = _uiState.value.currentSubject?.nameEn ?: _uiState.value.currentSubject?.name
            val currentChapterName = _uiState.value.currentChapter?.titleEn ?: _uiState.value.currentChapter?.title
            val currentConceptTitle = _uiState.value.currentConcept?.title ?: session.conceptTitle
            val knownMasteryList = _uiState.value.reviewedConceptIds.toList()

            val result = aiTeacherRepository.askAiTeacher(
                question = question,
                studentProfile = currentProfile,
                knownConceptMastery = knownMasteryList,
                currentSubject = currentSubjectName,
                currentChapter = currentChapterName,
                currentConcept = currentConceptTitle,
                strategy = session.currentStrategy,
                failedStrategies = session.failedStrategies,
                attemptNumber = session.explanationAttempts,
                missingPrerequisite = session.identifiedMissingPrerequisite,
                extraInstruction = extraInstruction
            )

            when (result) {
                is AiTeacherResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isTeacherThinking = false,
                            aiTeacherState = AiTeacherUiState.Success(
                                question = question,
                                response = result.response,
                                adaptiveSession = session.copy(
                                    identifiedMissingPrerequisite = result.response.prerequisiteIdentified
                                        ?: session.identifiedMissingPrerequisite
                                )
                            ),
                            userFeedbackMessage = "इस बार दूसरे तरीके से: ${session.currentStrategy.titleHi}"
                        )
                    }
                }
                is AiTeacherResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isTeacherThinking = false,
                            aiTeacherState = AiTeacherUiState.Error(
                                errorType = result.errorType,
                                userFriendlyMessage = result.userMessage,
                                technicalDetails = result.details
                            )
                        )
                    }
                }
            }
        }
    }

    fun retryLastAiQuestion() {
        val lastQ = _uiState.value.lastAskedQuestion
        if (lastQ.isNotBlank()) {
            askAiTeacherWithQuestion(lastQ)
        }
    }

    fun usePedagogicalFallback() {
        val lastQ = _uiState.value.lastAskedQuestion.ifBlank { "Ohm's Law Intuition" }
        val currentAdaptiveState = _uiState.value.aiTeacherState
        val session = if (currentAdaptiveState is AiTeacherUiState.Success) {
            currentAdaptiveState.adaptiveSession
        } else {
            com.example.model.AdaptiveTeachingSession(question = lastQ)
        }

        _uiState.update {
            it.copy(
                aiTeacherState = AiTeacherUiState.Loading(
                    message = "ऑफ़लाइन गाइड तैयार कर रहा हूँ…",
                    strategy = session.currentStrategy,
                    attempt = session.explanationAttempts
                )
            )
        }
        viewModelScope.launch {
            kotlinx.coroutines.delay(250)
            val profile = _uiState.value.studentProfile
            val fallback = aiTeacherRepository.askAiTeacher(
                question = lastQ,
                studentProfile = profile,
                strategy = session.currentStrategy,
                failedStrategies = session.failedStrategies,
                attemptNumber = session.explanationAttempts,
                missingPrerequisite = session.identifiedMissingPrerequisite,
                extraInstruction = "offline_fallback"
            )
            if (fallback is AiTeacherResult.Success) {
                _uiState.update {
                    it.copy(
                        aiTeacherState = AiTeacherUiState.Success(
                            question = lastQ,
                            response = fallback.response,
                            adaptiveSession = session
                        )
                    )
                }
            }
        }
    }

    fun setAiTeacherFocus(focus: TeacherFocusSection) {
        val currentState = _uiState.value.aiTeacherState
        if (currentState is AiTeacherUiState.Success) {
            _uiState.update {
                it.copy(
                    aiTeacherState = currentState.copy(currentFocusSection = focus)
                )
            }
        }
    }

    fun setStudentCheckAnswer(input: String) {
        val currentState = _uiState.value.aiTeacherState
        if (currentState is AiTeacherUiState.Success) {
            _uiState.update {
                it.copy(
                    aiTeacherState = currentState.copy(studentCheckAnswerInput = input)
                )
            }
        }
    }

    fun revealCheckAnswer() {
        val currentState = _uiState.value.aiTeacherState
        if (currentState is AiTeacherUiState.Success) {
            _uiState.update {
                it.copy(
                    aiTeacherState = currentState.copy(isAnswerRevealed = !currentState.isAnswerRevealed)
                )
            }
        }
    }

    fun requestSimplerExplanation() {
        switchAdaptiveStrategy(com.example.model.ExplanationStrategy.ANALOGY)
    }

    fun requestStillConfused() {
        onStudentStillConfused()
    }

    fun selectSubject(subjectId: String) {
        _uiState.update { it.copy(selectedSubjectId = subjectId) }
    }

    fun selectChapter(chapterId: String) {
        _uiState.update { it.copy(selectedChapterId = chapterId) }
    }

    fun selectConcept(conceptId: String) {
        _uiState.update { it.copy(selectedConceptId = conceptId) }
    }

    // --- Practice & Quizzes ---

    fun selectQuizOption(index: Int) {
        _uiState.update { it.copy(selectedQuizOption = index) }
    }

    fun evaluateQuiz() {
        val current = _uiState.value
        val question = CurriculumRepository.sampleQuiz.getOrNull(current.currentQuizIndex) ?: return
        val isCorrect = current.selectedQuizOption == question.correctIndex

        _uiState.update {
            it.copy(
                isQuizEvaluated = true,
                quizScore = if (isCorrect) it.quizScore + 1 else it.quizScore,
                completedQuizzesCount = it.completedQuizzesCount + 1
            )
        }
    }

    fun nextQuizQuestion() {
        _uiState.update {
            val nextIndex = (it.currentQuizIndex + 1) % CurriculumRepository.sampleQuiz.size
            it.copy(
                currentQuizIndex = nextIndex,
                selectedQuizOption = null,
                isQuizEvaluated = false
            )
        }
    }

    fun toggleStudyPlanItem(itemId: String) {
        _uiState.update { current ->
            val updated = current.studyPlanItems.map {
                if (it.id == itemId) it.copy(isCompleted = !it.isCompleted) else it
            }
            current.copy(studyPlanItems = updated)
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(userFeedbackMessage = null) }
    }

    // --- Phase 3 Educational Knowledge Structure Methods ---

    fun loadSubjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isKnowledgeLoading = true, knowledgeError = null) }
            try {
                val list = knowledgeRepository.getSubjects()
                _uiState.update { it.copy(subjects = list, isKnowledgeLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isKnowledgeLoading = false, knowledgeError = e.message) }
            }
        }
    }

    fun loadChapters(subjectId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isKnowledgeLoading = true, knowledgeError = null) }
            try {
                val subject = _uiState.value.subjects.find { it.subjectId == subjectId }
                    ?: knowledgeRepository.getSubjects().find { it.subjectId == subjectId }
                val chapters = knowledgeRepository.getChapters(subjectId)
                _uiState.update {
                    it.copy(
                        currentSubject = subject,
                        chapters = chapters,
                        isKnowledgeLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isKnowledgeLoading = false, knowledgeError = e.message) }
            }
        }
    }

    fun loadTopics(chapterId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isKnowledgeLoading = true, knowledgeError = null) }
            try {
                val chapter = _uiState.value.chapters.find { it.chapterId == chapterId }
                val topics = knowledgeRepository.getTopics(chapterId)
                _uiState.update {
                    it.copy(
                        currentChapter = chapter,
                        topics = topics,
                        isKnowledgeLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isKnowledgeLoading = false, knowledgeError = e.message) }
            }
        }
    }

    fun loadConcepts(topicId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isKnowledgeLoading = true, knowledgeError = null) }
            try {
                val topic = _uiState.value.topics.find { it.topicId == topicId }
                val concepts = knowledgeRepository.getConcepts(topicId)
                _uiState.update {
                    it.copy(
                        currentTopic = topic,
                        concepts = concepts,
                        isKnowledgeLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isKnowledgeLoading = false, knowledgeError = e.message) }
            }
        }
    }

    fun loadConceptDetail(conceptId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isKnowledgeLoading = true,
                    knowledgeError = null,
                    conceptCheckSelectedOption = null,
                    conceptCheckAnswerSubmitted = false,
                    isConceptCheckCorrect = false
                )
            }
            try {
                val concept = knowledgeRepository.getConcept(conceptId)
                val prereqs = knowledgeRepository.getPrerequisitesForConcept(conceptId)
                val prereqConcepts = if (concept != null && concept.prerequisites.isNotEmpty()) {
                    knowledgeRepository.getConceptsByIds(concept.prerequisites)
                } else emptyList()

                _uiState.update {
                    it.copy(
                        currentConcept = concept,
                        currentConceptPrerequisites = prereqs,
                        prerequisiteConceptEntities = prereqConcepts,
                        isKnowledgeLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isKnowledgeLoading = false, knowledgeError = e.message) }
            }
        }
    }

    fun selectConceptCheckOption(index: Int) {
        _uiState.update { it.copy(conceptCheckSelectedOption = index) }
    }

    fun submitConceptCheck() {
        val concept = _uiState.value.currentConcept ?: return
        val selected = _uiState.value.conceptCheckSelectedOption ?: return
        val isCorrect = selected == concept.understandingCheck.correctOptionIndex

        _uiState.update {
            it.copy(
                conceptCheckAnswerSubmitted = true,
                isConceptCheckCorrect = isCorrect
            )
        }
    }

    fun resetConceptCheck() {
        _uiState.update {
            it.copy(
                conceptCheckSelectedOption = null,
                conceptCheckAnswerSubmitted = false,
                isConceptCheckCorrect = false
            )
        }
    }

    fun markConceptAsReviewed(conceptId: String) {
        _uiState.update {
            val updated = it.reviewedConceptIds + conceptId
            it.copy(
                reviewedConceptIds = updated,
                userFeedbackMessage = "रिवीजन पूर्ण! (Concept marked as reviewed)"
            )
        }
    }
}
