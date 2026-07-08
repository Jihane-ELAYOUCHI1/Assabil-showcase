package com.assabil.ui.screens

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
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.assabil.domain.model.Adkar
import com.assabil.ui.theme.AsSabilColors
import com.assabil.viewmodel.HadithAdkarViewModel

// ─── SETTINGS SCREEN ──────────────────────────────────────────────
@Composable
fun SettingsScreen(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("Français") }
    var fontSize by remember { mutableFloatStateOf(18f) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AsSabilColors.Cream)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AsSabilColors.Espresso, AsSabilColors.Caramel)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text("Paramètres", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Appearance ────────────────────────────────────────
            SettingsSection(title = "Apparence") {
                SettingsToggleRow(
                    icon = Icons.Default.DarkMode,
                    label = "Mode Sombre",
                    checked = isDarkMode,
                    onCheckedChange = { isDarkMode = it }
                )
                HorizontalDivider(color = AsSabilColors.Divider)
                SettingsSliderRow(
                    icon = Icons.Default.TextFields,
                    label = "Taille Police Arabe",
                    value = fontSize,
                    range = 14f..32f,
                    onValueChange = { fontSize = it },
                    valueLabel = "${fontSize.toInt()}sp"
                )
            }

            // ── Language ──────────────────────────────────────────
            SettingsSection(title = "Langue & Traduction") {
                listOf("Français", "English", "العربية").forEach { lang ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { language = lang }
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Icon(Icons.Default.Language, null, tint = AsSabilColors.Leafy, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(lang, modifier = Modifier.weight(1f), color = AsSabilColors.Espresso)
                        if (language == lang) {
                            Icon(Icons.Default.Check, null, tint = AsSabilColors.Caramel, modifier = Modifier.size(18.dp))
                        }
                    }
                    if (lang != "العربية") HorizontalDivider(color = AsSabilColors.Divider)
                }
            }

            // ── Notifications ─────────────────────────────────────
            SettingsSection(title = "Notifications") {
                SettingsToggleRow(
                    icon = Icons.Default.Notifications,
                    label = "Adkar Matin & Soir",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                HorizontalDivider(color = AsSabilColors.Divider)
                SettingsToggleRow(
                    icon = Icons.Default.Book,
                    label = "Hadith du Jour",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }

            // ── About ─────────────────────────────────────────────
            SettingsSection(title = "À Propos") {
                SettingsInfoRow(icon = Icons.Default.Info, label = "Version", value = "1.0.0")
                HorizontalDivider(color = AsSabilColors.Divider)
                SettingsInfoRow(icon = Icons.Default.Favorite, label = "Développé avec ❤️ pour la Oumma", value = "")
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AsSabilColors.TextLight,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Icon(icon, null, tint = AsSabilColors.Leafy, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f), color = AsSabilColors.Espresso, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AsSabilColors.Caramel
            )
        )
    }
}

@Composable
private fun SettingsSliderRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    valueLabel: String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = AsSabilColors.Leafy, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, modifier = Modifier.weight(1f), color = AsSabilColors.Espresso, fontSize = 14.sp)
            Text(valueLabel, color = AsSabilColors.Caramel, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = AsSabilColors.Caramel,
                activeTrackColor = AsSabilColors.Caramel,
                inactiveTrackColor = AsSabilColors.SandStorm
            )
        )
    }
}

@Composable
private fun SettingsInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Icon(icon, null, tint = AsSabilColors.Leafy, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f), color = AsSabilColors.Espresso, fontSize = 14.sp)
        if (value.isNotEmpty()) Text(value, color = AsSabilColors.TextLight, fontSize = 13.sp)
    }
}

// ─── ADKAR DETAIL SCREEN ──────────────────────────────────────────
@Composable
fun AdkarDetailScreen(
    sectionId: String,
    navController: NavController,
    viewModel: HadithAdkarViewModel = hiltViewModel()
) {
    val adkarList by viewModel.getAdkarForSection(sectionId)
        .collectAsState(initial = emptyList())

    val sectionTitles = mapOf(
        "sabah"  to Pair("Adkar Sabah",   "🌅"),
        "masaa"  to Pair("Adkar Masaa",   "🌙"),
        "prayer" to Pair("Après Prière",  "🕌"),
        "sleep"  to Pair("Avant Dormir",  "😴")
    )
    val (title, emoji) = sectionTitles[sectionId] ?: Pair("Adkar", "📿")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AsSabilColors.Cream)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AsSabilColors.Espresso, AsSabilColors.Caramel)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Text("$emoji $title", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            }
        }

        // Adkar Cards (demo or loaded)
        val demoAdkar = getDemoAdkar(sectionId)
        val items = if (adkarList.isEmpty()) demoAdkar else adkarList

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items.size) { index ->
                val adkar = items[index]
                AdkarItemCard(
                    adkar = adkar,
                    onIncrement = { viewModel.incrementAdkarCount(adkar) }
                )
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
private fun AdkarItemCard(adkar: Adkar, onIncrement: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val progress = if (adkar.repeatCount > 0) adkar.currentCount.toFloat() / adkar.repeatCount else 0f

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Arabic text
            Text(
                adkar.arabicText,
                fontSize = 22.sp,
                color = AsSabilColors.Espresso,
                fontWeight = FontWeight.Medium,
                lineHeight = 42.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = AsSabilColors.Divider)
            Spacer(Modifier.height(10.dp))

            // Translation
            Text(
                adkar.frenchTranslation,
                fontSize = 13.sp,
                color = AsSabilColors.TextMedium,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(16.dp))

            // Progress + counter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AsSabilColors.Caramel,
                    trackColor = AsSabilColors.SandStorm
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "${adkar.currentCount}/${adkar.repeatCount}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AsSabilColors.Caramel
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onIncrement()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (adkar.currentCount >= adkar.repeatCount)
                        AsSabilColors.Leafy else AsSabilColors.Caramel
                ),
                enabled = adkar.currentCount < adkar.repeatCount
            ) {
                Text(
                    if (adkar.currentCount >= adkar.repeatCount) "✓ Complété" else "Compter  +1",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Demo data for AdkarDetailScreen when Room is empty
private fun getDemoAdkar(sectionId: String): List<Adkar> = listOf(
    Adkar(1, sectionId, "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ", "Asbahna wa asbahal mulku lillah",
        "Nous voici au matin, et le règne appartient à Allah.", "We have reached morning, and at this morning, the sovereignty belongs to Allah.", 1, 0, false, ""),
    Adkar(2, sectionId, "سُبْحَانَ اللهِ وَبِحَمْدِهِ", "Subhanallahi wa bihamdih",
        "Gloire à Allah et louange à Lui.", "Glory is to Allah and praise is to Him.", 100, 0, false, "Bukhari"),
    Adkar(3, sectionId, "أَسْتَغْفِرُ اللهَ", "Astaghfirullah",
        "Je demande pardon à Allah.", "I seek Allah's forgiveness.", 3, 0, false, "Muslim"),
)
