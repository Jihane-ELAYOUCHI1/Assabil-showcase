package com.assabil.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.assabil.R
import com.assabil.ui.screens.*
import com.assabil.ui.theme.AsSabilColors

// ─── ROUTES ───────────────────────────────────────────────────────
sealed class Screen(val route: String) {
    object Hadith  : Screen("hadith")
    object Quran   : Screen("quran")
    object Kibla   : Screen("kibla")
    object SurahDetail : Screen("surah/{surahId}") {
        fun createRoute(id: Int) = "surah/$id"
    }
    object AdkarDetail : Screen("adkar/{sectionId}") {
        fun createRoute(id: String) = "adkar/$id"
    }
    object Settings : Screen("settings")
}

// ─── NAV HOST ─────────────────────────────────────────────────────
@Composable
fun AsSabilNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Quran.route,
        enterTransition = {
            fadeIn(animationSpec = tween(280)) +
            slideInHorizontally(animationSpec = tween(280)) { it / 10 }
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) +
            slideOutHorizontally(animationSpec = tween(200)) { -it / 10 }
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(280)) +
            slideInHorizontally(animationSpec = tween(280)) { -it / 10 }
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) +
            slideOutHorizontally(animationSpec = tween(200)) { it / 10 }
        }
    ) {
        composable(Screen.Hadith.route)  { HadithAdkarScreen(navController) }
        composable(Screen.Quran.route)   { QuranScreen(navController) }
        composable(Screen.Kibla.route)   { KiblaScreen(navController) }
        composable(Screen.SurahDetail.route) { backStack ->
            val surahId = backStack.arguments?.getString("surahId")?.toIntOrNull() ?: 1
            SurahDetailScreen(surahId = surahId, navController = navController)
        }
        composable(Screen.AdkarDetail.route) { backStack ->
            val sectionId = backStack.arguments?.getString("sectionId") ?: "sabah"
            AdkarDetailScreen(sectionId = sectionId, navController = navController)
        }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
    }
}

// ─── DATA FOR BOTTOM NAV ──────────────────────────────────────────
data class BottomNavItem(
    val label: String,
    val route: String,
    val iconRes: Int,
    val iconActiveRes: Int
)

val bottomNavItems = listOf(
    BottomNavItem("Hadith", Screen.Hadith.route, R.drawable.ic_tasbih, R.drawable.ic_tasbih),
    BottomNavItem("Quran",  Screen.Quran.route,  R.drawable.ic_quran,  R.drawable.ic_quran),
    BottomNavItem("Kibla",  Screen.Kibla.route,  R.drawable.ic_compass, R.drawable.ic_compass),
)

// ─── BEAUTIFUL BOTTOM NAV BAR ─────────────────────────────────────
@Composable
fun AsSabilBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val mainRoutes = listOf(Screen.Hadith.route, Screen.Quran.route, Screen.Kibla.route)
    val isMainScreen = mainRoutes.contains(currentRoute)

    AnimatedVisibility(
        visible = isMainScreen,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = AsSabilColors.Espresso.copy(alpha = 0.15f),
                        spotColor = AsSabilColors.Caramel.copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                AsSabilColors.White,
                                AsSabilColors.LightCream
                            )
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    BottomNavItemView(
                        item = item,
                        isSelected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Quran.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(if (isSelected) 52.dp else 44.dp)
                .clip(if (isSelected) RoundedCornerShape(16.dp) else CircleShape)
                .background(
                    if (isSelected)
                        Brush.verticalGradient(
                            colors = listOf(AsSabilColors.Caramel, AsSabilColors.Cinnamon)
                        )
                    else Color.Transparent
                )
        ) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = item.label,
                tint = if (isSelected) AsSabilColors.White else AsSabilColors.TextLight,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) AsSabilColors.Caramel else AsSabilColors.TextLight
        )
    }
}
