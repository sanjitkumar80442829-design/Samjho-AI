package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Type-safe navigation routes for Samjho AI
 */
sealed class Screen(val route: String, val titleEn: String = "", val titleHi: String = "") {
    object Splash : Screen("splash", "Welcome", "स्वागत")
    object Authentication : Screen("auth", "Sign In", "लॉग इन")
    object OnboardingWelcome : Screen("onboarding_welcome", "Get Started", "शुरुआत")
    object EducationProfile : Screen("education_profile", "Education Profile", "शैक्षणिक प्रोफ़ाइल")
    object LanguagePreference : Screen("language_preference", "Language", "भाषा प्राथमिकता")
    object LearningPreference : Screen("learning_preference", "Learning Style", "सीखने का तरीका")

    // Main Bottom Navigation tabs
    object Home : Screen("home", "Home", "होम")
    object Learn : Screen("learn", "Learn", "सब्जेक्ट्स")
    object AskAI : Screen("ask_ai", "Ask AI", "पूछें")
    object Practice : Screen("practice", "Practice", "अभ्यास")
    object Progress : Screen("progress", "Progress", "प्रगति")

    // Top Right destinations
    object Profile : Screen("profile", "Profile", "प्रोफ़ाइल")
    object Settings : Screen("settings", "Settings", "सेटिंग्स")

    // Learn Flow Destinations (Phase 3)
    object Chapters : Screen("chapters/{subjectId}", "Chapters", "अध्याय") {
        fun createRoute(subjectId: String) = "chapters/$subjectId"
    }
    object Topics : Screen("topics/{chapterId}", "Topics", "विषय") {
        fun createRoute(chapterId: String) = "topics/$chapterId"
    }
    object Concepts : Screen("concepts/{topicId}", "Concepts", "अवधारणाएं") {
        fun createRoute(topicId: String) = "concepts/$topicId"
    }
    object ConceptDetail : Screen("concept_detail/{conceptId}", "Concept", "अवधारणा") {
        fun createRoute(conceptId: String) = "concept_detail/$conceptId"
    }

    // AI Teacher Response Page (Phase 4)
    object AIResponse : Screen("ai_response", "AI Teacher", "AI शिक्षक")
}

data class BottomNavItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Learn, Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    BottomNavItem(Screen.AskAI, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    BottomNavItem(Screen.Practice, Icons.Filled.Quiz, Icons.Outlined.Quiz),
    BottomNavItem(Screen.Progress, Icons.Filled.ShowChart, Icons.Outlined.ShowChart)
)
