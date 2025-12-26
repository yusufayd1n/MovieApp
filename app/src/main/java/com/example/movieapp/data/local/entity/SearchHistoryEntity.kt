package com.example.movieapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String, // query benzersiz anahtar olsun
    val timestamp: Long = System.currentTimeMillis()
)