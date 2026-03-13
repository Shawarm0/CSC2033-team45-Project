package com.team45.mysustainablecity.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.team45.mysustainablecity.reps.PostRep
import com.team45.mysustainablecity.utils.LocationCluster
import com.team45.mysustainablecity.utils.MapLocation
import com.team45.mysustainablecity.utils.Tag
import com.team45.mysustainablecity.utils.clusterLocations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MapViewModel(val postRep: PostRep) : ViewModel() {

    // -------------------------------------------------------------------------
    // Search
    // -------------------------------------------------------------------------

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _isSearchCommitted = MutableStateFlow(false)
    val isSearchCommitted: StateFlow<Boolean> = _isSearchCommitted.asStateFlow()

    val isSearching: Boolean get() = _searchText.value.isNotBlank()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        // If the user edits after committing, un-commit so filters stay ignored while typing
        if (_isSearchCommitted.value) _isSearchCommitted.value = false
    }

    init {
        viewModelScope.launch {
            postRep.sessionFlow.collect { session ->
                if (session != null) {
                    try {
                        postRep.fetchAllPosts(null, null)
                        postRep.initialisePostsChannel()
                    } catch (e: Exception) {
                        Log.e("MapViewModel", "Error fetching posts", e)
                    }
                }
            }
        }
    }

    fun onSearchCommit() {
        _isSearchCommitted.value = true
    }

    fun clearSearch() {
        _searchText.value = ""
        _isSearchCommitted.value = false
        _selectedCluster.value = null
    }

    // -------------------------------------------------------------------------
    // Filters
    // -------------------------------------------------------------------------

    private val _activeFilters = MutableStateFlow<Set<Tag>>(emptySet())
    val activeFilters: StateFlow<Set<Tag>> = _activeFilters.asStateFlow()

    fun toggleFilter(tag: Tag) {
        _activeFilters.value = if (tag in _activeFilters.value) {
            _activeFilters.value - tag
        } else {
            _activeFilters.value + tag
        }
        _selectedCluster.value = null
    }

    // -------------------------------------------------------------------------
    // Visible locations — derived from search + filters
    // -------------------------------------------------------------------------

    /**
     * Returns the filtered/searched list of [MapLocation]s to render on the map.
     * Call this from the composable inside a `derivedStateOf` or `collectAsState`.
     */
    fun visibleLocations(
        searchText: String = _searchText.value,
        activeFilters: Set<Tag> = _activeFilters.value,
    ): List<MapLocation> {
        val all = postRep.liveMapLocations.value  // ← was allMapLocations
        return when {
            searchText.isNotBlank() -> all.filter {
                it.name.contains(searchText.trim(), ignoreCase = true)
            }
            activeFilters.isEmpty() -> all
            else -> all.filter { location ->
                activeFilters.all { it in location.tags }
            }
        }
    }

    /**
     * The first result when searching — used to animate the camera.
     */
    fun topSearchResult(
        searchText: String = _searchText.value,
        locations: List<MapLocation> = visibleLocations(),
    ): MapLocation? = if (searchText.isNotBlank()) locations.firstOrNull() else null

    // -------------------------------------------------------------------------
    // Clustering
    // -------------------------------------------------------------------------

    /**
     * Builds clusters from [locations] at the given [zoom] level.
     * Call inside `derivedStateOf` in the composable so it only recomputes on zoom/location changes.
     */
    fun buildClusters(locations: List<MapLocation>, zoom: Float): List<LocationCluster> {
        val thresholdMeters = when {
            zoom >= 19f  -> 10.0
            zoom >= 18f  -> 20.0
            zoom >= 17f  -> 35.0
            zoom >= 16.5f -> 55.0
            zoom >= 16f  -> 80.0
            zoom >= 15.5f -> 110.0
            zoom >= 15f  -> 150.0
            zoom >= 14.5f -> 200.0
            zoom >= 14f  -> 260.0
            zoom >= 13.5f -> 340.0
            zoom >= 13f  -> 430.0
            zoom >= 12.5f -> 550.0
            zoom >= 12f  -> 700.0
            zoom >= 11.5f -> 900.0
            zoom >= 11f  -> 1150.0
            zoom >= 10.5f -> 1500.0
            zoom >= 10f  -> 2000.0
            else         -> 3000.0
        }
        return clusterLocations(locations, thresholdMeters)
    }

    // -------------------------------------------------------------------------
    // Selected cluster (bottom sheet)
    // -------------------------------------------------------------------------

    private val _selectedCluster = MutableStateFlow<LocationCluster?>(null)
    val selectedCluster: StateFlow<LocationCluster?> = _selectedCluster.asStateFlow()

    fun selectCluster(cluster: LocationCluster) {
        _selectedCluster.value = cluster
    }

    fun clearSelectedCluster() {
        _selectedCluster.value = null
    }

}