package com.amaurypm.videogamesdb.data

import com.amaurypm.videogamesdb.data.db.GameDao
import com.amaurypm.videogamesdb.data.db.model.GameEntity

/**
 * Creado por Amaury Perea Matsumura el 26/08/23
 */
class GameRepository(private val gameDao: GameDao) {

    suspend fun insertGame(game: GameEntity){
        gameDao.insertGame(game)
    }

    suspend fun insertGame(title: String, date: String, director: String, genre: String, ){
        gameDao.insertGame(GameEntity(title = title, date = date, director = director, genre = genre))
    }

    suspend fun getAllGames(): List<GameEntity> = gameDao.getAllGames()

    suspend fun updateGame(game: GameEntity){
        gameDao.updateGame(game)
    }

    suspend fun deleteGame(game: GameEntity){
        gameDao.deleteGame(game)
    }


}