package com.rafaelfelipeac.marvelapp.features.details.data

import com.rafaelfelipeac.marvelapp.features.details.data.model.MarvelCharacterDetailDto
import com.rafaelfelipeac.marvelapp.features.details.data.model.MarvelDetailInfoDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DetailsApi {

    @GET("characters/{characterId}")
    suspend fun getDetails(
        @Path("characterId") characterId: Long,
        @Query("apikey") apiKey: String,
        @Query("hash") hash: String,
        @Query("ts") ts: Long
    ): MarvelCharacterDetailDto

    @GET("characters/{characterId}/comics")
    suspend fun getDetailsComics(
        @Path("characterId") characterId: Long,
        @Query("apikey") apiKey: String,
        @Query("hash") hash: String,
        @Query("ts") ts: Long,
        @Query("offset") offset: Int
    ): MarvelDetailInfoDto

    @GET("characters/{characterId}/series")
    suspend fun getDetailsSeries(
        @Path("characterId") characterId: Long,
        @Query("apikey") apiKey: String,
        @Query("hash") hash: String,
        @Query("ts") ts: Long,
        @Query("offset") offset: Int
    ): MarvelDetailInfoDto
}
