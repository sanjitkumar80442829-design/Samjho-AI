package com.example.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProfileOptions
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.viewmodel.AppUiState
import com.example.viewmodel.SamjhoViewModel

/**
 * 1. SplashPage
 * - Checks if user is returning & authenticated -> directly to Home!
 * - If not logged in -> navigate to Authentication.
 */
@Composable
fun SplashPage(
    uiState: AppUiState,
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-direct returning logged in users whose profile is complete
    LaunchedEffect(uiState.isAuthenticated, uiState.isOnboardingComplete) {
        if (uiState.isAuthenticated && uiState.isOnboardingComplete) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(24.dp)
            .testTag("splash_page")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Hero Brand Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Samjho AI Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Samjho AI",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "“रटने के लिए नहीं, समझने के लिए।”",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "India's Intuition-First Learning Companion\nरटना छोड़ो, तर्क समझो।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    text = if (uiState.isAuthenticated) "डैशबोर्ड पर जाएँ (Open Home)" else "लॉग इन / शुरू करें (Get Started)",
                    onClick = {
                        if (uiState.isAuthenticated && uiState.isOnboardingComplete) {
                            onNavigateToHome()
                        } else {
                            onNavigateToAuth()
                        }
                    },
                    icon = Icons.Default.ArrowForward,
                    testTag = "splash_start_button"
                )

                if (uiState.isAuthenticated) {
                    SecondaryButton(
                        text = "नया खाता जोड़ें (Switch Account)",
                        onClick = onNavigateToAuth,
                        testTag = "splash_switch_account_button"
                    )
                }
            }
        }
    }
}

/**
 * 2. OnboardingWelcomePage
 * Welcomes user post-authentication and explains pedagogical pillars.
 */
@Composable
fun OnboardingWelcomePage(
    uiState: AppUiState,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val studentName = uiState.currentUser?.name?.ifBlank { "विद्यार्थी" } ?: "विद्यार्थी"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("onboarding_welcome_page"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "स्वागत है, $studentName! 👋",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Samjho AI में आपका स्वागत है",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3 Core Pillars
            val pillars = listOf(
                Triple(
                    Icons.Default.Psychology,
                    "First-Principles Intuition (मूल तर्क)",
                    "हम फ़ॉर्मूला रटाते नहीं, यह समझाते हैं कि प्रकृति और विज्ञान में चीजें ऐसे क्यों काम करती हैं।"
                ),
                Triple(
                    Icons.Default.Translate,
                    "Natural Bilingual Clarity (हिंदी + हिंग्लिश + English)",
                    "अपनी सहज भाषा में सवाल पूछें और समझें। भाषा कभी भी सीखने में बाधा नहीं बनेगी।"
                ),
                Triple(
                    Icons.Default.CompareArrows,
                    "Relatable Real-World Analogies",
                    "दैनिक जीवन के उदाहरणों से जटिल समीकरण और प्रमेय आसानी से समझें।"
                )
            )

            pillars.forEach { (icon, title, desc) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            PrimaryButton(
                text = "शैक्षणिक प्रोफ़ाइल सेट करें (Set Up Profile)",
                onClick = onContinue,
                icon = Icons.Default.School,
                testTag = "welcome_continue_button"
            )
        }
    }
}

/**
 * 3. EducationProfilePage
 * Configure Education Level options:
 * - School
 * - Polytechnic/Diploma
 * - College
 * - Competitive Exam
 * - Other
 * With dynamic subfields and validation.
 */
