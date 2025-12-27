package com.example.movieapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "movie_list_cross_ref",
    primaryKeys = ["listId", "movieId"],
    foreignKeys = [
        ForeignKey(
            entity = FavoriteListEntity::class,
            parentColumns = ["listId"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["listId"]), Index(value = ["movieId"])]
)
data class MovieListCrossRef(
    val listId: Long,
    val movieId: Int,
    val addedAt: Long = System.currentTimeMillis()
)