package com.example.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.model.AppLanguage
import com.example.ui.pages.*
import com.example.ui.pages.knowledge.*
import com.example.viewmodel.SamjhoViewModel

@Composable
fun SamjhoAppNavHost(
    viewModel: SamjhoViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Show Bottom Bar only on primary top-level tabs
    val isBottomBarVisible = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.Learn.route,
        Screen.AskAI.route,
        Screen.Practice.route,
        Screen.Progress.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav"),
                    tonalElevation = 6.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.route == item.screen.route
                        val label = when (uiState.language) {
                            AppLanguage.HINDI -> item.screen.titleHi
                            else -> item.screen.titleEn
                        }

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentDestination?.route != item.screen.route) {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("nav_item_${item.screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Splash Page
            composable(Screen.Splash.route) {
                SplashPage(
                    uiState = uiState,
                    onNavigateToAuth = {
                        navController.navigate(Screen.Authentication.route)
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Authentication Page (Sign in / Sign up)
            composable(Screen.Authentication.route) {
                AuthenticationPage(
                    viewModel = viewModel,
                    onNavigateToWelcome = {
                        navController.navigate(Screen.OnboardingWelcome.route) {
                            popUpTo(Screen.Authentication.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Authentication.route) { inclusive = true }
                        }
                    }
                )
            }

            // 3. Onboarding Welcome Page
            composable(Screen.OnboardingWelcome.route) {
                OnboardingWelcomePage(
                    uiState = uiState,
                    onContinue = {
                        navController.navigate(Screen.EducationProfile.route)
                    }
                )
            }

            // 4. Education Profile Page
            composable(Screen.EducationProfile.route) {
                EducationProfilePage(
                    viewModel = viewModel,
                    onContinue = {
                        navController.navigate(Screen.LanguagePreference.route)
                    }
                )
            }

            // 5. Language Preference Page
            composable(Screen.LanguagePreference.route) {
                LanguagePreferencePage(
                    viewModel = viewModel,
                    onContinue = {
                        navController.navigate(Screen.LearningPreference.route)
                    }
                )
            }

            // 6. Learning Preference Page
            composable(Screen.LearningPreference.route) {
                LearningPreferencePage(
                    viewModel = viewModel,
                    onFinish = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 7. Home Page
            composable(Screen.Home.route) {
                HomePage(
                    viewModel = viewModel,
                    onNavigateToAskAI = { navController.navigate(Screen.AskAI.route) },
                    onNavigateToLearn = { navController.navigate(Screen.Learn.route) },
                    onNavigateToPractice = { navController.navigate(Screen.Practice.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            // 8. Learn Page (Subjects)
            composable(Screen.Learn.route) {
                LearnPage(
                    viewModel = viewModel,
                    onNavigateToChapters = { subjectId ->
                        navController.navigate(Screen.Chapters.createRoute(subjectId))
                    },
                    onNavigateToConceptDetail = { conceptId ->
                        navController.navigate(Screen.ConceptDetail.createRoute(conceptId))
                    }
                )
            }

            // 8a. Chapters Page (Subject -> Chapters)
            composable(
                route = Screen.Chapters.route,
                arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                ChaptersPage(
                    subjectId = subjectId,
                    viewModel = viewModel,
                    onNavigateToTopics = { chapterId ->
                        navController.navigate(Screen.Topics.createRoute(chapterId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 8b. Topics Page (Chapter -> Topics)
            composable(
                route = Screen.Topics.route,
                arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
            ) { backStackEntry ->
                val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                TopicsPage(
                    chapterId = chapterId,
                    viewModel = viewModel,
                    onNavigateToConcepts = { topicId ->
                        navController.navigate(Screen.Concepts.createRoute(topicId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 8c. Concepts List Page (Topic -> Concepts)
            composable(
                route = Screen.Concepts.route,
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
                ConceptsListPage(
                    topicId = topicId,
                    viewModel = viewModel,
                    onNavigateToConceptDetail = { conceptId ->
                        navController.navigate(Screen.ConceptDetail.createRoute(conceptId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 8d. Concept Detail Page
            composable(
                route = Screen.ConceptDetail.route,
                arguments = listOf(navArgument("conceptId") { type = NavType.StringType })
            ) { backStackEntry ->
                val conceptId = backStackEntry.arguments?.getString("conceptId") ?: ""
                ConceptDetailPage(
                    conceptId = conceptId,
                    viewModel = viewModel,
                    onNavigateToPrerequisite = { prereqConceptId ->
                        navController.navigate(Screen.ConceptDetail.createRoute(prereqConceptId))
                    },
                    onBack = { navController.popBackStack() },
                    onNavigateToAskAi = { question ->
                        viewModel.askAiTeacherWithQuestion(question)
                        navController.navigate(Screen.AIResponse.route)
                    }
                )
            }

            // 9. Ask AI Page
            composable(Screen.AskAI.route) {
                AskAIPage(
                    viewModel = viewModel,
                    onNavigateToAiResponse = {
                        navController.navigate(Screen.AIResponse.route)
                    }
                )
            }

            // 9b. AI Teacher Response Page (Phase 4)
            composable(Screen.AIResponse.route) {
                AIResponsePage(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToNextConcept = { conceptId ->
                        navController.navigate(Screen.ConceptDetail.createRoute(conceptId))
                    }
                )
            }

            // 10. Practice Page
            composable(Screen.Practice.route) {
                PracticePage(
                    viewModel = viewModel,
                    onNavigateToLearn = { navController.navigate(Screen.Learn.route) },
                    onNavigateToAskAI = { navController.navigate(Screen.AskAI.route) }
                )
            }

            // 11. Progress Page
            composable(Screen.Progress.route) {
                ProgressPage(
                    viewModel = viewModel,
                    onNavigateToPractice = { navController.navigate(Screen.Practice.route) },
                    onNavigateToLearn = { navController.navigate(Screen.Learn.route) }
                )
            }

            // 12. Profile Page
            composable(Screen.Profile.route) {
                ProfilePage(
                    viewModel = viewModel,
                    onNavigateToEducationProfile = { navController.navigate(Screen.EducationProfile.route) },
                    onNavigateToLanguagePreference = { navController.navigate(Screen.LanguagePreference.route) },
                    onNavigateToLearningPreference = { navController.navigate(Screen.LearningPreference.route) },
                    onLogout = {
                        navController.navigate(Screen.Splash.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 13. Settings Page
            composable(Screen.Settings.route) {
                SettingsPage(
                    viewModel = viewModel,
                    onReplayOnboarding = { navController.navigate(Screen.Splash.route) },
                    onLogout = {
                        navController.navigate(Screen.Splash.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