@Composable
fun EducationProfilePage(
    viewModel: SamjhoViewModel,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val initial = uiState.studentProfile

    var selectedLevel by remember { mutableStateOf(initial.educationLevel) }
    var selectedClassOrCourse by remember { mutableStateOf(initial.classOrCourse) }
    var selectedBoard by remember { mutableStateOf(initial.board ?: "CBSE") }
    var selectedUniversity by remember { mutableStateOf(initial.university ?: "") }
    var selectedExam by remember { mutableStateOf(initial.exam ?: "JEE Main / Advanced") }
    var selectedSubjects by remember { mutableStateOf(initial.subjects.toSet()) }

    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("education_profile_page")
    ) {
        Text(
            text = "अपनी शैक्षणिक स्थिति चुनें",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Education Level & Curriculum for tailored intuition",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (validationError != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text(
                    text = validationError ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // 1. Education Level Selection
        Text(text = "स्तर (Education Level):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))

        ProfileOptions.educationLevels.forEach { level ->
            SelectableRowCard(
                title = level,
                isSelected = selectedLevel == level,
                onSelect = {
                    selectedLevel = level
                    validationError = null
                    // Reset default course according to level
                    when (level) {
                        "School" -> {
                            selectedClassOrCourse = "Class 10"
                            selectedBoard = "CBSE"
                        }
                        "Polytechnic/Diploma" -> {
                            selectedClassOrCourse = "1st Year (Diploma)"
                            selectedBoard = "State Technical Board"
                        }
                        "College" -> {
                            selectedClassOrCourse = "B.Tech / B.E."
                            selectedUniversity = "State University"
                        }
                        "Competitive Exam" -> {
                            selectedExam = "JEE Main / Advanced"
                            selectedClassOrCourse = "JEE Preparation"
                        }
                        "Other" -> {
                            selectedClassOrCourse = "General Foundation"
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dynamic Subfields based on selected education level
        when (selectedLevel) {
            "School" -> {
                Text(text = "कक्षा (Class):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                WrapChipsGrid(
                    items = ProfileOptions.schoolClasses,
                    selectedItem = selectedClassOrCourse,
                    onSelect = { selectedClassOrCourse = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "बोर्ड (Board):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                ProfileOptions.schoolBoards.forEach { b ->
                    SelectableRowCard(
                        title = b,
                        isSelected = selectedBoard == b,
                        onSelect = { selectedBoard = b },
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "विषय (Subjects of Focus):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                MultiSelectChipsGrid(
                    items = ProfileOptions.schoolSubjects,
                    selectedItems = selectedSubjects,
                    onToggle = { subj ->
                        selectedSubjects = if (selectedSubjects.contains(subj)) selectedSubjects - subj else selectedSubjects + subj
                    }
                )
            }

            "Polytechnic/Diploma" -> {
                Text(text = "डिप्लोमा वर्ष (Year / Semester):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                ProfileOptions.polytechnicYears.forEach { yr ->
                    SelectableRowCard(
                        title = yr,
                        isSelected = selectedClassOrCourse == yr,
                        onSelect = { selectedClassOrCourse = yr },
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = selectedBoard,
                    onValueChange = { selectedBoard = it },
                    label = { Text("तकनीकी बोर्ड / संस्थान (Board or Institute)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "College" -> {
                Text(text = "कोर्स / डिग्री (Course or Degree):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                WrapChipsGrid(
                    items = ProfileOptions.collegeCourses,
                    selectedItem = selectedClassOrCourse,
                    onSelect = { selectedClassOrCourse = it }
                )

                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = selectedUniversity,
                    onValueChange = { selectedUniversity = it },
                    label = { Text("विश्वविद्यालय / कॉलेज का नाम (University or College)") },
                    placeholder = { Text("e.g. Delhi University, AKTU, Pune Univ...") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            "Competitive Exam" -> {
                Text(text = "लक्ष्य परीक्षा (Target Exam):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                ProfileOptions.competitiveExams.forEach { ex ->
                    SelectableRowCard(
                        title = ex,
                        isSelected = selectedExam == ex,
                        onSelect = {
                            selectedExam = ex
                            selectedClassOrCourse = ex
                        },
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            else -> {
                OutlinedTextField(
                    value = selectedClassOrCourse,
                    onValueChange = { selectedClassOrCourse = it },
                    label = { Text("आप क्या सीखना चाहते हैं? (Target Topic / Subject)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryButton(
            text = "आगे बढ़ें: भाषा चुनें (Next: Language)",
            onClick = {
                if (selectedClassOrCourse.isBlank()) {
                    validationError = "कृपया अपनी कक्षा, कोर्स या परीक्षा का चयन करें।"
                    return@PrimaryButton
                }
                viewModel.updateEducationDetails(
                    level = selectedLevel,
                    classOrCourse = selectedClassOrCourse,
                    board = if (selectedLevel == "School" || selectedLevel == "Polytechnic/Diploma") selectedBoard else null,
                    university = if (selectedLevel == "College") selectedUniversity.ifBlank { null } else null,
                    exam = if (selectedLevel == "Competitive Exam") selectedExam else null,
                    subjects = selectedSubjects.toList()
                )
                onContinue()
            },
            icon = Icons.Default.ArrowForward,
            testTag = "save_education_profile_button"
        )
    }
}

/**
 * 4. LanguagePreferencePage
 * Language options:
 * - Hindi
 * - English
 * - Hinglish
 */
@Composable
fun LanguagePreferencePage(
    viewModel: SamjhoViewModel,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedLang by remember { mutableStateOf(uiState.studentProfile.preferredLanguage) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("language_preference_page")
    ) {
        Text(
            text = "आप किस भाषा में समझना चाहते हैं?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Preferred language for conceptual breakdowns",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileOptions.languages.forEach { lang ->
            val isSelected = selectedLang.equals(lang, ignoreCase = true)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { selectedLang = lang }
                    .testTag("lang_option_${lang.lowercase()}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { selectedLang = lang }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (lang) {
                                "Hinglish" -> "Hinglish (हिंग्लिश)"
                                "Hindi" -> "हिंदी (Hindi)"
                                else -> "English"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = when (lang) {
                                "Hinglish" -> "Natural conversational mix of Hindi & English concepts"
                                "Hindi" -> "शुद्ध एवं सहज हिंदी में स्पष्टीकरण"
                                else -> "Clear, intuitive explanations directly in English"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sample explanation preview
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "व्याख्या पूर्वावलोकन (Preview):",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                val previewText = when (selectedLang) {
                    "Hindi" -> "\"विद्युत धारा वास्तव में आवेश के बहने की दर है। जिस प्रकार नल की नली में जल बहता है, उसी प्रकार तार में इलेक्ट्रॉन प्रवाहित होते हैं।\""
                    "English" -> "\"Electric current is essentially the rate of flow of charge. Just like water flowing through a pipe, electrons flow along the wire.\""
                    else -> "\"Current basically charges ka flow rate hai. Jaise pipe me paani flow hota hai, waise hi wire me electrons flow hote hain.\""
                }
                Text(text = previewText, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryButton(
            text = "आगे बढ़ें: सीखने की शैली (Next: Learning Style)",
            onClick = {
                viewModel.updateLanguagePreference(selectedLang)
                onContinue()
            },
            icon = Icons.Default.ArrowForward,
            testTag = "save_language_preference_button"
        )
    }
}

/**
 * 5. LearningPreferencePage
 * Options:
 * Difficulty options:
 * - Concept समझना, English समझना, Formula, Mathematics, याद रखना, Questions solve करना, शुरुआत कहाँ से करें, Other
 * Learning preference:
 * - Simple explanation, Real-life examples, Diagram, Step-by-step, Listening, Practice, Mixed
 */
@Composable
fun LearningPreferencePage(
    viewModel: SamjhoViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val initial = uiState.studentProfile

    var selectedLearningPrefs by remember {
        mutableStateOf(
            if (initial.learningPreferences.isNotEmpty()) initial.learningPreferences.toSet()
            else setOf("Simple explanation", "Real-life examples")
        )
    }

    var selectedDifficulties by remember {
        mutableStateOf(
            if (initial.difficultyAreas.isNotEmpty()) initial.difficultyAreas.toSet()
            else setOf("Concept समझना", "Formula")
        )
    }

    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("learning_preference_page")
    ) {
        Text(
            text = "सीखने की शैली एवं कठिनाइयाँ",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pedagogical preferences & challenge areas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (validationError != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Text(
                    text = validationError ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Learning Preferences (Multi-Select)
        Text(
            text = "आपको कैसे समझना सबसे अच्छा लगता है? (Learning Preferences):",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Select all that apply",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))

        MultiSelectChipsGrid(
            items = ProfileOptions.learningPreferences,
            selectedItems = selectedLearningPrefs,
            onToggle = { item ->
                validationError = null
                selectedLearningPrefs = if (selectedLearningPrefs.contains(item)) {
                    selectedLearningPrefs - item
                } else {
                    selectedLearningPrefs + item
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Difficulty Areas (Multi-Select)
        Text(
            text = "पढ़ाई में मुख्य कठिनाई कहाँ आती है? (Difficulty Areas):",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Select the challenges you face",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))

        MultiSelectChipsGrid(
            items = ProfileOptions.difficultyOptions,
            selectedItems = selectedDifficulties,
            onToggle = { item ->
                validationError = null
                selectedDifficulties = if (selectedDifficulties.contains(item)) {
                    selectedDifficulties - item
                } else {
                    selectedDifficulties + item
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "डैशबोर्ड शुरू करें (Complete & Enter Dashboard)",
            onClick = {
                if (selectedLearningPrefs.isEmpty()) {
                    validationError = "कृपया कम से कम एक सीखने की शैली चुनें।"
                    return@PrimaryButton
                }
                viewModel.updateLearningAndDifficulty(
                    learningPrefs = selectedLearningPrefs.toList(),
                    difficulties = selectedDifficulties.toList(),
                    onComplete = onFinish
                )
            },
            icon = Icons.Default.Check,
            testTag = "finish_onboarding_button"
        )
    }
}

// Reusable Multi-Select Chips Grid
@Composable
private fun MultiSelectChipsGrid(
    items: List<String>,
    selectedItems: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { item ->
                    val isSelected = selectedItems.contains(item)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onToggle(item) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// Single Select Wrap Chips Grid
@Composable
private fun WrapChipsGrid(
    items: List<String>,
    selectedItem: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(3).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { item ->
                    val isSelected = selectedItem == item
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelect(item) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                // Fill empty slots in row
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SelectableRowCard(
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.5.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onSelect)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
