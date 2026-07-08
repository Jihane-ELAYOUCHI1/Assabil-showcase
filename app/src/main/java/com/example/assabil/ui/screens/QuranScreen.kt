package com.assabil.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.assabil.domain.model.Surah
import com.assabil.ui.navigation.Screen
import com.assabil.ui.theme.AsSabilColors
import com.assabil.viewmodel.QuranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    navController: NavController,
    viewModel: QuranViewModel = hiltViewModel()
) {
    val surahs by viewModel.filteredSurahs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lastRead by viewModel.lastRead.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AsSabilColors.Cream)
    ) {
        // ── Top Header ────────────────────────────────────────────
        QuranHeader(
            onSettingsClick = { navController.navigate(Screen.Settings.route) }
        )

        // ── Search Bar ────────────────────────────────────────────
        SearchBarComposable(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // ── Last Read Banner ──────────────────────────────────────
        lastRead?.let { progress ->
            LastReadBanner(
                surahName = surahs.find { it.id == progress.lastSurahId }?.name ?: "",
                ayahNumber = progress.lastAyahNumber,
                onClick = {
                    navController.navigate(Screen.SurahDetail.createRoute(progress.lastSurahId))
                }
            )
        }

        // ── Tab Row ───────────────────────────────────────────────
        val tabs = listOf("Sura", "Page", "Juz", "Hizb")
        var selectedTab by remember { mutableIntStateOf(0) }
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = AsSabilColors.Caramel,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 2.5.dp,
                    color = AsSabilColors.Caramel
                )
            },
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            tabs.forEachIndexed { i, tab ->
                Tab(
                    selected = selectedTab == i,
                    onClick = { selectedTab = i },
                    text = {
                        Text(
                            tab,
                            fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // ── Surah List ────────────────────────────────────────────
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AsSabilColors.Caramel)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(surahs, key = { _, s -> s.id }) { index, surah ->
                    SurahListItem(
                        surah = surah,
                        animDelay = (index * 30).coerceAtMost(300),
                        onClick = { navController.navigate(Screen.SurahDetail.createRoute(surah.id)) }
                    )
                }
                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }
}

// ─── HEADER ───────────────────────────────────────────────────────
@Composable
private fun QuranHeader(onSettingsClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AsSabilColors.Espresso, AsSabilColors.Caramel)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Decorative pattern overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val paint = androidx.compose.ui.graphics.Paint()
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = size.width * 0.6f,
                center = androidx.compose.ui.geometry.Offset(size.width * 1.1f, -size.height * 0.2f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = size.width * 0.4f,
                center = androidx.compose.ui.geometry.Offset(-size.width * 0.1f, size.height * 1.2f)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    "Al-Quran Al-Karim",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "القرآن الكريم",
                    fontSize = 16.sp,
                    color = AsSabilColors.SandStorm.copy(alpha = 0.8f)
                )
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Paramètres",
                    tint = Color.White
                )
            }
        }
    }
}

// ─── SEARCH BAR ───────────────────────────────────────────────────
@Composable
private fun SearchBarComposable(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = AsSabilColors.Espresso.copy(0.08f))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Icon(Icons.Default.Search, null, tint = AsSabilColors.Leafy, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = AsSabilColors.Espresso
            ),
            singleLine = true,
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text("Rechercher une sourate...", fontSize = 14.sp, color = AsSabilColors.TextLight)
                }
                inner()
            },
            modifier = Modifier.weight(1f)
        )
        if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Default.Clear, null, tint = AsSabilColors.TextLight, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─── LAST READ BANNER ─────────────────────────────────────────────
@Composable
private fun LastReadBanner(
    surahName: String,
    ayahNumber: Int,
    onClick: () -> Unit
) {
    if (surahName.isEmpty()) return

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(AsSabilColors.Caramel, AsSabilColors.Cinnamon)
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            Text("Reprendre la lecture", fontSize = 11.sp, color = Color.White.copy(0.75f))
            Text(
                "$surahName · Ayat $ayahNumber",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.White
            )
        }
        Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(28.dp))
    }
}

// ─── SURAH LIST ITEM ──────────────────────────────────────────────
@Composable
private fun SurahListItem(
    surah: Surah,
    animDelay: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animDelay.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it / 3 }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .shadow(3.dp, RoundedCornerShape(16.dp), ambientColor = AsSabilColors.Caramel.copy(0.08f))
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            // Number Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AsSabilColors.SandStorm)
            ) {
                Text(
                    surah.id.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = AsSabilColors.Caramel
                )
            }

            Spacer(Modifier.width(14.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    surah.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = AsSabilColors.Espresso
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${surah.versesCount} Versets  •  ${surah.revelationType}",
                    fontSize = 12.sp,
                    color = AsSabilColors.TextLight
                )
            }

            // Arabic Name
            Text(
                surah.arabicName,
                fontSize = 18.sp,
                color = AsSabilColors.Caramel,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
