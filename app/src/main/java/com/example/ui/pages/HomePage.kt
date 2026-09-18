package com.example.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumRepository
import com.example.model.AppLanguage
import com.example.model.ExplanationMode
import com.example.ui.components.*
import com.example.viewmodel.SamjhoViewModel

/**
 * 6. HomePage
 * Central educational command center.
 * Features:
 * - Greeting
 * - Today's goal
 * - Ask question buttons
 * - Continue learning
 * - Revision section
 * - Recommended learning
 * - Recent activity
 */
@Composable
fun HomePage(
    viewModel: SamjhoViewModel,
    onNavigateToAskAI: () -> Unit,
    onNavigateToLearn: () -> Unit,
    onNavigateToPractice: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_page"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Top Greeting & Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val studentName = uiState.currentUser?.name?.ifBlank { "Learner" } ?: "Learner"
                            val greetingText = when (uiState.language) {
                                AppLanguage.HINDI -> "नमस्ते, $studentName! 🙏"
                                AppLanguage.HINGLISH -> "Namaste, $studentName! 👋"
                                AppLanguage.ENGLISH -> "Welcome, $studentName! 👋"
                            }
                            val classInfo = if (uiState.studentProfile.classOrCourse.isNotBlank()) {
                                "${uiState.studentProfile.classOrCourse} • ${uiState.studentProfile.educationLevel}"
                            } else {
                                "Class 10 • School"
                            }
                            Text(
                                text = greetingText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$classInfo • ${uiState.studentProfile.preferredLanguage}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Profile & Settings shortcut buttons at top-right
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onNavigateToSettings,
                                modifier = Modifier.testTag("top_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = onNavigateToProfile,
                                modifier = Modifier.testTag("top_profile_button")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tagline Banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        tonalElevation = 1.dp,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "“रटने के लिए नहीं, समझने के लिए।”",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 2. Ask Question Buttons (Action Bar)
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = "सवाल पूछें (Ask a Question)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAskButton(
                        label = "Type Doubt",
                        hindiLabel = "लिखकर पूछें",
                        icon = Icons.Default.Edit,
                        onClick = onNavigateToAskAI,
                        modifier = Modifier.weight(1f)
                    )
                    QuickAskButton(
                        label = "Photo Doubt",
                        hindiLabel = "फ़ोटो खींचें",
                        icon = Icons.Default.CameraAlt,
                        onClick = {
                            viewModel.attachImage("Question from book / copy")
                            onNavigateToAskAI()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAskButton(
                        label = "Voice Doubt",
                        hindiLabel = "बोलकर पूछें",
                        icon = Icons.Default.Mic,
                        onClick = {
                            viewModel.toggleVoiceRecording()
                            onNavigateToAskAI()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Today's Goal
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "आज का लक्ष्य (Today's Goal)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val completedCount = uiState.studyPlanItems.count { it.isCompleted }
                    Text(
                        text = "$completedCount/${uiState.studyPlanItems.size} Done",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                uiState.studyPlanItems.take(2).forEach { item ->
                    StudyPlanCard(
                        item = item,
                        onToggle = { viewModel.toggleStudyPlanItem(item.id) },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }

        // 4. Continue Learning Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = "पढ़ाई जारी रखें (Continue Learning)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onNavigateToLearn)
                        .testTag("continue_learning_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "Physics • Electricity",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "Topic 1 of 4",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Ohm's Law: Potential Difference vs Current",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ओम का नियम: विभव और धारा का सहज संबंध",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = onNavigateToLearn,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("जारी रखें (Resume)", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }

        // 5. Revision Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = "दोहराव सत्र (Revision Section)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                CurriculumRepository.revisionItems.take(1).forEach { rev ->
                    RevisionCard(
                        revision = rev,
                        onRevise = onNavigateToPractice,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }

        // 6. Recommended Learning
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = "सुझाई गई संकल्पनाएँ (Recommended Learning)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                CurriculumRepository.concepts.take(2).forEach { concept ->
                    ConceptCard(
                        concept = concept,
                        onClick = {
                            viewModel.selectConcept(concept.id)
                            onNavigateToLearn()
                        },
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        }

        // 7. Recent Activity (Realistic baseline empty state - no fake progress)
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = "हाल की गतिविधि (Recent Activity)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (uiState.completedQuizzesCount == 0 && uiState.submittedDoubts.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.HistoryEdu,
                        title = "कोई पिछली गतिविधि दर्ज नहीं है",
                        description = "आज अभी तक कोई क्विज़ या सवाल नहीं पूछा गया है। सीखने की शुरुआत करने के लिए ऊपर दिए गए किसी भी विषय को चुनें।",
                        actionText = "प्रश्नोत्तरी अभ्यास करें (Try Quiz)",
                        onActionClick = onNavigateToPractice
                    )
                } else {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "आज की गतिविधि:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (uiState.completedQuizzesCount > 0) {
                                Text(
                                    text = "• ${uiState.completedQuizzesCount} अभ्यास प्रश्न हल किए (Accuracy: ${uiState.quizScore * 100 / maxOf(uiState.completedQuizzesCount, 1)}%)",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (uiState.submittedDoubts.isNotEmpty()) {
                                Text(
                                    text = "• ${uiState.submittedDoubts.size} सवाल पूछे गए",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAskButton(
    label: String,
    hindiLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Text(
                text = hindiLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
