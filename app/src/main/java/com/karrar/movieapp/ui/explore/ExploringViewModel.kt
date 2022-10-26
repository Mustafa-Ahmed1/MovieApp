package com.karrar.movieapp.ui.explore

import androidx.lifecycle.*
import com.karrar.movieapp.domain.explorUsecase.GetTrendyMovieUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.utilities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExploringViewModel @Inject constructor(
    private val getTrendyMovieUseCase: GetTrendyMovieUseCase,
) :BaseViewModel(), TrendInteractionListener {

    private val _uiState = MutableStateFlow(ExploreUIState())
    val uiState: StateFlow<ExploreUIState> = _uiState

    private val _clickSearchEvent = MutableLiveData<Event<Boolean>>()
    var clickSearchEvent = _clickSearchEvent.toLiveData()

    private val _clickMoviesEvent = MutableLiveData<Event<Boolean>>()
    var clickMoviesEvent = _clickMoviesEvent.toLiveData()

    private val _clickTVShowEvent = MutableLiveData<Event<Boolean>>()
    var clickTVShowEvent = _clickTVShowEvent.toLiveData()

    private val _clickActorsEvent = MutableLiveData<Event<Boolean>>()
    var clickActorsEvent = _clickActorsEvent.toLiveData()

    private val _clickTrendEvent = MutableLiveData<Event<Int>>()
    var clickTrendEvent = _clickTrendEvent.toLiveData()

    val mediaType = MutableStateFlow("")


    init {
        getData()
    }

    override fun getData() {
        _uiState.update { it.copy(isLoading = true) }
        wrapWithState({
            val result = getTrendyMovieUseCase()
            _uiState.update { it.copy(isLoading = false) }
            _uiState.update { it.copy(trendyMovie = result.map { it.toTrendyMedia() }) }
        }){
            _uiState.update {
                it.copy(errors = emptyList())
            }
        }
    }

    override fun onClickTrend(trendID: Int, trendType: String) {
        _clickTrendEvent.postValue(Event(trendID))
        viewModelScope.launch { mediaType.emit(trendType) }
    }

    fun onClickSearch() {
        _clickSearchEvent.postEvent(true)
    }

    fun onClickMovies() {
        _clickMoviesEvent.postEvent(true)
    }

    fun onClickTVShow() {
        _clickTVShowEvent.postEvent(true)
    }

    fun onClickActors() {
        _clickActorsEvent.postEvent(true)
    }

}