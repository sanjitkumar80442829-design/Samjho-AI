package com.example.ui.pages

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiErrorType
import com.example.model.AiTeacherUiState
import com.example.model.StructuredAiTeacherResponse
import com.example.model.TeacherFocusSection
import com.example.viewmodel.SamjhoViewModel

/**
 * AIResponsePage:
 * Displays structured educational guidance from the AI Teacher.
 *
 * Screen sections:
 * 1. Question
 * 2. Concept
 * 3. Easy Explanation
 * 4. Example
 * 5. Understanding Check
 * 6. Next Step
 *
 * Interactive action buttons:
 * - "और आसान" (Simpler intuition)
 * - "Example" (Focus/generate everyday life analogy)
 * - "Step-by-step" (Step-by-step logical sequence)
 * - "Exam में कैसे लिखें?" (Exam answer structure)
 * - "मुझे अभी भी समझ नहीं आया" (Still confused fallback breakdown)
 *
 * Loading states:
 * - "सवाल समझ रहा हूँ…"
 * - "सबसे आसान तरीका खोज रहा हूँ…"
 * - "आपके स्तर के अनुसार उदाहरण तैयार कर रहा हूँ…"
 *
 * Error handling:
 * - API Error, Timeout, No Internet with retry options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIResponsePage(
    viewModel: SamjhoViewModel,
    onBack: () -> Unit,
    onNavigateToNextConcept: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val aiState = uiState.aiTeacherState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI शिक्षक (Teacher)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "रटने के लिए नहीं, समझने के लिए",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("ai_response_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiState.studentProfile.preferredLanguage.ifBlank { "Hinglish" },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("ai_response_screen")
        ) {
            when (aiState) {
                is AiTeacherUiState.Idle -> {
                    AiEmptyOrIdleView(
                        onAskSample = { sampleQ ->
                            viewModel.askAiTeacherWithQuestion(sampleQ)
                        }
                    )
                }

                is AiTeacherUiState.Loading -> {
                    AiLoadingView(
                        message = aiState.message,
                        strategy = aiState.strategy,
                        attempt = aiState.attempt
                    )
                }

                is AiTeacherUiState.Error -> {
                    AiErrorView(
                        errorType = aiState.errorType,
                        userMessage = aiState.userFriendlyMessage,
                        onRetry = { viewModel.retryLastAiQuestion() },
                        onUseFallback = { viewModel.usePedagogicalFallback() }
                    )
                }

                is AiTeacherUiState.Success -> {
                    AiSuccessResponseContent(
                        question = aiState.question,
                        response = aiState.response,
                        adaptiveSession = aiState.adaptiveSession,
                        currentFocus = aiState.currentFocusSection,
                        studentAnswerInput = aiState.studentCheckAnswerInput,
                        isAnswerRevealed = aiState.isAnswerRevealed,
                        isSimplifiedMore = aiState.isSimplifiedMore,
                        onSelectFocus = { viewModel.setAiTeacherFocus(it) },
                        onAnswerInputChanged = { viewModel.setStudentCheckAnswer(it) },
                        onRevealAnswer = { viewModel.revealCheckAnswer() },
                        onRequestSimpler = { viewModel.requestSimplerExplanation() },
                        onRequestStillConfused = { viewModel.onStudentStillConfused() },
                        onStudentUnderstood = { viewModel.onStudentUnderstood() },
                        onSelectStrategy = { viewModel.switchAdaptiveStrategy(it) },
                        onReturnToOriginalConcept = { viewModel.returnToOriginalConceptAfterPrerequisite() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AiSuccessResponseContent(
    question: String,
    response: StructuredAiTeacherResponse,
    adaptiveSession: com.example.model.AdaptiveTeachingSession,
    currentFocus: TeacherFocusSection,
    studentAnswerInput: String,
    isAnswerRevealed: Boolean,
    isSimplifiedMore: Boolean,
    onSelectFocus: (TeacherFocusSection) -> Unit,
    onAnswerInputChanged: (String) -> Unit,
    onRevealAnswer: () -> Unit,
    onRequestSimpler: () -> Unit,
    onRequestStillConfused: () -> Unit,
    onStudentUnderstood: () -> Unit,
    onSelectStrategy: (com.example.model.ExplanationStrategy) -> Unit,
    onReturnToOriginalConcept: () -> Unit
) {
    val currentStrategy = adaptiveSession.currentStrategy

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phase 5: Adaptive Strategy Active Banner & Attempt Counter
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (currentStrategy) {
                        com.example.model.ExplanationStrategy.PREREQUISITE -> Color(0xFFFFF3E0)
                        com.example.model.ExplanationStrategy.VISUAL -> Color(0xFFEDE7F6)
                        com.example.model.ExplanationStrategy.ANALOGY -> Color(0xFFE3F2FD)
                        com.example.model.ExplanationStrategy.REAL_LIFE_EXAMPLE -> Color(0xFFE8F5E9)
                        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.5.dp,
                    when (currentStrategy) {
                        com.example.model.ExplanationStrategy.PREREQUISITE -> Color(0xFFFF9800)
                        com.example.model.ExplanationStrategy.VISUAL -> Color(0xFF7E57C2)
                        com.example.model.ExplanationStrategy.ANALOGY -> Color(0xFF2196F3)
                        com.example.model.ExplanationStrategy.REAL_LIFE_EXAMPLE -> Color(0xFF4CAF50)
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    }
                ),
                modifier = Modifier.fillMaxWidth().testTag("adaptive_strategy_banner")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentStrategy.iconEmoji,
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "इस बार: ${currentStrategy.titleHi}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentStrategy.descriptionHi,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "प्रयास ${adaptiveSession.explanationAttempts}/6",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    // Phase 5 Non-shaming encouragement message
                    val encouragement = response.encouragementMessage.ifBlank {
                        if (adaptiveSession.explanationAttempts > 1) {
                            "कोई बात नहीं! इसे और basic तरीके से देखते हैं।"
                        } else {
                            "चिंता न करें, हम इसे रटने के बजाय गहराई से समझेंगे।"
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = encouragement,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // If Prerequisite strategy is teaching foundation first
                    if (adaptiveSession.isTeachingPrerequisiteFirst || currentStrategy == com.example.model.ExplanationStrategy.PREREQUISITE) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFF8E1),
                            border = BorderStroke(1.dp, Color(0xFFFFB300)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Layers,
                                        contentDescription = null,
                                        tint = Color(0xFFF57C00),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "बुनियादी पूर्व-शर्त (Prerequisite Diagnosis):",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = response.prerequisiteIdentified
                                        ?: adaptiveSession.identifiedMissingPrerequisite
                                        ?: "बुनियादी सिद्धांत",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF4E342E)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onReturnToOriginalConcept,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("return_to_concept_button")
                                ) {
                                    Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("नींव समझ ली? अब मुख्य संकल्पना पर लौटें")
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. Question Section
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("ai_response_question_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QuestionMark,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "आपका सवाल (Your Question)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 2. Concept Header
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("ai_response_concept_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "मूल संकल्पना (Core Concept)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (response.difficulty.lowercase()) {
                                "easy" -> Color(0xFFE8F5E9)
                                "hard" -> Color(0xFFFFEBEE)
                                else -> Color(0xFFFFF3E0)
                            }
                        ) {
                            Text(
                                text = response.difficulty,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (response.difficulty.lowercase()) {
                                    "easy" -> Color(0xFF2E7D32)
                                    "hard" -> Color(0xFFC62828)
                                    else -> Color(0xFFEF6C00)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = response.concept.ifBlank { "Conceptual Intuition" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (response.subject.isNotBlank() || response.chapter.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = listOfNotNull(
                                response.subject.takeIf { it.isNotBlank() },
                                response.chapter.takeIf { it.isNotBlank() },
                                response.topic.takeIf { it.isNotBlank() }
                            ).joinToString(" › "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (response.shortAnswer.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = response.shortAnswer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // Prerequisites chips if any
                    if (response.prerequisites.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "पहले यह समझें (Prerequisites):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            response.prerequisites.take(3).forEach { prereq ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = prereq,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Phase 5: Strategy Switcher Bar (All 6 Pedagogical Angles)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "शिक्षण का तरीका (Explanation Strategies):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "बदलने के लिए टैप करें",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable or Multi-row Strategy Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    com.example.model.ExplanationStrategy.values().take(3).forEach { strat ->
                        val isCurrent = strat == currentStrategy
                        val isFailed = adaptiveSession.failedStrategies.contains(strat)
                        Surface(
                            onClick = { onSelectStrategy(strat) },
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isFailed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                1.dp,
                                if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.weight(1f).testTag("strategy_chip_${strat.key}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(strat.iconEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = strat.titleHi.split(" ").first(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    com.example.model.ExplanationStrategy.values().drop(3).forEach { strat ->
                        val isCurrent = strat == currentStrategy
                        val isFailed = adaptiveSession.failedStrategies.contains(strat)
                        Surface(
                            onClick = { onSelectStrategy(strat) },
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isFailed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                1.dp,
                                if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.weight(1f).testTag("strategy_chip_${strat.key}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(strat.iconEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = strat.titleHi.split(" ").first(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive Focus Buttons Row:
        // "और आसान", "Example", "Step-by-step", "Exam में कैसे लिखें?", "मुझे अभी भी समझ नहीं आया"
        item {
            Column {
                Text(
                    text = "शिक्षक से और पूछें (Adaptive Tutor Controls):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TeacherActionChip(
                        label = "और आसान",
                        icon = Icons.Default.SentimentSatisfiedAlt,
                        isSelected = isSimplifiedMore || currentFocus == TeacherFocusSection.SIMPLER_INTUITION,
                        onClick = onRequestSimpler,
                        modifier = Modifier.weight(1f).testTag("button_more_easy")
                    )

                    TeacherActionChip(
                        label = "Example",
                        icon = Icons.Default.Science,
                        isSelected = currentFocus == TeacherFocusSection.EXAMPLE,
                        onClick = { onSelectFocus(TeacherFocusSection.EXAMPLE) },
                        modifier = Modifier.weight(1f).testTag("button_example")
                    )

                    TeacherActionChip(
                        label = "Step-by-step",
                        icon = Icons.Default.FormatListNumbered,
                        isSelected = currentFocus == TeacherFocusSection.STEP_BY_STEP,
                        onClick = { onSelectFocus(TeacherFocusSection.STEP_BY_STEP) },
                        modifier = Modifier.weight(1f).testTag("button_step_by_step")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TeacherActionChip(
                        label = "Exam में कैसे लिखें?",
                        icon = Icons.Default.EditNote,
                        isSelected = currentFocus == TeacherFocusSection.EXAM_ANSWER,
                        onClick = { onSelectFocus(TeacherFocusSection.EXAM_ANSWER) },
                        modifier = Modifier.weight(1.2f).testTag("button_exam_answer")
                    )

                    TeacherActionChip(
                        label = "मुझे समझ नहीं आया",
                        icon = Icons.Default.HelpCenter,
                        isSelected = false,
                        isWarning = true,
                        onClick = onRequestStillConfused,
                        modifier = Modifier.weight(1.3f).testTag("button_still_confused")
                    )
                }
            }
        }

        // Phase 5: Student Understanding Feedback Card ("क्या यह तरीका समझ आया?")
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (adaptiveSession.isUnderstoodConfirmed)
                        Color(0xFFE8F5E9)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.5.dp,
                    if (adaptiveSession.isUnderstoodConfirmed)
                        Color(0xFF4CAF50)
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("student_understanding_feedback_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (adaptiveSession.isUnderstoodConfirmed) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "बधाई! आपने इसे समझ लिया (Understood)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "सफल शिक्षण विधि: ${adaptiveSession.currentStrategy.titleHi}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1B5E20)
                        )
                    } else {
                        Text(
                            text = "क्या आपको यह तरीका समझ आया?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ईमानदारी से बताएं — अगर नहीं आया, तो AI शिक्षक तुरंत दूसरा तरीका अपनाएगा।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // "हाँ, समझ आया" Button
                            Button(
                                onClick = onStudentUnderstood,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp).testTag("button_understood_yes")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "हाँ, समझ आया",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            // "❓ मुझे अभी भी समझ नहीं आया" Button
                            OutlinedButton(
                                onClick = onRequestStillConfused,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.2f).height(48.dp).testTag("button_still_confused_main")
                            ) {
                                Text(
                                    text = "❓ अभी भी नहीं आया",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Easy Explanation Section
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.5.dp,
                    if (currentFocus == TeacherFocusSection.EASY_EXPLANATION || isSimplifiedMore)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("ai_response_easy_explanation")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isSimplifiedMore) "अति-सरल समझ (Ultra-Simple Intuition)" else "सहज स्पष्टीकरण (Easy Explanation)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = response.easyExplanation.ifBlank { "Understanding the underlying intuition..." },
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Visual prompt if recommended
                    if (response.visualNeeded && response.visualDescription.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "मानसिक चित्र / आरेख (${response.visualType})",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = response.visualDescription,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Example Section
        if (response.example.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.5.dp,
                        if (currentFocus == TeacherFocusSection.EXAMPLE)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("ai_response_example_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiObjects,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "रोजमर्रा का उदाहरण (Real-World Example)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = response.example,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Step-by-Step sequence (Visible if highlighted or stepByStep is available)
        if (response.stepByStep.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.5.dp,
                        if (currentFocus == TeacherFocusSection.STEP_BY_STEP)
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("ai_response_step_by_step_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatListNumbered,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "क्रमवार समझ (Step-by-Step Breakdown)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        response.stepByStep.forEachIndexed { idx, step ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Exam Answer structure if student requested or available
        if (response.examAnswer.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.5.dp,
                        if (currentFocus == TeacherFocusSection.EXAM_ANSWER)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("ai_response_exam_answer_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AssignmentTurnedIn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exam में कैसे लिखें? (Exam Structure)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = response.examAnswer,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Common mistakes students make
        if (response.commonMistakes.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1).copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("ai_response_common_mistakes_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = Color(0xFFF57C00),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "विद्यार्थी यहाँ गलती करते हैं (Common Mistakes)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        response.commonMistakes.forEach { mistake ->
                            Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "✕",
                                    color = Color(0xFFD32F2F),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = mistake,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF4E342E)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Understanding Check Section
        if (response.understandingCheck.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("ai_response_understanding_check")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "समझ की परख (Understanding Check)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = response.understandingCheck,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Student answer input
                        OutlinedTextField(
                            value = studentAnswerInput,
                            onValueChange = onAnswerInputChanged,
                            placeholder = { Text("अपना उत्तर यहाँ सोचकर लिखें...", fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("understanding_check_input"),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onRevealAnswer,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("reveal_check_answer_button")
                            ) {
                                Text(if (isAnswerRevealed) "उत्तर छिपाएँ" else "सही उत्तर देखें (Check Answer)")
                            }

                            if (studentAnswerInput.isNotBlank() && !isAnswerRevealed) {
                                Text(
                                    text = "विचार दर्ज हुआ ✓",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Answer explanation revealed
                        AnimatedVisibility(visible = isAnswerRevealed) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFE8F5E9),
                                    border = BorderStroke(1.dp, Color(0xFF81C784)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "अपेक्षित उत्तर (Expected Answer):",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = response.expectedAnswer.ifBlank { "शानदार! यदि आपने सही तर्क लगाया तो आपका आधार मजबूत है।" },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF1B5E20)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Next Step Section
        if (response.nextStep.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("ai_response_next_step_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "अगला कदम (Next Step)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = response.nextStep,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherActionChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isWarning: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isWarning -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            1.dp,
            when {
                isSelected -> MaterialTheme.colorScheme.primary
                isWarning -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            }
        ),
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isWarning -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isWarning -> MaterialTheme.colorScheme.onErrorContainer
                    else -> MaterialTheme.colorScheme.onSurface
                },
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AiLoadingView(
    message: String,
    strategy: com.example.model.ExplanationStrategy? = null,
    attempt: Int = 1
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag("ai_loading_view"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(52.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (strategy != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(strategy.iconEmoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "प्रयास $attempt • ${strategy.titleHi}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "AI शिक्षक आपकी कक्षा और भाषा के अनुसार सरलतम व्याख्या तैयार कर रहा है…",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AiErrorView(
    errorType: AiErrorType,
    userMessage: String,
    onRetry: () -> Unit,
    onUseFallback: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag("ai_error_view"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (errorType) {
                    AiErrorType.NO_INTERNET -> Icons.Default.WifiOff
                    AiErrorType.NETWORK_TIMEOUT -> Icons.Default.Timer
                    else -> Icons.Default.ErrorOutline
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = when (errorType) {
                AiErrorType.NO_INTERNET -> "इंटरनेट कनेक्शन नहीं है"
                AiErrorType.NETWORK_TIMEOUT -> "अनुरोध समय समाप्त (Timeout)"
                else -> "AI शिक्षक संपर्क में समस्या"
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = userMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onUseFallback,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("error_offline_mode_button")
            ) {
                Text("ऑफ़लाइन गाइड देखें")
            }

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("error_retry_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("पुनः प्रयास करें")
            }
        }
    }
}

@Composable
private fun AiEmptyOrIdleView(
    onAskSample: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("ai_idle_view"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "AI शिक्षक से पूछें",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "रटने के लिए नहीं, समझने के लिए। कोई भी कठिन प्रश्न पूछें और पाएँ वास्तविक जीवन से जुड़ा स्पष्टीकरण।",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "उदा. प्रश्न आज़माएँ:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))

        listOf(
            "ओम का नियम वास्तव में क्या बताता है और यह कब लागू नहीं होता?",
            "विभवांतर (Voltage) और धारा (Current) में मूल अंतर क्या है?",
            "Differentiation और Integration का वास्तविक भौतिक अर्थ क्या है?"
        ).forEach { sample ->
            Surface(
                onClick = { onAskSample(sample) },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = sample,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
