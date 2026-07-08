package com.assabil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assabil.data.repository.*
import com.assabil.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

// ─── QURAN VIEW MODEL ─────────────────────────────────────────────
@HiltViewModel
class QuranViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val lastRead: StateFlow<ReadingProgress?> = repository.getReadingProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val filteredSurahs: StateFlow<List<Surah>> = combine(
        repository.getAllSurahs(),
        _searchQuery
    ) { surahs, query ->
        if (query.isEmpty()) surahs
        else surahs.filter {
            it.name.contains(query, true) ||
            it.arabicName.contains(query) ||
            it.id.toString() == query
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadSurahs()
    }

    fun onSearchQueryChange(q: String) { _searchQuery.value = q }

    private fun loadSurahs() = viewModelScope.launch {
        _isLoading.value = true
        repository.fetchAndCacheSurahs()
        _isLoading.value = false
    }
}

// ─── SURAH DETAIL VIEW MODEL ──────────────────────────────────────
@HiltViewModel
class SurahDetailViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _surah = MutableStateFlow<Surah?>(null)
    val surah: StateFlow<Surah?> = _surah

    val ayahs: StateFlow<List<Ayah>> = _surah.flatMapLatest { s ->
        if (s != null) repository.getAyahsBySurah(s.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _audioState = MutableStateFlow(AudioState())
    val audioState: StateFlow<AudioState> = _audioState

    private val _showTranslation = MutableStateFlow(true)
    val showTranslation: StateFlow<Boolean> = _showTranslation

    private val _showTafsir = MutableStateFlow(false)
    val showTafsir: StateFlow<Boolean> = _showTafsir

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadSurah(id: Int) = viewModelScope.launch {
        _isLoading.value = true
        _surah.value = repository.getSurahById(id)
        repository.fetchAyahsForSurah(id)
        _isLoading.value = false
    }

    fun toggleTranslation() { _showTranslation.value = !_showTranslation.value }
    fun toggleTafsir() { _showTafsir.value = !_showTafsir.value }

    fun playPause() {
        _audioState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun playAyah(ayahNum: Int) {
        _audioState.update { it.copy(isPlaying = true, currentAyah = ayahNum) }
        // TODO: trigger ExoPlayer via service
    }

    fun nextSurah() {
        val currentId = _surah.value?.id ?: return
        if (currentId < 114) loadSurah(currentId + 1)
    }

    fun prevSurah() {
        val currentId = _surah.value?.id ?: return
        if (currentId > 1) loadSurah(currentId - 1)
    }

    fun toggleBookmark(ayah: Ayah) = viewModelScope.launch {
        val surahName = _surah.value?.name ?: ""
        repository.toggleBookmark(ayah.surahId, ayah.numberInSurah, surahName, ayah.arabicText)
    }

    fun shareAyah(ayah: Ayah) {
        // TODO: Android share sheet
    }
}

// ─── HADITH / ADKAR VIEW MODEL ────────────────────────────────────
@HiltViewModel
class HadithAdkarViewModel @Inject constructor(
    private val hadithRepo: HadithRepository,
    private val adkarRepo: AdkarRepository
) : ViewModel() {

    val dailyHadith: StateFlow<Hadith?> = flow {
        emit(hadithRepo.getDailyHadith())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val nawawiHadiths: StateFlow<List<Hadith>> = hadithRepo.getNawawi40()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _tasbihCount = MutableStateFlow(0)
    val tasbihCount: StateFlow<Int> = _tasbihCount

    private val _tasbihTarget = MutableStateFlow(33)
    val tasbihTarget: StateFlow<Int> = _tasbihTarget

    fun incrementTasbih() {
        if (_tasbihCount.value < _tasbihTarget.value) {
            _tasbihCount.update { it + 1 }
        }
    }

    fun resetTasbih() { _tasbihCount.value = 0 }

    fun setTasbihTarget(target: Int) {
        _tasbihTarget.value = target
        _tasbihCount.value = 0
    }

    fun toggleHadithFavorite(hadith: Hadith) = viewModelScope.launch {
        hadithRepo.toggleFavorite(hadith)
    }

    fun getAdkarForSection(sectionId: String): Flow<List<Adkar>> =
        adkarRepo.getAdkarBySection(sectionId)

    fun incrementAdkarCount(adkar: Adkar) = viewModelScope.launch {
        adkarRepo.incrementCount(adkar)
    }
}

// ─── KIBLA VIEW MODEL ─────────────────────────────────────────────
@HiltViewModel
class KiblaViewModel @Inject constructor(
    // LocationRepository injected here in full implementation
) : ViewModel() {

    private val _kiblaInfo = MutableStateFlow<KiblaInfo?>(null)
    val kiblaInfo: StateFlow<KiblaInfo?> = _kiblaInfo

    private val _compassBearing = MutableStateFlow(0.0)
    val compassBearing: StateFlow<Double> = _compassBearing

    // Kaaba coordinates
    private val KAABA_LAT = 21.4225
    private val KAABA_LNG = 39.8262

    fun updateBearing(azimuth: Float) {
        _compassBearing.value = azimuth.toDouble()
    }

    fun fetchLocation() {
        // TODO: use FusedLocationProviderClient
        // For demonstration, using a dummy location (Paris)
        updateKiblaInfo(48.8566, 2.3522)
    }

    fun updateKiblaInfo(lat: Double, lng: Double) {
        val qibla = calculateQiblaDirection(lat, lng)
        val distance = calculateDistance(lat, lng, KAABA_LAT, KAABA_LNG)
        _kiblaInfo.value = KiblaInfo(
            qiblaDirection = qibla,
            userLatitude = lat,
            userLongitude = lng,
            distanceToKaaba = distance
        )
    }

    private fun calculateQiblaDirection(lat: Double, lng: Double): Double {
        val phi1 = Math.toRadians(lat)
        val phi2 = Math.toRadians(KAABA_LAT)
        val deltaLambda = Math.toRadians(KAABA_LNG - lng)
        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        return (Math.toDegrees(atan2(y, x)) + 360) % 360
    }

    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return R * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
