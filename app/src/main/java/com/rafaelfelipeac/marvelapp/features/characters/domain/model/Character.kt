package com.rafaelfelipeac.marvelapp.features.characters.domain.model

data class Character(
    val name: String,
    val stars: Int,
    val forks: Int,
    val owner: Owner
)