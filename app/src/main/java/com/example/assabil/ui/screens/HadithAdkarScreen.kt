package com.assabil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.assabil.ui.navigation.Screen
import com.assabil.ui.theme.AsSabilColors
import com.assabil.viewmodel.HadithAdkarViewModel

@Composable
fun HadithAdkarScreen(
    navController: NavController,
    viewModel: HadithAdkarViewModel = hiltViewModel()
) {
    val dailyHadith by viewModel.dailyHadith.collectAsState()
    val tasbihCount by viewModel.tasbihCount.collectAsState()
    val tasbihTarget by viewModel.tasbihTarget.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AsSabilColors.Cream)
    ) {
        // ── Header ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AsSabilColors.Espresso, Color(0xFF6B3A20))
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        "Adkar & Hadith",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Text(
                        "أذكار وأحاديث",
                        fontSize = 14.sp,
                        color = AsSabilColors.SandStorm.copy(0.8f)
                    )
                }
                IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                    Icon(Icons.Default.Settings, null, tint = Color.White)
                }
            }
        }

        // ── Tabs ──────────────────────────────────────────────────
        val tabs = listOf("Adkar", "Tasbih", "40 Hadiths", "Histoires")
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = AsSabilColors.Caramel,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 2.5.dp,
                    color = AsSabilColors.Caramel
                )
            }
        ) {
            tabs.forEachIndexed { i, tab ->
                Tab(
                    selected = selectedTab == i,
                    onClick = { selectedTab = i },
                    text = {
                        Text(
                            tab,
                            fontWeight = if (selectedTab == i) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        // ── Content ───────────────────────────────────────────────
        when (selectedTab) {
            0 -> AdkarTabContent(navController)
            1 -> TasbihTabContent(
                count = tasbihCount,
                target = tasbihTarget,
                onTap = viewModel::incrementTasbih,
                onReset = viewModel::resetTasbih,
                onSetTarget = viewModel::setTasbihTarget
            )
            2 -> NawawiTabContent(viewModel)
            3 -> StoriesTabContent()
        }
    }
}

// ─── ADKAR SECTIONS ───────────────────────────────────────────────
@Composable
private fun AdkarTabContent(navController: NavController) {
    val sections = listOf(
        Triple("sabah",  "Adkar Sabah",   "أذكار الصباح",  "🌅", Color(0xFFE8C07A)),
        Triple("masaa",  "Adkar Masaa",   "أذكار المساء",  "🌙", Color(0xFF9B7BB0)),
        Triple("prayer", "Après Prière",  "أذكار بعد الصلاة", "🕌", AsSabilColors.Cinnamon),
        Triple("sleep",  "Avant Dormir",  "أذكار النوم",   "😴", AsSabilColors.Leafy),
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Choisissez une section",
                fontSize = 13.sp,
                color = AsSabilColors.TextLight,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        items(sections) { (id, title, arabic, emoji, color) ->
            AdkarSectionCard(
                emoji = emoji,
                title = title,
                arabic = arabic,
                accentColor = color,
                onClick = { navController.navigate(Screen.AdkarDetail.createRoute(id)) }
            )
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
private fun AdkarSectionCard(
    emoji: String,
    title: String,
    arabic: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .shadow(4.dp, RoundedCornerShape(18.dp), ambientColor = AsSabilColors.Caramel.copy(0.1f))
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accentColor.copy(0.15f))
        ) {
            Text(emoji, fontSize = 26.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = AsSabilColors.Espresso)
            Spacer(Modifier.height(2.dp))
            Text(arabic, fontSize = 14.sp, color = AsSabilColors.TextLight)
        }
        Icon(Icons.Default.ChevronRight, null, tint = AsSabilColors.Leafy)
    }
}

// ─── TASBIH COUNTER ───────────────────────────────────────────────
@Composable
private fun TasbihTabContent(
    count: Int,
    target: Int,
    onTap: () -> Unit,
    onReset: () -> Unit,
    onSetTarget: (Int) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val progress = if (target > 0) count.toFloat() / target.toFloat() else 0f
    val animProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(400),
        label = "tasbih_progress"
    )

    val scale = remember { Animatable(1f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // ── Dhikr selector ────────────────────────────────────────
        Text("سُبْحَانَ اللّٰهِ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AsSabilColors.Espresso)
        Text("Subhanallah", fontSize = 13.sp, color = AsSabilColors.TextLight)

        Spacer(Modifier.height(36.dp))

        // ── Circular progress + tap area ──────────────────────────
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { animProgress },
                modifier = Modifier.size(220.dp),
                color = AsSabilColors.Caramel,
                trackColor = AsSabilColors.SandStorm,
                strokeWidth = 12.dp
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AsSabilColors.Caramel, AsSabilColors.Espresso)
                        )
                    )
                    .scale(scale.value)
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onTap()
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        count.toString(),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "/ $target",
                        fontSize = 16.sp,
                        color = Color.White.copy(0.6f)
                    )
                }
            }
        }

        Spacer(Modifier.height(36.dp))

        // ── Controls ──────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onReset,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AsSabilColors.Caramel),
                border = BorderStroke(1.5.dp, AsSabilColors.Caramel)
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Réinitialiser")
            }

            // Quick targets
            listOf(33, 100, 99).forEach { t ->
                FilterChip(
                    selected = target == t,
                    onClick = { onSetTarget(t) },
                    label = { Text("$t") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AsSabilColors.Caramel,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // ── Completion message ────────────────────────────────────
        if (count >= target && target > 0) {
            Spacer(Modifier.height(24.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AsSabilColors.SandStorm)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text("🌿", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Mashallah! Dhikr complété.",
                        fontWeight = FontWeight.SemiBold,
                        color = AsSabilColors.Espresso
                    )
                }
            }
        }
    }
}

