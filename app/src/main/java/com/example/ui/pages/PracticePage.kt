package com.example.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumRepository
import com.example.ui.components.*
import com.example.viewmodel.SamjhoViewModel

/**
 * 10. PracticePage
 * - Upcoming practice
 * - Interactive Quiz Session
 * - Recent quizzes
 * - Realistic baseline data without fake inflated student stats
 */
@Composable
fun PracticePage(
    viewModel: SamjhoViewModel,
    onNavigateToLearn: () -> Unit,
    onNavigateToAskAI: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val quizQuestions = CurriculumRepository.sampleQuiz
    val currentQuestion = quizQuestions.getOrNull(uiState.currentQuizIndex) ?: quizQuestions.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("practice_page"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp)
            ) {
                Text(
                    text = "अभ्यास एवं प्रश्नोत्तरी (Practice & Quizzes)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Intuition-based concept verification",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Upcoming Practice Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = "आगामी अभ्यास सत्र (Upcoming Practice):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                CurriculumRepository.revisionItems.forEach { rev ->
                    RevisionCard(
                        revision = rev,
                        onRevise = {
                            viewModel.nextQuizQuestion()
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }

        // 2. Active Intuition Quiz Runner
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "संकल्पना जाँच (Active Intuition Check):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Q ${uiState.currentQuizIndex + 1} of ${quizQuestions.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = currentQuestion.conceptTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentQuestion.questionEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentQuestion.questionHi,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quiz options
                        currentQuestion.options.forEachIndexed { idx, option ->
                            QuizOption(
                                index = idx,
                                optionText = option,
                                isSelected = uiState.selectedQuizOption == idx,
                                onSelect = {
                                    if (!uiState.isQuizEvaluated) {
                                        viewModel.selectQuizOption(idx)
                                    }
                                },
                                isEvaluated = uiState.isQuizEvaluated,
                                isCorrect = idx == currentQuestion.correctIndex,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions
                        if (!uiState.isQuizEvaluated) {
                            PrimaryButton(
                                text = "उत्तर जाँचें (Check Answer)",
                                onClick = { viewModel.evaluateQuiz() },
                                enabled = uiState.selectedQuizOption != null,
                                testTag = "quiz_check_button"
                            )
                        } else {
                            // Explanation box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "मूल तर्क व्याख्या (Intuition Behind Answer):",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentQuestion.intuitionExplanation,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.onDoubtTextChanged("मुझे इस प्रश्न का मूल तर्क विस्तार से समझाएं: ${currentQuestion.questionEn}")
                                        onNavigateToAskAI()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("AI से पूछें", fontSize = 13.sp)
                                }

                                Button(
                                    onClick = { viewModel.nextQuizQuestion() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("अगला प्रश्न", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Recent Quizzes (Empty state or completed count without fake progress)
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = "हाल के क्विज़ (Recent Quizzes):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.completedQuizzesCount == 0) {
                    EmptyState(
                        icon = Icons.Default.Quiz,
                        title = "कोई पूर्व प्रश्नोत्तरी परिणाम नहीं",
                        description = "जब आप ऊपर दिए गए प्रश्नों का अभ्यास करेंगे, आपके वास्तविक परिणाम और संकल्पना स्पष्टता यहाँ दर्ज होगी।",
                        actionText = "पहला प्रश्न हल करें",
                        onActionClick = {
                            // Focus on active question
                        }
                    )
                } else {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Physics Practice Batch #1",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "${uiState.quizScore}/${uiState.completedQuizzesCount} Correct",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "विद्युत एवं प्रकाश संकल्पना अभ्यास पूरा किया।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
