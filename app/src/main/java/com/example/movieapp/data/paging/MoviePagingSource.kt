package com.example.movieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.data.mapper.toDomain
import com.example.movieapp.data.remote.TmdbApi
import com.example.movieapp.domain.model.Movie
import java.io.IOException
import retrofit2.HttpException
import java.util.Locale
import com.example.movieapp.ui.feature.search.SortOption

class MoviePagingSource(
    private val api: TmdbApi,
    private val query: String,
    private val year: String?,
    private val sortOption: SortOption
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        val deviceLanguage = Locale.getDefault().language
        return try {
            val response = api.searchMovies(
                query = query,
                page = page,
                language = deviceLanguage,
                year = year
            )

            var movies = response.results.map { it.toDomain() }

            movies = when (sortOption) {
                SortOption.A_Z -> movies.sortedBy { it.title }
                SortOption.Z_A -> movies.sortedByDescending { it.title }
                SortOption.NEWEST -> movies.sortedByDescending { it.releaseDate }
                SortOption.RATING -> movies.sortedByDescending { it.voteAverage }
                SortOption.DEFAULT -> movies
            }

            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (movies.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}