// ─── 40 HADITHS ───────────────────────────────────────────────────
@Composable
private fun NawawiTabContent(viewModel: HadithAdkarViewModel) {
    val hadiths by viewModel.nawawiHadiths.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(hadiths) { hadith ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Number badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(AsSabilColors.Caramel, AsSabilColors.Cinnamon)
                                    )
                                )
                        ) {
                            Text(hadith.number.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Hadith Nawawi", fontSize = 10.sp, color = AsSabilColors.TextLight)
                            Text(hadith.narrator, fontSize = 12.sp, color = AsSabilColors.Caramel, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(
                            onClick = { viewModel.toggleHadithFavorite(hadith) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                if (hadith.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                null,
                                tint = if (hadith.isFavorite) AsSabilColors.Cinnamon else AsSabilColors.TextLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AsSabilColors.Divider)

                    Text(
                        hadith.frenchText,
                        fontSize = 14.sp,
                        color = AsSabilColors.TextMedium,
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AsSabilColors.SandStorm)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(hadith.grade, fontSize = 10.sp, color = AsSabilColors.Caramel, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(hadith.source, fontSize = 11.sp, color = AsSabilColors.TextLight)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}

// ─── STORIES PLACEHOLDER ──────────────────────────────────────────
@Composable
private fun StoriesTabContent() {
    val prophets = listOf(
        Pair("Adam (AS)", "🌿"), Pair("Nuh (AS)", "🚢"), Pair("Ibrahim (AS)", "🔥"),
        Pair("Musa (AS)", "🌊"), Pair("Issa (AS)", "✨"), Pair("Muhammad (SAW)", "📖")
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Histoires des Prophètes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AsSabilColors.Espresso, modifier = Modifier.padding(vertical = 4.dp))
        }
        items(prophets) { (name, emoji) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .clickable { /* TODO: open story */ }
                    .padding(16.dp)
            ) {
                Text(emoji, fontSize = 28.sp)
                Spacer(Modifier.width(14.dp))
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AsSabilColors.Espresso, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, null, tint = AsSabilColors.Leafy)
            }
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}
