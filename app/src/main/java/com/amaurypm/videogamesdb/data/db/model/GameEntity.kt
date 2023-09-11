package com.amaurypm.videogamesdb.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amaurypm.videogamesdb.util.Constants

/**
 * Creado por Amaury Perea Matsumura el 26/08/23
 */

@Entity(tableName = Constants.DATABASE_GAME_TABLE)
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "movie_id")
    val id: Long = 0,

    @ColumnInfo(name = "movie_title")
    var title: String,

    @ColumnInfo(name = "movie_date")
    var date: String,

    @ColumnInfo(name = "movie_director")
    var director: String,

    @ColumnInfo(name = "movie_genre")
    var genre: String
)
