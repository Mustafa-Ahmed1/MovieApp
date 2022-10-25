package com.karrar.movieapp.domain.usecase

import com.karrar.movieapp.data.remote.response.genre.GenreDto
import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.data.repository.SeriesRepository
import com.karrar.movieapp.domain.mappers.GenreMapper
import com.karrar.movieapp.domain.mappers.ListMapper
import com.karrar.movieapp.domain.models.Genre
import com.karrar.movieapp.utilities.Constants
import javax.inject.Inject

class GetGenreListUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val seriesRepository: SeriesRepository,
    private val genreMapper: GenreMapper
) {

    suspend operator fun invoke(mediaId: Int): List<Genre> {
        val genre = when (mediaId) {
            Constants.MOVIE_CATEGORIES_ID -> {
                mapGenre(movieRepository.getMovieGenreList2())
            }
            else -> {
                mapGenre(seriesRepository.getTVShowsGenreList2())
            }
        }
        return addAllGenre(genre)
    }

    private fun addAllGenre(genre: List<Genre>): List<Genre> {
        val allGenre = mutableListOf<Genre>()
        allGenre.add(Genre(Constants.FIRST_CATEGORY_ID, Constants.ALL))
        allGenre.addAll(genre)
        return allGenre.toList()
    }

    private fun mapGenre(genre: List<GenreDto>?): List<Genre> {
        return ListMapper(genreMapper).mapList(genre)
    }
}