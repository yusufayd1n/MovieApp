package com.example.movieapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_lists")
data class FavoriteListEntity(
    @PrimaryKey(autoGenerate = true)
    val listId: Long = 0,
    val listName: String
)