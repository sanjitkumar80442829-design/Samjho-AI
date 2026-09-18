package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BrandTealDark,
    onPrimary = BrandTealOnDark,
    primaryContainer = BrandTealContainerDark,
    onPrimaryContainer = BrandTealOnContainerDark,
    secondary = SlateIndigoDark,
    onSecondary = SlateIndigoOnDark,
    secondaryContainer = SlateIndigoContainerDark,
    onSecondaryContainer = SlateIndigoOnContainerDark,
    tertiary = SparkAmberDark,
    onTertiary = SparkAmberOnDark,
    tertiaryContainer = SparkAmberContainerDark,
    onTertiaryContainer = SparkAmberOnContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BrandTealLight,
    onPrimary = BrandTealOnLight,
    primaryContainer = BrandTealContainerLight,
    onPrimaryContainer = BrandTealOnContainerLight,
    secondary = SlateIndigoLight,
    onSecondary = SlateIndigoOnLight,
    secondaryContainer = SlateIndigoContainerLight,
    onSecondaryContainer = SlateIndigoOnContainerLight,
    tertiary = SparkAmberLight,
    onTertiary = SparkAmberOnLight,
    tertiaryContainer = SparkAmberContainerLight,
    onTertiaryContainer = SparkAmberOnContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
  )

@Composable
fun SamjhoAiTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For branded educational identity, prefer our cohesive palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  SamjhoAiTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

