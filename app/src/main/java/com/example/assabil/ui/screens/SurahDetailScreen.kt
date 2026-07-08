package com.assabil.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.assabil.domain.model.Ayah
import com.assabil.domain.model.Surah
import com.assabil.ui.theme.AsSabilColors
import com.assabil.viewmodel.SurahDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    surahId: Int,
    navController: NavController,
    viewModel: SurahDetailViewModel = hiltViewModel()
) {
    val surah by viewModel.surah.collectAsState()
    val ayahs by viewModel.ayahs.collectAsState()
    val audioState by viewModel.audioState.collectAsState()
    val showTranslation by viewModel.showTranslation.collectAsState()
    val showTafsir by viewModel.showTafsir.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(surahId) { viewModel.loadSurah(surahId) }

    Box(modifier = Modifier.fillMaxSize().background(AsSabilColors.Cream)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Header ────────────────────────────────────────────
            SurahDetailHeader(
                surah = surah,
                onBack = { navController.popBackStack() },
                showTranslation = showTranslation,
                showTafsir = showTafsir,
                onToggleTranslation = viewModel::toggleTranslation,
                onToggleTafsir = viewModel::toggleTafsir
            )

            // ── Bismillah Banner ──────────────────────────────────
            if (surahId != 1 && surahId != 9) {
                BismillahBanner()
            }

            // ── Ayahs List ────────────────────────────────────────
            if (isLoading) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AsSabilColors.Caramel)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(ayahs, key = { it.globalNumber }) { ayah ->
                        AyahCard(
                            ayah = ayah,
                            showTranslation = showTranslation,
                            showTafsir = showTafsir,
                            isPlaying = audioState.isPlaying && audioState.currentAyah == ayah.numberInSurah,
                            onPlayAyah = { viewModel.playAyah(ayah.numberInSurah) },
                            onBookmark = { viewModel.toggleBookmark(ayah) },
                            onShare = { viewModel.shareAyah(ayah) }
                        )
                    }
                    item { Spacer(Modifier.height(140.dp)) }
                }
            }
        }

        // ── Audio Player Bar (floating bottom) ────────────────────
        surah?.let {
            AudioPlayerBar(
                surahName = it.name,
                audioState = audioState,
                onPlay = viewModel::playPause,
                onNext = viewModel::nextSurah,
                onPrev = viewModel::prevSurah,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 80.dp)
            )
        }
    }
}

// ─── HEADER ───────────────────────────────────────────────────────
@Composable
private fun SurahDetailHeader(
    surah: Surah?,
    onBack: () -> Unit,
    showTranslation: Boolean,
    showTafsir: Boolean,
    onToggleTranslation: () -> Unit,
    onToggleTafsir: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AsSabilColors.Espresso, AsSabilColors.Cinnamon)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
            Spacer(Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                surah?.let {
                    Text(
                        "${it.id}. ${it.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        "${it.versesCount} versets  •  ${it.revelationType}",
                        fontSize = 12.sp,
                        color = AsSabilColors.SandStorm.copy(0.7f)
                    )
                }
            }
            // Traduction toggle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = showTranslation,
                    onCheckedChange = { onToggleTranslation() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AsSabilColors.White,
                        checkedTrackColor = AsSabilColors.Caramel
                    ),
                    modifier = Modifier.scale(0.75f)
                )
                Text("Trad.", fontSize = 9.sp, color = AsSabilColors.SandStorm)
            }
        }
    }
}

// ─── BISMILLAH ────────────────────────────────────────────────────
@Composable
private fun BismillahBanner() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(AsSabilColors.SandStorm.copy(0.5f))
            .padding(vertical = 20.dp)
    ) {
        Text(
            "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ",
            fontSize = 24.sp,
            color = AsSabilColors.Espresso,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// ─── AYAH CARD ────────────────────────────────────────────────────
@Composable
private fun AyahCard(
    ayah: Ayah,
    showTranslation: Boolean,
    showTafsir: Boolean,
    isPlaying: Boolean,
    onPlayAyah: () -> Unit,
    onBookmark: () -> Unit,
    onShare: () -> Unit
) {
    var bookmarked by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) AsSabilColors.SandStorm else Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Ayah number + actions row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Numbered badge (Islamic geometric style)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(AsSabilColors.Caramel, AsSabilColors.Espresso)
                            )
                        )
                ) {
                    Text(
                        ayah.numberInSurah.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(onClick = onPlayAyah, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            null,
                            tint = AsSabilColors.Caramel,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(
                        onClick = { bookmarked = !bookmarked; onBookmark() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            null,
                            tint = if (bookmarked) AsSabilColors.Cinnamon else AsSabilColors.TextLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Share,
                            null,
                            tint = AsSabilColors.TextLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Arabic Text (RTL)
            Text(
                ayah.arabicText,
                fontSize = 22.sp,
                color = AsSabilColors.Espresso,
                fontWeight = FontWeight.Medium,
                lineHeight = 44.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            // Translation
            AnimatedVisibility(visible = showTranslation && ayah.frenchTranslation.isNotEmpty()) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = AsSabilColors.Divider
                    )
                    Text(
                        "Sahih International",
                        fontSize = 10.sp,
                        color = AsSabilColors.TextLight,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        ayah.frenchTranslation,
                        fontSize = 14.sp,
                        color = AsSabilColors.TextMedium,
                        lineHeight = 22.sp
                    )
                }
            }

            // Sajda indicator
            if (ayah.sajda) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = AsSabilColors.Caramel, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Sajda", fontSize = 11.sp, color = AsSabilColors.Caramel, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─── AUDIO PLAYER BAR ─────────────────────────────────────────────
@Composable
private fun AudioPlayerBar(
    surahName: String,
    audioState: com.assabil.domain.model.AudioState,
    onPlay: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AsSabilColors.Espresso),
        elevation = CardDefaults.cardElevation(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("En lecture", fontSize = 10.sp, color = AsSabilColors.Leafy)
                    Text(surahName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPrev) {
                        Icon(Icons.Default.SkipPrevious, null, tint = AsSabilColors.SandStorm, modifier = Modifier.size(26.dp))
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(AsSabilColors.Caramel)
                            .clickable(onClick = onPlay)
                    ) {
                        Icon(
                            if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onNext) {
                        Icon(Icons.Default.SkipNext, null, tint = AsSabilColors.SandStorm, modifier = Modifier.size(26.dp))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { audioState.progress },
                modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                color = AsSabilColors.Caramel,
                trackColor = Color.White.copy(0.2f)
            )
        }
    }
}
