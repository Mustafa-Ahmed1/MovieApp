package com.karrar.movieapp.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.karrar.movieapp.domain.models.Genre
import com.karrar.movieapp.domain.models.Media
import com.karrar.movieapp.domain.usecase.GetCategoryUseCase
import com.karrar.movieapp.domain.usecase.GetGenreUseCase
import com.karrar.movieapp.ui.UIState
import com.karrar.movieapp.ui.adapters.MediaInteractionListener
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.utilities.Constants.FIRST_CATEGORY_ID
import com.karrar.movieapp.utilities.Event
import com.karrar.movieapp.utilities.postEvent
import com.karrar.movieapp.utilities.toLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class CategoryUIState(
    val genre: List<Genre> = emptyList(),
    val media: Flow<PagingData<Media>> = emptyFlow(),
    val mediaUIState: UIState<Boolean> = UIState.Loading,
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoryUseCase: GetCategoryUseCase,
    private val getGenreUsecase: GetGenreUseCase,
    state: SavedStateHandle
) : BaseViewModel(), MediaInteractionListener, CategoryInteractionListener {

    val args = CategoryFragmentArgs.fromSavedStateHandle(state)

    private val _uiState = MutableStateFlow(CategoryUIState())
    val uiState: StateFlow<CategoryUIState> = _uiState

    private val _clickMovieEvent = MutableLiveData<Event<Int>>()
    var clickMovieEvent = _clickMovieEvent

    private val _clickRetryEvent = MutableLiveData<Event<Boolean>>()
    val clickRetryEvent = _clickRetryEvent.toLiveData()

    private val _selectedCategory = MutableLiveData(FIRST_CATEGORY_ID)
    val selectedCategory = _selectedCategory.toLiveData()

    init {
        getData()
    }

    override fun getData() {
        setAllMediaList()
        getGenre()
        _clickRetryEvent.postEvent(true)
    }

    private fun getGenre() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(genre = getGenreUsecase(args.mediaId))
            }
        }
    }

    fun setAllMediaList() {
        _uiState.update { it.copy(mediaUIState = UIState.Loading) }
        val result = getCategoryUseCase(args.mediaId, selectedCategory.value ?: FIRST_CATEGORY_ID)
        _uiState.update { it.copy(mediaUIState = UIState.Success(true), media = result) }
    }

    override fun onClickMedia(mediaId: Int) {
        _clickMovieEvent.postValue(Event(mediaId))
    }

    override fun onClickCategory(categoryId: Int) {
        _selectedCategory.postValue(categoryId)
    }

    fun setErrorUiState(loadState: LoadState) {
        when (loadState) {
            is LoadState.Error, null -> {
                _uiState.update { it.copy(mediaUIState = UIState.Error("")) }
            }
            else -> {
                _uiState.update { it.copy(mediaUIState = UIState.Success(true)) }
            }
        }
    }
}

